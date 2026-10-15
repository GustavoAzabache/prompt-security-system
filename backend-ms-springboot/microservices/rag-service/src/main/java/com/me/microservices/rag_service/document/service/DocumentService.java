package com.me.microservices.rag_service.document.service;

import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.me.microservices.rag_service.document.client.DocumentClient;
import com.me.microservices.rag_service.document.exception.DocumentEmptyException;
import com.me.microservices.rag_service.document.exception.RetrieveDocumentException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DocumentService {

    private final DocumentClient documentClient;

    public Resource getContent(UUID id, String authorization) {
        var response = documentClient.getContent(id, authorization);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RetrieveDocumentException("Could not retrieve document:" + id);
        }

        Resource resource = response.getBody();

        if (resource == null) {
            throw new DocumentEmptyException("Document content is empty: " + id);
        }

        return resource;
    }


}
