package com.me.microservices.document_service.document.validator;

import org.springframework.web.multipart.MultipartFile;

import com.me.microservices.document_service.document.entity.enums.TypeDocument;

public interface DocumentValidator {

    TypeDocument getSupportedType();
    
    void validate(MultipartFile file);
}
