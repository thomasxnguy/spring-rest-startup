package com.spring.phone.controller;

import com.spring.phone.repository.jpa.LinkageData;
import com.spring.phone.services.LinkageService;
import com.spring.utils.crypto.JweEncrypter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigInteger;
import java.util.Date;
import java.util.Optional;

import static com.spring.phone.services.ErrorMessages.NO_LINKAGE_FOUND;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = PhoneByIdLinkageController.class, secure = false)
public class PhoneByIdLinkageControllerTest {

    @MockBean
    private LinkageService linkageService;

    @MockBean
    private JweEncrypter jweEncrypter;

    @Autowired
    private MockMvc mockMvc;

    private LinkageData mockLinkage;

    @Before
    public void setUp() {
        mockLinkage = mock(LinkageData.class);
    }

    @Test
    public void getPhoneById_NumberId_OKwithPhone() throws Exception {
        when(mockLinkage.getPhoneNumber()).thenReturn("123456789");
        when(linkageService.getLinkageDataById(BigInteger.TEN)).thenReturn(Optional.of(mockLinkage));
        final Date expirationDate = new Date();
        when(jweEncrypter.encrypt(anyString())).thenReturn(new JweEncrypter.Token("encrypted:phone", expirationDate));
        mockMvc.perform(
                get("/id/{id}", BigInteger.TEN.toString())
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("status", is("OK")))
                .andExpect(jsonPath("response.phone", is("123456789")))
                .andExpect(jsonPath("response.token", is("encrypted:phone")))
                .andExpect(jsonPath("response.expireEpochMills", is(expirationDate.getTime())));
    }

    @Test
    public void getPhoneById_NumberId_OKwithPhoneAndHash() throws Exception {
        when(mockLinkage.getPhoneNumber()).thenReturn("123456789");
        when(mockLinkage.getPhoneHash()).thenReturn("2dbed70947d2c74ec198f882ad40e9b312a4df3a1fe8dab94dba05123e23d3ca");
        when(linkageService.getLinkageDataById(BigInteger.TEN)).thenReturn(Optional.of(mockLinkage));
        final Date expirationDate = new Date();
        when(jweEncrypter.encrypt(anyString())).thenReturn(new JweEncrypter.Token("encrypted:phone", expirationDate));
        mockMvc.perform(
                get("/id/{id}", BigInteger.TEN.toString())
                        .param("hash", "")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("status", is("OK")))
                .andExpect(jsonPath("response.canonicalPhoneHash", is("2dbed70947d2c74ec198f882ad40e9b312a4df3a1fe8dab94dba05123e23d3ca")))
                .andExpect(jsonPath("response.phone", is("123456789")))
                .andExpect(jsonPath("response.token", is("encrypted:phone")))
                .andExpect(jsonPath("response.expireEpochMills", is(expirationDate.getTime())));
    }

    @Test
    public void getPhoneById_NumberIdWithNoLinkage_notFound() throws Exception {
        when(linkageService.getLinkageDataById(BigInteger.TEN)).thenReturn(Optional.empty());
        mockMvc.perform(
                get("/id/{id}", BigInteger.TEN.toString())
        )
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("status", is("ERROR")))
                .andExpect(jsonPath("reason", is(NO_LINKAGE_FOUND)));
        verify(jweEncrypter, never()).encrypt(anyString());
    }

    @Test
    public void getPhoneById_NotNumberId_badRequest() throws Exception {
        mockMvc.perform(
                get("/id/{id}", "ABCDEF")
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("status", is("ERROR")))
                .andExpect(jsonPath("reason", is("Wrong type of argument, id should be BigInteger, but was [ABCDEF]")));
        verify(jweEncrypter, never()).encrypt(anyString());
    }

    @Test
    public void getPhoneById_noArgumentsInPath_badRequest() throws Exception {
        when(mockLinkage.getPhoneNumber()).thenReturn("123456789");
        when(linkageService.getLinkageDataById(BigInteger.TEN)).thenReturn(Optional.of(mockLinkage));
        mockMvc.perform(
                get("/id/", BigInteger.TEN.toString())
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("status", is("ERROR")))
                .andExpect(jsonPath("reason", is("No handler found for GET /id/")));
        verify(jweEncrypter, never()).encrypt(anyString());
    }

    @Test
    public void getPhone_unhandledException_500FromControllerAdvice() throws Exception {
        when(linkageService.getLinkageDataById(any())).thenThrow(new IllegalStateException("Some error message"));
        mockMvc.perform(
                get("/id/{id}", BigInteger.TEN.toString())
        )
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("status", is("ERROR")))
                .andExpect(jsonPath("reason", is("Some error message")));
        verify(jweEncrypter, never()).encrypt(anyString());
    }

}