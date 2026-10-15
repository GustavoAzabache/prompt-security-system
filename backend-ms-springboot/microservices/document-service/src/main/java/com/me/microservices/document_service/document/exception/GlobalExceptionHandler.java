package com.me.microservices.document_service.document.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ForbiddenUploadException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenUpload(ForbiddenUploadException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ErrorResponse.of(HttpStatus.FORBIDDEN, exception.getMessage())
        );
    }

    @ExceptionHandler(ForbiddenReadException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenRead(ForbiddenReadException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ErrorResponse.of(HttpStatus.FORBIDDEN, exception.getMessage())
        );
    }

    @ExceptionHandler(DocumentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDocumentNotFound(DocumentNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorResponse.of(HttpStatus.NOT_FOUND, exception.getMessage())
        );
    }

    @ExceptionHandler(ForbiddenDocumentUpdateException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenDocumentUpdate(ForbiddenDocumentUpdateException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ErrorResponse.of(HttpStatus.FORBIDDEN, exception.getMessage())
        );
    }

    @ExceptionHandler(InvalidDocumentTypeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDocumentType(InvalidDocumentTypeException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse.of(HttpStatus.BAD_REQUEST, exception.getMessage())
        );
    }

    @ExceptionHandler(DocumentStorageException.class)
    public ResponseEntity<ErrorResponse> handleDocumentStorage(DocumentStorageException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorResponse.of(HttpStatus.NOT_FOUND, exception.getMessage())
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
}
