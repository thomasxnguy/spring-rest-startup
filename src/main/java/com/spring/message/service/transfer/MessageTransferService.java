package com.spring.message.service.transfer;

public interface MessageTransferService {

    String sendPlainText(String phoneNumber, String message, String... tags);

}
