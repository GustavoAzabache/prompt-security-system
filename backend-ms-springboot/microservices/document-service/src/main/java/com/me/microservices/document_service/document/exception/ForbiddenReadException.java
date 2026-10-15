package com.me.microservices.document_service.document.exception;

public class ForbiddenReadException extends RuntimeException {

    public ForbiddenReadException(String message) {
        super(message);
    }

}
