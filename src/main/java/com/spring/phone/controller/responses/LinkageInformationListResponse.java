package com.spring.phone.controller.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@Getter
@ApiModel
public class LinkageInformationListResponse implements Serializable {

    @JsonProperty("linkages")
    private List<LinkageInformationResponse> linkages = new ArrayList<>();

    @JsonProperty
    private Integer total;

    public LinkageInformationListResponse(List<LinkageInformationResponse> linkages) {
        if (nonNull(linkages)) {
            this.linkages = linkages;
        }
        total = this.linkages.size();
    }
}