package com.github.erf88.controller.exceptions;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Mono<StandardError>> handleDuplicateKeyException(final DuplicateKeyException ex, final ServerHttpRequest request) {
        String message;
        if (ex.getMessage().contains("email dup key")) {
            message = "E-mail already registered";
        } else {
            message = "Duplicate key exception";
        }
        return ResponseEntity.badRequest().body(getBody(BAD_REQUEST, message, request.getPath().toString()));
    }

    private Mono<StandardError> getBody(final HttpStatus httpStatus, final String message, final String path) {
        return Mono.just(StandardError.builder()
                .timestamp(OffsetDateTime.now())
                .status(httpStatus.value())
                .error(httpStatus.getReasonPhrase())
                .message(message)
                .path(path)
                .build());
    }

}
