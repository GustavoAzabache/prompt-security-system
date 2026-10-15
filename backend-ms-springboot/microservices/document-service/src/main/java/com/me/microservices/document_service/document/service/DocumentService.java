package com.me.microservices.document_service.document.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.me.microservices.document_service.document.dto.DocumentContent;
import com.me.microservices.document_service.document.dto.EmployeeData;
import com.me.microservices.document_service.document.dto.request.ChangeConfidentialityRequest;
import com.me.microservices.document_service.document.dto.response.ChangeConfidentialityResponse;
import com.me.microservices.document_service.document.dto.response.DocumentResponse;
import com.me.microservices.document_service.document.dto.response.UploadDocumentResponse;
import com.me.microservices.document_service.document.entity.DocumentEntity;
import com.me.microservices.document_service.document.entity.enums.ConfidentialityLevel;
import com.me.microservices.document_service.document.entity.enums.TypeDocument;
import com.me.microservices.document_service.document.exception.DocumentNotFoundException;
import com.me.microservices.document_service.document.exception.DocumentStorageException;
import com.me.microservices.document_service.document.exception.ForbiddenDocumentUpdateException;
import com.me.microservices.document_service.document.mapper.DocumentMapper;
import com.me.microservices.document_service.document.repository.DocumentRepository;
import com.me.microservices.document_service.document.validator.DocumentValidator;
import com.me.microservices.document_service.document.validator.DocumentValidatorFactory;
import com.me.microservices.document_service.document.validator.detector.DocumentTypeDetector;
import com.me.microservices.document_service.document.validator.detector.DocumentTypeResolver;
import com.me.microservices.document_service.storage.FileStorageService;
import com.me.microservices.document_service.storage.dto.StoredFile;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Service 
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentTypeDetector documentTypeDetector;
    private final DocumentTypeResolver documentTypeResolver;
    private final DocumentValidatorFactory documentValidatorFactory;
    private final DocumentMapper documentMapper;
    private final FileStorageService fileStorageService;

    public List<DocumentResponse> getAll(Pageable pageable) {
        return documentRepository.findAll(pageable).stream()
                .map(documentMapper::fromEntityToResponse)
                .toList();
    }

    public DocumentResponse getById(UUID id) {
        return documentMapper.fromEntityToResponse(findDocumentById(id));
    }

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

    public UploadDocumentResponse upload(MultipartFile file, EmployeeData employeeData) {
        
        // 1. Detectar MIME real
        String mimeType = documentTypeDetector.detect(file);

        // 2. Convertir MIME a nuestro enum
        TypeDocument type = documentTypeResolver.resolve(mimeType);

        // 3. Obtener estrategia correspondiente
        DocumentValidator validator = documentValidatorFactory.getValidator(type);

        // 4. Validar estructura real
        validator.validate(file);

        // Paso para detectar malware para el futuro

        UUID documentId = UUID.randomUUID();

        // Archivo como tal guardado en disco
        StoredFile storedFile = fileStorageService.store(file, documentId);
        
        // Metadatos del Multipart en MongoDB
        DocumentEntity entity = documentRepository.save(
            DocumentEntity.create(
                documentId,
                storedFile.originalFilename(),
                storedFile.size(),
                type,
                mimeType,
                storedFile.location(),
                employeeData.area(),
                employeeData.hierarchy(),
                ConfidentialityLevel.PRIVATE_ONLY_AREA
            )
        );
        
        return new UploadDocumentResponse(
            entity.getId(),
            entity.getTitle(),
            entity.getType(),
            entity.getSize()
        );
    }

    public ChangeConfidentialityResponse changeConfidentiality(
        UUID id, ChangeConfidentialityRequest request, EmployeeData employeeData
    ) {
        DocumentEntity entity = findDocumentById(id);
        
        canChangeConfidentiality(entity, employeeData);

        String oldConfidentality = entity.getConfidentialityLevel().name();

        entity.changeConfidentiality(request.confidentialityLevel());

        String currentConfidentality = entity.getConfidentialityLevel().name();

        return new ChangeConfidentialityResponse(
            "The document's confidentiality level has been changed from " + oldConfidentality + " to " + currentConfidentality + "."
        );
    }

    private void canChangeConfidentiality(DocumentEntity entity, EmployeeData employeeData) {
        if (entity.getConfidentialityLevel() == ConfidentialityLevel.CONFIDENTIAL
            && !employeeData.hierarchy().equals("DIRECTOR")
        ) {
            throw new ForbiddenDocumentUpdateException("Only DIRECTORS can update CONFIDENTIAL documents");
        }

        if (entity.getConfidentialityLevel() == ConfidentialityLevel.PRIVATE_ONLY_AREA
            && !entity.getArea().equals(employeeData.area())
        ) {
            throw new ForbiddenDocumentUpdateException("Only employees from " + entity.getArea() + " can update " + entity.getArea() + " documents");
        }
    }

    private DocumentEntity findDocumentById(UUID id) {
        return documentRepository.findById(id.toString())
                                    .orElseThrow(() -> new DocumentNotFoundException("The document with ID " + id + " doesn't exist."));
    }
}
