package com.me.microservices.auth_service.common.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.me.microservices.auth_service.area.exception.AreaNotFoundException;
import com.me.microservices.auth_service.security.exception.InvalidTokenTypeException;
import com.me.microservices.auth_service.session.exception.SessionNotFoundException;
import com.me.microservices.auth_service.session.exception.SessionRevokedException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<ErrorResponse> handler(InvalidDataException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse.of(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
            )
        );
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handler(MethodArgumentNotValidException exception) {
        List<String> errors = exception.getFieldErrors()
                                .stream()
                                .map(e -> e.getDefaultMessage())
                                .toList();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse.of(
                HttpStatus.BAD_REQUEST,
                errors
            )
        );
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<ErrorResponse> handler(EmailAlreadyUsedException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            ErrorResponse.of(
                HttpStatus.CONFLICT,
                exception.getMessage()
            )
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handler(UserNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorResponse.of(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
            )
        );
    }

    @ExceptionHandler(AreaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handler(AreaNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorResponse.of(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
            )
        );
    }

    @ExceptionHandler(IncorrectCredentialException.class)
    public ResponseEntity<ErrorResponse> handler(IncorrectCredentialException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ErrorResponse.of(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage()
            )
        );
    }

    @ExceptionHandler(SessionRevokedException.class)
    public ResponseEntity<ErrorResponse> handler(SessionRevokedException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ErrorResponse.of(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage()
            )
        );
    }

    @ExceptionHandler(SessionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handler(SessionNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ErrorResponse.of(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage()
            )
        );
    }

    @ExceptionHandler(InvalidTokenTypeException.class)
    public ResponseEntity<ErrorResponse> handler(InvalidTokenTypeException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ErrorResponse.of(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage()
            )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handler(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getMessage()
            )
        );
    }
}
