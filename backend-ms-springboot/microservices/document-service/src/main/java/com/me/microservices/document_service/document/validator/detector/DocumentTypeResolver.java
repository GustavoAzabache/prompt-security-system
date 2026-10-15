package com.me.microservices.document_service.document.validator.detector;

import org.springframework.stereotype.Component;

import com.me.microservices.document_service.document.entity.enums.TypeDocument;
import com.me.microservices.document_service.document.exception.InvalidDocumentTypeException;

@Component
public class DocumentTypeResolver {

    public TypeDocument resolve(String mimeType) {

        return switch (mimeType) {
            case "application/pdf" ->
                TypeDocument.PDF;

            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" ->
                TypeDocument.DOCX;

            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" ->
                TypeDocument.XLSX;

            default ->
                throw new InvalidDocumentTypeException("Unsupported document type: " + mimeType);
        };
    }
}