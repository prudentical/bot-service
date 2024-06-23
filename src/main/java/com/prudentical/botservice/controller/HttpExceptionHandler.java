package com.prudentical.botservice.controller;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.prudentical.botservice.exceptions.InsufficientFundsException;
import com.prudentical.botservice.exceptions.NotFoundException;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class HttpExceptionHandler extends ResponseEntityExceptionHandler {

    @Builder
    record ErrorBody(Instant timestamp, int status, String message) {

    }

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<ErrorBody> handleNotFound(RuntimeException ex, WebRequest request) {
        log.debug("Handling {}",ex);
        var error = ErrorBody.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(Instant.now())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    ResponseEntity<ErrorBody> handleBadRequest(RuntimeException ex, WebRequest request) {
        log.debug("Handling {}",ex);
        var error = ErrorBody.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(Instant.now())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<ErrorBody> handleOthers(RuntimeException ex, WebRequest request) {
        log.debug("Handling {}",ex);
        ex.printStackTrace();
        var error = ErrorBody.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(Instant.now())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

}
