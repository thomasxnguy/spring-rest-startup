package com.spring.phone.controller;

import com.spring.phone.services.ErrorMessages;
import com.spring.phone.services.LinkageService;
import com.spring.phone.controller.responses.LinkageInformationResponse;
import com.spring.utils.ResponseUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.spring.utils.ResponseUtils.notFound;
import static java.util.stream.Collectors.joining;

@PreAuthorize("hasAuthority(T(com.spring.security.Authority).ID_BY_HASH)")
@Slf4j
@RestController
@RequestMapping(path = "/hashes", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(description = "Looks up Linkage information by phone hash (including bulk)")
public class PhoneHashLookupController {

    private final LinkageService linkageService;
    private final Long requestSizeLimitBytes;

    @Autowired
    public PhoneHashLookupController(LinkageService linkageService, @Value("${requestSizeLimitBytes:10000}") Long requestSizeLimit) {
        this.linkageService = linkageService;
        this.requestSizeLimitBytes = requestSizeLimit;
    }

    @GetMapping("{phone_hash}")
    public ResponseEntity findPhoneByHash(@PathVariable("phone_hash") String phoneHash) {
        log.debug("finedPhoneByHash [phoneHash = {}]", phoneHash);
        return linkageService.getLinkageDataByHash(phoneHash)
                .map(ld -> new LinkageInformationResponse(ld.getPhoneNumber(), ld.getId(), ld.getPhoneHash()))
                .map(ResponseUtils::ok)
                .orElse(notFound(ErrorMessages.NO_LINKAGE_FOUND));
    }
}
