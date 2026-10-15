package com.me.microservices.rag_service.ingest.exception;

public class VectorStoreException extends RuntimeException {

    public VectorStoreException(String message) {
        super(message);
    }

}
