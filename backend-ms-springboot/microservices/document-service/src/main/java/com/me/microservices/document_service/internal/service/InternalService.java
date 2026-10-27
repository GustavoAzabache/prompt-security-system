package com.me.microservices.document_service.internal.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.me.microservices.document_service.document.dto.DocumentContent;
import com.me.microservices.document_service.document.entity.DocumentEntity;
import com.me.microservices.document_service.document.exception.DocumentNotFoundException;
import com.me.microservices.document_service.document.exception.DocumentStorageException;
import com.me.microservices.document_service.document.repository.DocumentRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class InternalService {

    private final DocumentRepository documentRepository;

    public DocumentContent getContent(UUID id) {
        DocumentEntity entity = findDocumentById(id);

        Path path = Paths.get(entity.getLocation());

        Resource resource = new FileSystemResource(path);

        if (!resource.exists() || !resource.isReadable()) {
            throw new DocumentStorageException("Document file not found: " + entity.getLocation());
        }

        String mediaType = entity.getMediaType() != null && !entity.getMediaType().isBlank()
                ? entity.getMediaType()
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;

        return new DocumentContent(mediaType, entity.getSize(), entity.getTitle(), resource);
    }

    private DocumentEntity findDocumentById(UUID id) {
        return documentRepository.findById(id.toString())
                                    .orElseThrow(() -> new DocumentNotFoundException("The document with ID " + id + " doesn't exist."));
    }

}
