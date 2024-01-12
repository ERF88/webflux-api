package com.github.erf88.controller.exceptions;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    public Mono<ResponseEntity<StandardError>> handleDuplicateKeyException(final DuplicateKeyException ex, final ServerHttpRequest request) {
        String message;
        if (ex.getMessage().contains("email dup key")) {
            message = "E-mail already registered";
        } else {
            message = "Duplicate key exception";
        }
        StandardError standardError = getStandardError(BAD_REQUEST.value(), BAD_REQUEST.getReasonPhrase(), message, request.getPath().toString());
        return Mono.just(ResponseEntity.badRequest().body(standardError));
    }

    @Override
    protected Mono<ResponseEntity<Object>> handleWebExchangeBindException(
            WebExchangeBindException ex,
            HttpHeaders httpHeaders,
            HttpStatusCode httpStatusCode,
            ServerWebExchange serverWebExchange) {

        final OffsetDateTime timestamp = OffsetDateTime.now();
        final String path = serverWebExchange.getRequest().getPath().toString();
        final Integer status = httpStatusCode.value();
        final String error = "Validation error";
        final String message = "Error on validation attributes";

        ValidationError validationError = new ValidationError(timestamp, path, status, error, message);

        ex.getBindingResult().getFieldErrors().forEach(e -> validationError.addError(e.getField(), e.getDefaultMessage()));

        return Mono.just(ResponseEntity.badRequest().body(validationError));
    }

    private StandardError getStandardError(int status, String error, String message, String path) {
        return StandardError.builder()
                .timestamp(OffsetDateTime.now())
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .build();
    }

}
