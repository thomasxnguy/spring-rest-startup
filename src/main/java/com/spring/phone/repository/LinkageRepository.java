package com.spring.phone.repository;

import com.spring.phone.repository.jpa.LinkageData;
import org.springframework.data.repository.NoRepositoryBean;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@NoRepositoryBean
public interface LinkageRepository {

    Optional<LinkageData> findByPkIdAndPkPartitionKey(BigInteger id, Integer partitionKey);

    Optional<LinkageData> findByPhoneNumber(String phoneNumber);

    Optional<LinkageData> findByPhoneHash(String PhoneHash);

    List<LinkageData> findByPhoneHashIn(Set<String> hashes);
}
