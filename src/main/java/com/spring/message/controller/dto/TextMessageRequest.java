package com.spring.message.controller.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class TextMessageRequest {

    @NotEmpty
    public String destination;

    @NotEmpty
    public String body;

}
