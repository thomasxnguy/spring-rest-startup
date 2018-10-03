package com.spring.phone.controller;

import com.spring.phone.repository.jpa.LinkageData;
import com.spring.phone.services.LinkageService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigInteger;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = PhoneHashLookupController.class, secure = false)
public class PhoneHashLookupControllerTest {

    @MockBean
    private LinkageService linkageService;

    @Autowired
    private MockMvc mockMvc;

    private LinkageData mockLinkage;

    @Before
    public void setUp() {
        mockLinkage = mock(LinkageData.class);
    }

    @Test
    public void finedPhoneByHash() throws Exception {
        when(mockLinkage.getPhoneNumber()).thenReturn("123456789");
        when(mockLinkage.getId()).thenReturn(BigInteger.valueOf(-10));
        when(mockLinkage.getPhoneHash()).thenReturn("2dbed70947d2c74ec198f882ad40e9b312a4df3a1fe8dab94dba05123e23d3ca");
        when(linkageService.getLinkageDataByHash(anyString())).thenReturn(Optional.of(mockLinkage));
        mockMvc.perform(
                get("/hashes/{hash}", "2dbed70947d2c74ec198f882ad40e9b312a4df3a1fe8dab94dba05123e23d3ca")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("status", is("OK")))
                .andExpect(jsonPath("response.phone", is("123456789")))
                .andExpect(jsonPath("response.id", is(-10)))
                .andExpect(jsonPath("response.canonicalPhoneHash", is("2dbed70947d2c74ec198f882ad40e9b312a4df3a1fe8dab94dba05123e23d3ca")));
    }
}