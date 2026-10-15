package com.me.microservices.rag_service.ingest.exception;

public class DocumentTextExtractionException extends RuntimeException {

    public DocumentTextExtractionException(String message) {
        super(message);
    }

}
