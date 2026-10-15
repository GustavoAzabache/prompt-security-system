package com.me.microservices.rag_service.ingest.exception;

public class ForbiddenIngestException extends RuntimeException {

    public ForbiddenIngestException(String message) {
        super(message);
    }

}
