package com.me.microservices.document_service.document.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.me.microservices.document_service.document.dto.response.DocumentResponse;
import com.me.microservices.document_service.document.entity.DocumentEntity;

@Component
public class DocumentMapper {

    public DocumentResponse fromEntityToResponse(DocumentEntity entity) {
        return fromEntityToResponse(entity, List.of());
    }

    public DocumentResponse fromEntityToResponse(DocumentEntity entity, List<String> findings) {
        return new DocumentResponse(
            entity.getId(),
            entity.getTitle(),
            entity.getSize(),
            entity.getType().name(),
            entity.getArea(),
            entity.getConfidentialityLevel().name(),
            entity.getStatus().name(),
            findings != null ? findings : List.of()
        );
    }

}
