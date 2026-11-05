package com.me.microservices.document_service.document.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.me.microservices.document_service.document.entity.RejectedDocumentEntity;

public interface RejectedDocumentRepository extends MongoRepository<RejectedDocumentEntity, String> {

    List<RejectedDocumentEntity> findByDocumentIdIn(Collection<String> documentIds);

}
