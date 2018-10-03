package com.spring.phone.services;

import com.spring.phone.repository.jpa.LinkageData;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

public interface LinkageService {

    Optional<LinkageData> getLinkageDataByPhoneNumber(String phoneNumber);

    Optional<LinkageData> getLinkageDataById(BigInteger id);

    Optional<LinkageData> getLinkageDataByHash(String phoneHash);

    List<LinkageData> getLinkageDataByHashBulk(Set<String> hashes);

    default boolean isNumbersOnly(String string) {
        Pattern digits_only = Pattern.compile("^\\d+$");
        return StringUtils.isNotEmpty(string) && digits_only.matcher(string).find();
    }

}
