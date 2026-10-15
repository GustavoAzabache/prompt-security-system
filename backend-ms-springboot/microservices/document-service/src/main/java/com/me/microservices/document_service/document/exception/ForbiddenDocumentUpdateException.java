package com.me.microservices.document_service.document.exception;

public class ForbiddenDocumentUpdateException extends RuntimeException {

    public ForbiddenDocumentUpdateException(String message) {
        super(message);
    }

}
