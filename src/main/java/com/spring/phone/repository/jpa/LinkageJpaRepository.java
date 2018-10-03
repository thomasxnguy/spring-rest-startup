package com.spring.phone.repository.jpa;

import com.spring.phone.repository.LinkageRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Qualifier("jpa")
@Repository
interface LinkageJpaRepository extends LinkageRepository, JpaRepository<LinkageData, LinkageData.LinkageDataPK> {

}
