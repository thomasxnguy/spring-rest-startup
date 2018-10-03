package com.spring.message.service.transfer.impl.DCD;

import com.spring.message.service.transfer.MessageTransferService;
import com.spring.message.service.transfer.impl.DCD.dto.PlainTextDto;
import com.spring.message.service.transfer.impl.DCD.dto.ReservationResponse;
import com.spring.utils.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.net.URI;
import java.util.Set;

import static com.spring.utils.Utils.maskLeftWithTail4;

@Slf4j
class CommunicationGroupClientMessageTransferService implements MessageTransferService {

    private final Validator validator;
    private final RestTemplate restTemplate;
    private final URI dcdUri;

    CommunicationGroupClientMessageTransferService(Validator validator, RestTemplate restTemplate, URI url) {
        this.validator = validator;
        this.restTemplate = restTemplate;
        this.dcdUri = url;
    }

    @Override
    public String sendPlainText(String phoneNumber, String message, String... tags) {
        Assert.hasText(phoneNumber, "destination phone can not be empty");
        Assert.hasText(message, "message can not be empty");
        log.info("Sending plain text message with to [{}] with [{}] and tracking data [{}]", maskLeftWithTail4(phoneNumber), message, tags);
        PlainTextDto messageDto = new PlainTextDto(phoneNumber, message, tags);
        Set<ConstraintViolation<PlainTextDto>> constraintViolations = validator.validate(messageDto);
        if (!constraintViolations.isEmpty()) {
            log.warn("Can not construct message: {}, {}", messageDto, constraintViolations);
            throw new BadRequestException("Service message is invalid");
        }

        ResponseEntity<ReservationResponse> response = restTemplate.postForEntity(dcdUri, messageDto, ReservationResponse.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Failed to send message");
        }

        return response.getBody().getReservationId();
    }
}
