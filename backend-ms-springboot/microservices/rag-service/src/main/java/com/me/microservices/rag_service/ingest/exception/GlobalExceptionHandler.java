package com.me.microservices.rag_service.ingest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.me.microservices.rag_service.query.exception.ForbiddenQueryException;
import com.me.microservices.rag_service.query.exception.InvalidQueryException;
import com.me.microservices.rag_service.ask.exception.GenerationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidQueryException.class)
    public ResponseEntity<ErrorResponse> handleInvalidQuery(InvalidQueryException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse.of(HttpStatus.BAD_REQUEST, exception.getMessage())
        );
    }

    @ExceptionHandler(ForbiddenQueryException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenQuery(ForbiddenQueryException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ErrorResponse.of(HttpStatus.FORBIDDEN, exception.getMessage())
        );
    }

    @ExceptionHandler(GenerationException.class)
    public ResponseEntity<ErrorResponse> handleGeneration(GenerationException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage())
        );
    }

    @ExceptionHandler(ForbiddenIngestException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenIngest(ForbiddenIngestException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ErrorResponse.of(HttpStatus.FORBIDDEN, exception.getMessage())
        );
    }

    @ExceptionHandler(RemoteDocumentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRemoteDocumentNotFound(RemoteDocumentNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorResponse.of(HttpStatus.NOT_FOUND, exception.getMessage())
        );
    }

    @ExceptionHandler(DocumentTextExtractionException.class)
    public ResponseEntity<ErrorResponse> handleDocumentTextExtraction(DocumentTextExtractionException exception) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(
            ErrorResponse.of(HttpStatus.UNPROCESSABLE_CONTENT, exception.getMessage())
        );
    }

    @ExceptionHandler(VectorStoreException.class)
    public ResponseEntity<ErrorResponse> handleVectorStore(VectorStoreException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage())
        );
    }
}
