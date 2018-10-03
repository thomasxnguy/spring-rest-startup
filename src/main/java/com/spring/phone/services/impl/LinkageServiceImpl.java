package com.spring.phone.services.impl;

import com.spring.phone.services.ErrorMessages;
import com.spring.phone.services.LinkageService;
import com.spring.phone.repository.LinkageRepository;
import com.spring.phone.repository.jpa.LinkageData;
import com.spring.utils.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.math.BigInteger.TEN;
import static java.util.Objects.isNull;

@Slf4j
@Service
class LinkageServiceImpl implements LinkageService {

    private final LinkageRepository linkageRepository;

    @Autowired
    LinkageServiceImpl(LinkageRepository linkageRepository) {
        this.linkageRepository = linkageRepository;
    }

    @Override
    public Optional<LinkageData> getLinkageDataByPhoneNumber(String phoneNumber) {
        if (!isNumbersOnly(phoneNumber)) {
            throw new BadRequestException(MessageFormat.format("{0}, but was [{1}]", ErrorMessages.NOT_NUMERICAL_PHONE_NUMBER, phoneNumber));
        }
        try {
            return linkageRepository.findByPhoneNumber(phoneNumber);
        } catch (Exception ex) {
            if (ExceptionUtils.hasCause(ex, IncorrectResultSizeDataAccessException.class)) {
                throw new RuntimeException(ErrorMessages.NOT_UNIQUE_RESULT);
            }
            throw ex;
        }
    }

    private static final BigInteger PARTITION_KEY_COUNT = BigInteger.valueOf(12);

    @Override
    public Optional<LinkageData> getLinkageDataById(BigInteger id) {
        if (isNull(id)) {
            throw new BadRequestException(ErrorMessages.NON_NUMERICAL_ID);
        }
        try {
            //In case of sharding DB
            int partitionKey = id.divide(TEN).mod(PARTITION_KEY_COUNT).intValue();
            return linkageRepository.findByPkIdAndPkPartitionKey(id, partitionKey);
        } catch (Exception ex) {
            if (ExceptionUtils.hasCause(ex, IncorrectResultSizeDataAccessException.class)) {
                throw new RuntimeException(ErrorMessages.NOT_UNIQUE_RESULT);
            }
            throw ex;
        }
    }

    @Override
    public Optional<LinkageData> getLinkageDataByHash(String phoneHash) {
        if (StringUtils.isEmpty(phoneHash)) {
            throw new BadRequestException(ErrorMessages.EMPTY_HASH_REQUEST);
        }
        try {
            return linkageRepository.findByPhoneHash(phoneHash);
        } catch (Exception ex) {
            if (ExceptionUtils.hasCause(ex, IncorrectResultSizeDataAccessException.class)) {
                throw new RuntimeException(ErrorMessages.NOT_UNIQUE_RESULT);
            }
            throw ex;
        }
    }

    @Override
    public List<LinkageData> getLinkageDataByHashBulk(Set<String> hashes) {
        if (hashes.size() > 1000) {
            throw new BadRequestException("Exceeded limit for number of requested hashes, max is 1000");
        }
        return linkageRepository.findByPhoneHashIn(hashes);
    }
}
