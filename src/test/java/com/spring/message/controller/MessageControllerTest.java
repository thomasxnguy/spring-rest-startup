package com.spring.message.controller;

import com.spring.message.service.MessageService;
import com.spring.utils.crypto.JweEncrypter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = MessageController.class, secure = false)
public class MessageControllerTest {

    @MockBean
    private MessageService messageService;

    @MockBean
    private JweEncrypter encrypter;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void text_validRequestWithEncryptedPhone() throws Exception {

        when(encrypter.decrypt(anyString())).thenReturn("1234567890");
        when(messageService.sendPlainTextMessage(anyString(), anyString())).thenReturn(Optional.of("some string"));

        mockMvc.perform(
                post("/message/text?encrypted=true")
                .content("{\"body\":\"Send it\", \"destination\":\"1234567890\"}")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("status", is("OK")))
                .andExpect(jsonPath("response.tracking", is("some string")))
                .andExpect(jsonPath("response.epochMilli", allOf(notNullValue(), instanceOf(Number.class))));

        verify(encrypter, times(1)).decrypt(anyString());

    }

    @Test
    public void text_validRequestWithPhonePlainDestination() throws Exception {

        when(messageService.sendPlainTextMessage(anyString(), anyString())).thenReturn(Optional.of("some string"));

        mockMvc.perform(
                post("/message/text?plain=")
                .content("{\"body\":\"Send it\", \"destination\":\"1234567890\"}")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("status", is("OK")))
                .andExpect(jsonPath("response.tracking", is("some string")))
                .andExpect(jsonPath("response.epochMilli", allOf(notNullValue(), instanceOf(Number.class))));

        verifyZeroInteractions(encrypter);

    }

    @Test
    public void text_validRequest_NoLinkage_404() throws Exception {

        when(messageService.sendPlainTextMessage(anyString(), anyString())).thenReturn(Optional.empty());

        mockMvc.perform(
                post("/message/text")
                .content("{\"body\":\"Send it\", \"destination\":\"1234567890\"}")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("status", is("ERROR")))
                .andExpect(jsonPath("reason", is("No linkage found")));

    }

    @Test
    public void text_noBodyFieldRequest_400() throws Exception {

        mockMvc.perform(
                post("/message/text")
                        .content("{\"destination\":\"1234567890\"}")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("status", is("ERROR")))
                .andExpect(jsonPath("reason", is("[body] may not be empty")));

        verifyZeroInteractions(encrypter);
        verifyZeroInteractions(messageService);

    }

    @Test
    public void text_noDestFieldRequest_400() throws Exception {

        mockMvc.perform(
                post("/message/text")
                        .content("{\"body\":\"Send it\"}")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("status", is("ERROR")))
                .andExpect(jsonPath("reason", is("[destination] may not be empty")));

        verifyZeroInteractions(encrypter);
        verifyZeroInteractions(messageService);

    }

    @Test
    public void text_validRequestWithEncryptedPhoneExceptionThrow() throws Exception {

        when(encrypter.decrypt(anyString())).thenReturn("1234567890");
        when(messageService.sendPlainTextMessage(anyString(), anyString())).thenThrow(new RuntimeException("some exception"));

        mockMvc.perform(
                post("/message/text?encrypted=true")
                        .content("{\"body\":\"Send it\", \"destination\":\"1234567890\"}")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("status", is("ERROR")))
                .andExpect(jsonPath("reason", is("some exception")));

    }

    @Test
    public void text_validRequestWithEncryptedExpitedToken_BadReqeust() throws Exception {

        when(encrypter.decrypt(anyString())).thenThrow(JweEncrypter.JweEncrypterException.class);

        mockMvc.perform(
                post("/message/text?encrypted=true")
                        .content("{\"body\":\"Send it\", \"destination\":\"eyJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiZGlyIn0..IGSIJBjAKneaRpzT.GOdwU_mMwkBwwUiQ1stgKeTLk96Rmum3avpOljHqwWMl6-QNugoCMto.54k21xp7wN5kAyRqHDAebw\"}")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("status", is("ERROR")));

        verifyZeroInteractions(messageService);

    }

}