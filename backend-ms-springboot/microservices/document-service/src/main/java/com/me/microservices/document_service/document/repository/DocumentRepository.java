package com.me.microservices.document_service.document.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.me.microservices.document_service.document.entity.DocumentEntity;

public interface DocumentRepository extends MongoRepository<DocumentEntity, String> {

}
