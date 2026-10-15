package com.me.microservices.rag_service.document.exception;

public class RetrieveDocumentException extends RuntimeException {

    public RetrieveDocumentException(String message) {
        super(message);
    }

}
