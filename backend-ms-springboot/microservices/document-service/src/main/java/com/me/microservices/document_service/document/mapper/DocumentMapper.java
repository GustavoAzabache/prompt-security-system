package com.me.microservices.document_service.document.mapper;

import org.springframework.stereotype.Component;

import com.me.microservices.document_service.document.dto.response.DocumentResponse;
import com.me.microservices.document_service.document.entity.DocumentEntity;

@Component
public class DocumentMapper {

    public DocumentResponse fromEntityToResponse(DocumentEntity entity) {
        return new DocumentResponse(
            entity.getId(),
            entity.getTitle(),
            entity.getSize(),
            entity.getType().name(),
            entity.getArea(),
            entity.getConfidentialityLevel().name()
        );
    }

}
