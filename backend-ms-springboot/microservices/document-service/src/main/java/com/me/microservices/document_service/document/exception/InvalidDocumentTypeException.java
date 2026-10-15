package com.me.microservices.document_service.document.exception;

public class InvalidDocumentTypeException extends RuntimeException {

    public InvalidDocumentTypeException(String message) {
        super(message);
    }

}
