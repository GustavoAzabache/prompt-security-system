package com.me.microservices.document_service.document.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.me.microservices.document_service.document.entity.DocumentEntity;
import com.me.microservices.document_service.document.entity.enums.DocumentStatus;

public interface DocumentRepository extends MongoRepository<DocumentEntity, String> {

    List<DocumentEntity> findByStatus(Pageable pageable, DocumentStatus status);
}
