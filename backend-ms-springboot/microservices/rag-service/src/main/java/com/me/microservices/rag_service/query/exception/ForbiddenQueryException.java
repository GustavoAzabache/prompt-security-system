package com.me.microservices.rag_service.query.exception;

public class ForbiddenQueryException extends RuntimeException {
    public ForbiddenQueryException(String message) {
        super(message);
    }
}
