package com.me.microservices.rag_service.document.exception;

public class DocumentEmptyException extends RuntimeException {

    public DocumentEmptyException(String message) {
        super(message);
    }

}
