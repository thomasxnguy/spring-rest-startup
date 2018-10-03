package com.spring.message.controller;

import com.spring.message.controller.dto.MessageTrackingResponse;
import com.spring.message.service.MessageService;
import com.spring.message.controller.dto.TextMessageRequest;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ValueConstants;

import javax.validation.Valid;
import java.util.Optional;

import static com.spring.phone.services.ErrorMessages.NOT_UNIQUE_RESULT;
import static com.spring.phone.services.ErrorMessages.NO_LINKAGE_FOUND;
import static com.spring.utils.ResponseUtils.notFound;
import static java.util.Objects.nonNull;

@PreAuthorize("hasAuthority(T(com.spring.security.Authority).SEND_MESSAGE)")
@Slf4j
@RestController
@RequestMapping(value = "/message", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(description = "Sends Message Controller")
public class MessageController {

    private final JweEncrypter encrypter;
    private final MessageService messageService;

    @Autowired
    public MessageController(JweEncrypter encrypter, MessageService messageService) {
        this.encrypter = encrypter;
        this.messageService = messageService;
    }

    @ApiOperation(value = "Send plain text System Message", response = MessageTrackingResponse.class, code = 200)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, response = Response.class, message = "Corresponding field error message"),
            @ApiResponse(code = 404, response = Response.class, message = NO_LINKAGE_FOUND),
            @ApiResponse(code = 500, response = Response.class, message =
                    NOT_UNIQUE_RESULT +
                            " • Message line from server error"
            )
    })
    @PostMapping("/text")
    public ResponseEntity text(@RequestBody @Valid TextMessageRequest request,
                               @RequestParam(value = "plain", required = false) String plainDestination) {
        String dest = request.getDestination();
        log.debug("Send message request: plain destination [{}] with payload [{}}", plainDestination, request);
        boolean isEncrypted = true;
        if (nonNull(plainDestination) && !ValueConstants.DEFAULT_NONE.equals(plainDestination)) {
            isEncrypted = false;
        }

        if (isEncrypted) {
            try {
                dest = encrypter.decrypt(dest);
            } catch (JweEncrypter.JweEncrypterException e) {
                return ResponseUtils.badRequest(e.getMessage());
            }
            log.debug("Send message to: [{}]", dest);
        }
        Optional<String> tracking = messageService.sendPlainTextMessage(dest, request.getBody());

        return tracking
                .map(MessageTrackingResponse::new)
                .map(ResponseUtils::ok)
                .orElse(notFound(NO_LINKAGE_FOUND));

    }

}
