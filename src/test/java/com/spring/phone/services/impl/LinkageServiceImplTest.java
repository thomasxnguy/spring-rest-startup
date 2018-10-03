package com.spring.phone.services.impl;

import com.spring.phone.repository.LinkageRepository;
import com.spring.phone.repository.jpa.LinkageData;
import com.spring.phone.services.ErrorMessages;
import com.spring.utils.exception.BadRequestException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import java.math.BigInteger;
import java.util.Optional;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static com.googlecode.catchexception.apis.CatchExceptionHamcrestMatchers.hasMessage;
import static com.googlecode.catchexception.apis.CatchExceptionHamcrestMatchers.hasNoCause;
import static com.spring.phone.services.ErrorMessages.NOT_UNIQUE_RESULT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LinkageServiceImplTest {

    private LinkageRepository repository;
    private LinkageServiceImpl linkageService;

    @Before
    public void setUp() {
        repository = mock(LinkageRepository.class);
        linkageService = new LinkageServiceImpl(repository);
    }

    @Test
    public void getIdByPhoneNumber_NotNumericalPhoneNumber_ExceptionThrown() throws Exception {
        catchException(linkageService).getLinkageDataByPhoneNumber("ABCD");
        assertThat(caughtException(),
                allOf(
                        instanceOf(BadRequestException.class),
                        hasMessage("Canonical phone number should be only numeric, but was [ABCD]"),
                        hasNoCause()
                )
        );
    }

    @Test
    public void getIdByPhoneNumber_NullPhoneNumber_ExceptionThrown() throws Exception {
        catchException(linkageService).getLinkageDataByPhoneNumber(null);
        assertThat(caughtException(),
                allOf(
                        instanceOf(BadRequestException.class),
                        hasMessage("Canonical phone number should be only numeric, but was [null]"),
                        hasNoCause()
                )
        );
    }

    @Test
    public void getIdByPhoneNumber_NumericalPhoneNumberNonUniqueLinkage_ExceptionThrown() throws Exception {
        when(repository.findByPhoneNumber(any(String.class))).thenThrow(IncorrectResultSizeDataAccessException.class);
        catchException(linkageService).getLinkageDataByPhoneNumber("12345678");
        assertThat(caughtException(),
                allOf(
                        instanceOf(RuntimeException.class),
                        hasMessage(NOT_UNIQUE_RESULT),
                        hasNoCause()
                )
        );
    }

    @Test
    public void getIdByPhoneNumber_NumericalPhoneNumberUniqueLinkage_IdReturned() throws Exception {
        Optional<LinkageData> value = mockData();
        when(repository.findByPhoneNumber(any(String.class))).thenReturn(value);
        Optional<BigInteger> IdOpt = linkageService.getLinkageDataByPhoneNumber("1234567890").map(LinkageData::getId);
        assertThat(IdOpt.isPresent(), is(true));
        assertThat(IdOpt.get(), is(BigInteger.TEN));
    }

    @Test
    public void getIdByPhoneNumber_NumericalPhoneNumberNoLinkage_OptionalEmpty() throws Exception {
        when(repository.findByPhoneNumber(any(String.class))).thenReturn(Optional.empty());
        Optional<BigInteger> IdOpt = linkageService.getLinkageDataByPhoneNumber("1234567890").map(LinkageData::getId);;
        assertThat(IdOpt.isPresent(), is(false));
    }

    @Test
    public void getPhoneById_NullId_ExceptionThrown() throws Exception {
        catchException(linkageService).getLinkageDataById(null);
        assertThat(caughtException(),
                allOf(
                        instanceOf(BadRequestException.class),
                        hasMessage(ErrorMessages.NON_NUMERICAL_ID),
                        hasNoCause()
                )
        );
    }

    @Test
    public void getPhoneById_NumericalPhoneNumberNonUniqueLinkage_ExceptionThrown() throws Exception {
        when(repository.findByPkIdAndPkPartitionKey(any(BigInteger.class), any(Integer.class))).thenThrow(IncorrectResultSizeDataAccessException.class);
        catchException(linkageService).getLinkageDataById(BigInteger.TEN);
        assertThat(caughtException(),
                allOf(
                        instanceOf(RuntimeException.class),
                        hasMessage(NOT_UNIQUE_RESULT),
                        hasNoCause()
                )
        );
    }

    @Test
    public void getPhoneById_IdUniqueLinkage_PhoneReturned() throws Exception {
        Optional<LinkageData> value = mockData();
        when(repository.findByPkIdAndPkPartitionKey(any(BigInteger.class), any(Integer.class))).thenReturn(value);
        Optional<String> IdOpt = linkageService.getLinkageDataById(BigInteger.TEN).map(LinkageData::getPhoneNumber);;
        assertThat(IdOpt.isPresent(), is(true));
        assertThat(IdOpt.get(), is("1234567890"));
    }

    @Test
    public void getPhoneById_IdNoLinkage_OptionalEmpty() throws Exception {
        when(repository.findByPkIdAndPkPartitionKey(any(BigInteger.class), any(Integer.class))).thenReturn(Optional.empty());
        Optional<String> IdOpt = linkageService.getLinkageDataById(BigInteger.TEN).map(LinkageData::getPhoneNumber);;
        assertThat(IdOpt.isPresent(), is(false));
    }

    @Test
    public void test_isNumbersOnly() {
        assertThat(linkageService.isNumbersOnly("1234567890"), is(true));
        assertThat(linkageService.isNumbersOnly("ABCDEF"), is(false));
        assertThat(linkageService.isNumbersOnly(" 12345678"), is(false)); // leading space
        assertThat(linkageService.isNumbersOnly("1235678 "), is(false)); // trailing space
        assertThat(linkageService.isNumbersOnly("123s5678 "), is(false));
        assertThat(linkageService.isNumbersOnly("123 5678 "), is(false)); // space in between
        assertThat(linkageService.isNumbersOnly("123*5678 "), is(false)); // space in between
    }


    private Optional<LinkageData> mockData() {
        LinkageData data = mock(LinkageData.class);
        when(data.getPhoneNumber()).thenReturn("1234567890");
        when(data.getId()).thenReturn(BigInteger.TEN);
        when(data.getPartitionKey()).thenReturn(1); // (10 / 10 ) % 12 = 1
        return Optional.of(data);
    }

}