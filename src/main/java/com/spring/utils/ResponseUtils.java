package com.spring.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

public final class ResponseUtils {

    public static <T extends Serializable> ResponseEntity<Response<T, ?>> ok(T body) {
        return withContentType(OK).body(ResponseUtils.success(body));
    }

    public static <T extends Serializable> ResponseEntity<Response<T, ?>> accepted(T body) {
        return withContentType(ACCEPTED).body(ResponseUtils.success(body));
    }

    public static <E extends Serializable> ResponseEntity badRequest(E reason) {
        return withContentType(BAD_REQUEST).body(ResponseUtils.error(reason));
    }

    public static <E extends Serializable> ResponseEntity notFound(E reason) {
        return withContentType(NOT_FOUND).body(ResponseUtils.error(reason));
    }

    private static ResponseEntity.BodyBuilder withContentType(HttpStatus status) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return ResponseEntity.status(status).headers(headers);
    }

    public static <T extends Serializable> Response<T, ?> success(T response) {
        Response<T, ?> responseItem = new Response<>();
        responseItem.setStatus("OK");
        responseItem.setResponse(response);
        return responseItem;
    }

    public static <E extends Serializable> Response<?, E> error(E reason) {
        Response<?, E> errorResponse = new Response<>();
        errorResponse.setStatus("ERROR");
        errorResponse.setReason(reason);
        return errorResponse;
    }

    private ResponseUtils() {
        throw new IllegalAccessError("For static usage only");
    }

}
