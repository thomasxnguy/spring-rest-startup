package com.spring.phone.controller.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class Token implements Serializable {
    @ApiModelProperty("Encrypted phone number")
    @JsonProperty("token")
    private String tokenString;

    @ApiModelProperty("Token's expiration time (mills) in unix epoch")
    @JsonProperty("expireEpochMills")
    private Long tokenExpirationEpoch;

    public Token(String token, Long tokenExpirationEpoch) {
        this.tokenString = token;
        this.tokenExpirationEpoch = tokenExpirationEpoch;
    }
}
