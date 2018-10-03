package com.spring.phone.controller;


import com.spring.phone.services.ErrorMessages;
import com.spring.phone.services.LinkageService;
import com.spring.phone.controller.responses.LinkageInformationResponse;
import com.spring.phone.repository.jpa.LinkageData;
import com.spring.utils.Response;
import com.spring.utils.ResponseUtils;
import com.spring.utils.exception.BadRequestException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.spring.utils.ResponseUtils.notFound;

@PreAuthorize("hasAuthority(T(com.spring.security.Authority).ID_BY_PHONE)")
@Slf4j
@RestController
@RequestMapping(path = "/phones/{canonical_phone_number}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(description = "Looks up ID by phone number")
public class IdByPhoneLinkageController {

    private final LinkageService linkageService;

    @Autowired
    public IdByPhoneLinkageController(LinkageService linkageService) {
        this.linkageService = linkageService;
    }

    @GetMapping(path = "/id")
    @ApiOperation(value = "Looks up ID by Phone number", response = LinkageInformationResponse.class, code = 200)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, response = Response.class, message = ErrorMessages.NOT_NUMERICAL_PHONE_NUMBER),
            @ApiResponse(code = 404, response = Response.class, message = ErrorMessages.NO_LINKAGE_FOUND),
            @ApiResponse(code = 500, response = Response.class, message = ErrorMessages.NOT_UNIQUE_RESULT)
    })
    public ResponseEntity findIdByPhone(
            @ApiParam(value = "Number") @PathVariable(name = "canonical_phone_number") String phoneNumber
    ) throws BadRequestException {
        log.debug("findIdByPhone with request [{}]", phoneNumber);

        return linkageService.getLinkageDataByPhoneNumber(phoneNumber)
                .map(LinkageData::getId)
                .map(LinkageInformationResponse::new)
                .map(ResponseUtils::ok)
                .orElse(notFound(ErrorMessages.NO_LINKAGE_FOUND));
    }
}
