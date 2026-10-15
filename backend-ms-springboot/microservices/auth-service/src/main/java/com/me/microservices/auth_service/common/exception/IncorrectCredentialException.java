package com.me.microservices.auth_service.common.exception;

public class IncorrectCredentialException extends RuntimeException {

    public IncorrectCredentialException(String message) {
        super(message);
    }

}
