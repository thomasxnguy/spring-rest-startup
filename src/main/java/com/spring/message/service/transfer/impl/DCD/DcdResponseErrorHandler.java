package com.spring.message.service.transfer.impl.DCD;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.util.stream.Collectors.joining;

@Slf4j
public class DcdResponseErrorHandler extends DefaultResponseErrorHandler {

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        try {
            List<String> stringList = IOUtils.readLines(response.getBody(), StandardCharsets.UTF_8);
            log.error("Error response with body {}", stringList.stream().collect(joining(" ")));
        }catch (Exception e) {
            log.error("Failed to read error response");
        }
        // (1) by commenting out line below, we are preventing exception thrown,
        // (2) status code must be checked in client code
        // super.handleError(response);
    }

    @Override
    protected boolean hasError(HttpStatus statusCode) {
        return super.hasError(statusCode);
    }
}
