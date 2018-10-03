package com.spring.message.service.impl;

import com.spring.phone.repository.jpa.LinkageData;
import com.spring.phone.services.LinkageService;
import com.spring.message.service.MessageService;
import com.spring.message.service.transfer.MessageTransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Optional;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    private final LinkageService linkageService;
    private final MessageTransferService messageTransferService;

    @Autowired
    public MessageServiceImpl(LinkageService linkageService, MessageTransferService messageTransferService) {
        this.linkageService = linkageService;
        this.messageTransferService = messageTransferService;
    }

    @Override
    public Optional<String> sendPlainTextMessage(String dest, String text) {
        Optional<BigInteger> id = linkageService.getLinkageDataByPhoneNumber(dest).map(LinkageData::getId);
        if (id.isPresent()) {
            return Optional.ofNullable(messageTransferService.sendPlainText(dest, text));
        }
        return Optional.empty();
    }
}
