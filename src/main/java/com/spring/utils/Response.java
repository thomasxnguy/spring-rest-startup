package com.spring.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "General Model response")
public class Response<T extends Serializable, E extends Serializable> {

    @ApiModelProperty(required = true)
    @JsonProperty("status")
    private String status;

    @JsonProperty("response")
    private T response;

    @JsonProperty("reason")
    private E reason;

    Response() {
    }
}
