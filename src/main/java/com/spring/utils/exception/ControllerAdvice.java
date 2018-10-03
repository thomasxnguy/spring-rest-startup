package com.spring.utils.exception;

import com.spring.utils.Response;
import com.spring.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.text.MessageFormat;

@Slf4j
@org.springframework.web.bind.annotation.ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ControllerAdvice {

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public Response processValidationError(AccessDeniedException exception) {
        log.warn("Access Denied");
        return ResponseUtils.error("Access Denied");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Response processValidationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        FieldError error = result.getFieldError();
        String message = MessageFormat.format("[{0}] {1}", error.getField(), error.getDefaultMessage());
        log.warn("Request validation failed: {}", message);
        return ResponseUtils.error(message);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Response processValidationError(NoHandlerFoundException ex) {
        log.warn("{}", ex.getMessage());
        return ResponseUtils.error(ex.getMessage());
    }

    @ExceptionHandler(TypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Response typeMismatchException(TypeMismatchException exception) {
        String message = MessageFormat.format("Wrong type of argument, id should be {0}, but was [{1}]", exception.getRequiredType().getSimpleName(), exception.getValue());
        log.warn("Request validation failed: {}", message);
        return ResponseUtils.error(message);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Response errorResponse(BadRequestException exception) {
        log.warn("BadRequestException: ", exception.getMessage());
        return ResponseUtils.error(exception.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Response errorResponse(Throwable exception) {
        log.error("Unhandled exception: ", exception);
        return ResponseUtils.error(exception.getMessage());
    }

}
