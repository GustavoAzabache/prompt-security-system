package com.me.microservices.auth_service.session.exception;

public class SessionRevokedException extends RuntimeException {

    public SessionRevokedException(String message) {
        super(message);
    }

}
