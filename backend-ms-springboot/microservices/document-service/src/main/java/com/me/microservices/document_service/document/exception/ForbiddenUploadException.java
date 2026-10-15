package com.me.microservices.document_service.document.exception;

public class ForbiddenUploadException extends RuntimeException {

    public ForbiddenUploadException(String message) {
        super(message);
    }

}
