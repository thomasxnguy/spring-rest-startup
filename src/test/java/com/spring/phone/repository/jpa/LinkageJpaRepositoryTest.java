package com.spring.phone.repository.jpa;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.math.BigInteger;
import java.util.Optional;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static com.googlecode.catchexception.apis.CatchExceptionHamcrestMatchers.hasMessage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class})
public class LinkageJpaRepositoryTest {

    @Autowired
    LinkageJpaRepository repository;

    @Test
    @DatabaseSetup("sample-data-unique-linkage.xml")
    public void findById_ExistingUniqueLinkage_OneFound() {
        Optional<LinkageData> linkageOpt = repository.findByPkIdAndPkPartitionKey(BigInteger.valueOf(-10L), -1);
        assertThat(linkageOpt.isPresent(), is(true));
        LinkageData linkage = linkageOpt.get();
        assertThat(linkage.getPhoneNumber(), is("1234567890"));
        assertThat(linkage.getId(), is(BigInteger.valueOf(-10L)));
        assertThat(linkage.getPhoneHash(), is("2dbed70947d2c74ec198f882ad40e9b312a4df3a1fe8dab94dba05123e23d3ca"));
    }

    @Test
    @DatabaseSetup("sample-data-unique-linkage.xml")
    public void findById_NotExisting_NotFound() {
        Optional<LinkageData> linkageOpt = repository.findByPkIdAndPkPartitionKey(BigInteger.valueOf(-1L), 0);
        assertThat(linkageOpt.isPresent(), is(false));
    }

    @Test
    @DatabaseSetup("sample-data-unique-linkage.xml")
    public void findByPhoneNumber_ExistingUniqueLinkage_OneFound() {

        Optional<LinkageData> linkageOpt = repository.findByPhoneNumber("1234567890");
        assertThat(linkageOpt.isPresent(), is(true));
        LinkageData linkage = linkageOpt.get();
        assertThat(linkage.getId(), is(BigInteger.valueOf(-10L)));
        assertThat(linkage.getPhoneNumber(), is("1234567890"));
        assertThat(linkage.getPhoneHash(), is("2dbed70947d2c74ec198f882ad40e9b312a4df3a1fe8dab94dba05123e23d3ca"));

    }

    @Test
    @DatabaseSetup("sample-data-unique-linkage.xml")
    public void findByPhoneNumber_NotExisting_NotFound() {
        Optional<LinkageData> linkageOpt = repository.findByPhoneNumber("5555555555");
        assertThat(linkageOpt.isPresent(), is(false));
    }

    @Test
    @DatabaseSetup("sample-data-unique-linkage.xml")
    public void findByPhoneHash_ExistingUniqueLinkage_OneFound() {

        Optional<LinkageData> linkageOpt = repository.findByPhoneHash("2dbed70947d2c74ec198f882ad40e9b312a4df3a1fe8dab94dba05123e23d3ca");
        assertThat(linkageOpt.isPresent(), is(true));
        LinkageData linkage = linkageOpt.get();
        assertThat(linkage.getId(), is(BigInteger.valueOf(-10L)));
        assertThat(linkage.getPhoneNumber(), is("1234567890"));
        assertThat(linkage.getPhoneHash(), is("2dbed70947d2c74ec198f882ad40e9b312a4df3a1fe8dab94dba05123e23d3ca"));

    }

    @Test
    @DatabaseSetup("sample-data-unique-linkage.xml")
    public void findByPhoneHash_NotExisting_NotFound() {
        Optional<LinkageData> linkageOpt = repository.findByPhoneHash("xyz");
        assertThat(linkageOpt.isPresent(), is(false));
    }

    @Test
    @DatabaseSetup("sample-data-duplicated-phone.xml")
    public void findByPhoneNumber_ExistingNotUniqueLinkage_ExceptionThrown() {

        catchException(repository).findByPhoneNumber("1234567890");

        assertThat(caughtException(),
                allOf(
                        instanceOf(IncorrectResultSizeDataAccessException.class),
                        hasMessage("result returns more than one elements; " +
                                "nested exception is javax.persistence.NonUniqueResultException: " +
                                "result returns more than one elements")
                )
        );

    }

}