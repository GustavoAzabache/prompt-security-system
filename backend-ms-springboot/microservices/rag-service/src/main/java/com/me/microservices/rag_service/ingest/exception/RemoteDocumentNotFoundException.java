package com.me.microservices.rag_service.ingest.exception;

public class RemoteDocumentNotFoundException extends RuntimeException {

    public RemoteDocumentNotFoundException(String message) {
        super(message);
    }

}
