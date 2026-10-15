package com.me.microservices.rag_service.ask.exception;

public class GenerationException extends RuntimeException {
    public GenerationException(String message) {
        super(message);
    }
}
