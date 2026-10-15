package com.me.microservices.auth_service.area.exception;

public class AreaNotFoundException extends RuntimeException {

    public AreaNotFoundException(String message) {
        super(message);
    }

}
