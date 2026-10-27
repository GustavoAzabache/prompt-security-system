package com.me.microservices.document_service.document.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.me.microservices.document_service.document.entity.RejectedDocumentEntity;

public interface RejectedDocumentRepository extends MongoRepository<RejectedDocumentEntity, String> {

}
