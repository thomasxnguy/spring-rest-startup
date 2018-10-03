package com.spring.phone.controller.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigInteger;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@ApiModel
public class LinkageInformationResponse implements Serializable {

    @ApiModelProperty("Phone number")
    @JsonProperty("phone")
    private String phoneNumber;

    @ApiModelProperty("ID")
    @JsonProperty("id")
    private BigInteger id;

    @Setter
    @ApiModelProperty("Token's expiration time (mills) in unix epoch")
    @JsonProperty("canonicalPhoneHash")
    private String hash;

    @ApiModelProperty("Tokenized linkage information")
    @JsonUnwrapped
    private Token token;

    public LinkageInformationResponse(BigInteger id) {
        this.id = id;
    }

    public LinkageInformationResponse(String phoneNumber, BigInteger id, String hash) {
        this.phoneNumber = phoneNumber;
        this.id = id;
        this.hash = hash;
    }

    public LinkageInformationResponse(String phoneNumber, Token token) {
        this.phoneNumber = phoneNumber;
        this.token = token;
    }

}