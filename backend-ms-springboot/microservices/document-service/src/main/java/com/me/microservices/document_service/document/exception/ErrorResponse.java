package com.me.microservices.document_service.document.exception;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
    LocalDateTime timestamp,
    List<String> messages,
    int status
) {

    public static ErrorResponse of(HttpStatus status, List<String> messages) {
        return new ErrorResponse(LocalDateTime.now(), messages, status.value());
    }

    public static ErrorResponse of(HttpStatus status, String message) {
        return new ErrorResponse(LocalDateTime.now(), List.of(message), status.value());
    }

}