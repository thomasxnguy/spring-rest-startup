package com.spring.message.service.transfer.impl.dev;

import com.spring.message.service.transfer.MessageTransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Profile("nomessage")
@Slf4j
@Service
public class LogOnlyTransferService implements MessageTransferService {

    @Override
    public String sendPlainText(String phoneNumber, String message, String... tags) {
        log.debug("Sending plain text message with to [{}] with [{}] and tracking data [{}]", phoneNumber, message, tags);
        return UUID.randomUUID().toString();
    }
}
