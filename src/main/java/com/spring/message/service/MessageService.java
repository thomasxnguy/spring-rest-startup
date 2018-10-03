package com.spring.message.service;

import java.util.Optional;

public interface MessageService {

    Optional<String> sendPlainTextMessage(String dest, String text);

}
