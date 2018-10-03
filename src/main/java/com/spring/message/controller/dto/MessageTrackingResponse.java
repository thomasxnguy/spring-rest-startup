package com.spring.message.controller.dto;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;

@Getter
public class MessageTrackingResponse implements Serializable {

    private String tracking;
    private long epochMilli = Instant.now().toEpochMilli();

    public MessageTrackingResponse(String tracking) {
        this.tracking = tracking;
    }

}
