package com.spring.phone.controller;

import com.spring.phone.services.ErrorMessages;
import com.spring.phone.services.LinkageService;
import com.spring.phone.controller.responses.LinkageInformationResponse;
import com.spring.phone.controller.responses.Token;
import com.spring.utils.Response;
import com.spring.utils.ResponseUtils;
import com.spring.utils.crypto.JweEncrypter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ValueConstants;

import java.math.BigInteger;

import static com.spring.utils.ResponseUtils.notFound;
import static java.util.Objects.nonNull;

@PreAuthorize("hasAuthority(T(com.spring.security.Authority).PHONE_BY_ID)")
@Slf4j
@RestController
@RequestMapping(path = "/id", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(description = "Looks up phone number by ID")
public class PhoneByIdLinkageController {

    private final LinkageService linkageService;
    private final JweEncrypter jweEncrypter;

    @Autowired
    public PhoneByIdLinkageController(LinkageService linkageService, JweEncrypter jweEncrypter) {
        this.linkageService = linkageService;
        this.jweEncrypter = jweEncrypter;
    }

    @GetMapping(path = {"/{id}", "/{id}/phone"})
    @ApiOperation(value = "Looks up phone by id", response = LinkageInformationResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, response = Response.class, message = ErrorMessages.NO_LINKAGE_FOUND),
            @ApiResponse(code = 500, response = Response.class, message = ErrorMessages.NOT_UNIQUE_RESULT)
    })
    public ResponseEntity findPhoneById(@PathVariable("id") BigInteger id, @RequestParam(name = "hash", required = false) String withHash) {
        log.debug("findPhoneById [id = {}]", id);
        return linkageService.getLinkageDataById(id)
                .map(ld -> {
                    final String phone = ld.getPhoneNumber();
                    JweEncrypter.Token token = jweEncrypter.encrypt(phone);
                    Token tokenizedLinkageInfo = new Token(token.getToken(), token.getExpire().getTime());
                    LinkageInformationResponse linkageInformationResponse = new LinkageInformationResponse(phone, tokenizedLinkageInfo);
                    if (nonNull(withHash) && !ValueConstants.DEFAULT_NONE.equals(withHash)) {
                        linkageInformationResponse.setHash(ld.getPhoneHash());
                    }
                    return linkageInformationResponse;
                })
                .map(ResponseUtils::ok)
                .orElse(notFound(ErrorMessages.NO_LINKAGE_FOUND));

    }



}
