package com.spring.phone.controller;

import com.spring.phone.repository.jpa.LinkageData;
import com.spring.phone.services.LinkageService;
import com.spring.utils.exception.BadRequestException;
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

import static com.spring.phone.services.ErrorMessages.NOT_NUMERICAL_PHONE_NUMBER;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(value = IdByPhoneLinkageController.class, secure = false)
public class IdByPhoneLinkageControllerTest {

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
    public void getId_CorrectPhone_OKId() throws Exception {
        when(mockLinkage.getId()).thenReturn(BigInteger.TEN);
        when(linkageService.getLinkageDataByPhoneNumber("123456789")).thenReturn(Optional.of(mockLinkage));
        mockMvc.perform(
                get("/phones/123456789/id")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("status", is("OK")))
                .andExpect(jsonPath("response.id", is(10)));
    }

    @Test
    public void getId_CorrectPhoneNoLinkage_NotFound() throws Exception {
        when(linkageService.getLinkageDataByPhoneNumber("123456789")).thenReturn(Optional.empty());
        mockMvc.perform(
                get("/phones/123456789/id")
        )
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("status", is("ERROR")))
                .andExpect(jsonPath("reason", is("No linkage found")));
    }

    @Test
    public void getId_NotCorrectPhone_BadRequest() throws Exception {
        when(linkageService.getLinkageDataByPhoneNumber("abcdef")).thenThrow(new BadRequestException(NOT_NUMERICAL_PHONE_NUMBER));
        mockMvc.perform(
                get("/phones/abcdef/id")
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("status", is("ERROR")))
                .andExpect(jsonPath("reason", is("Canonical phone number should be only numeric")));
    }

    @Test
    public void getId_unhandledException_500FromControllerAdvice() throws Exception {
        when(linkageService.getLinkageDataByPhoneNumber(any())).thenThrow(new IllegalStateException("Some error message"));
        mockMvc.perform(
                get("/phones/12345678/id")
        )
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("status", is("ERROR")))
                .andExpect(jsonPath("reason", is("Some error message")));

    }


}