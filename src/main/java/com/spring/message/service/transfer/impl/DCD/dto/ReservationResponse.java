package com.spring.message.service.transfer.impl.DCD.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReservationResponse {
    @JsonProperty("status")
    private String status;
    @JsonProperty("reservation_id")
    private String reservationId;
}
