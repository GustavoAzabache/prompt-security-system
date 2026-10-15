package com.me.microservices.document_service.document.exception;

public class DocumentStorageException extends RuntimeException {

    public DocumentStorageException(String message) {
        super(message);
    }

}
