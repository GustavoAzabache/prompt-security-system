package com.me.microservices.document_service.document.service;

import java.io.File;
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
import com.me.microservices.document_service.document.entity.RejectedDocumentEntity;
import com.me.microservices.document_service.document.entity.enums.ConfidentialityLevel;
import com.me.microservices.document_service.document.entity.enums.DocumentStatus;
import com.me.microservices.document_service.document.entity.enums.TypeDocument;
import com.me.microservices.document_service.document.exception.DocumentNotFoundException;
import com.me.microservices.document_service.document.exception.DocumentStorageException;
import com.me.microservices.document_service.document.exception.ForbiddenDocumentUpdateException;
import com.me.microservices.document_service.document.mapper.DocumentMapper;
import com.me.microservices.document_service.document.repository.DocumentRepository;
import com.me.microservices.document_service.document.repository.RejectedDocumentRepository;
import com.me.microservices.document_service.document.validator.DocumentValidator;
import com.me.microservices.document_service.document.validator.DocumentValidatorFactory;
import com.me.microservices.document_service.document.validator.detector.DocumentTypeDetector;
import com.me.microservices.document_service.document.validator.detector.DocumentTypeResolver;
import com.me.microservices.document_service.messaging.events.DocumentScanResultEvent;
import com.me.microservices.document_service.messaging.events.DocumentStatusChangedEvent;
import com.me.microservices.document_service.messaging.publisher.DocumentEventPublisher;
import com.me.microservices.document_service.storage.FileStorageService;
import com.me.microservices.document_service.storage.dto.StoredFile;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Service 
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final RejectedDocumentRepository rejectedDocumentRepository;
    private final DocumentTypeDetector documentTypeDetector;
    private final DocumentTypeResolver documentTypeResolver;
    private final DocumentValidatorFactory documentValidatorFactory;
    private final DocumentMapper documentMapper;
    private final FileStorageService fileStorageService;
    private final DocumentEventPublisher documentEventPublisher;

    public List<DocumentResponse> getAll(Pageable pageable, EmployeeData employeeData) {
        return documentRepository.findAll(pageable).stream()
            .filter(doc -> canRead(doc, employeeData))
            .map(documentMapper::fromEntityToResponse)
            .toList();
    }

    public DocumentResponse getById(UUID id, EmployeeData employeeData) {
        DocumentEntity entity = findDocumentById(id);

        if (!canRead(entity, employeeData)) {
            throw new DocumentNotFoundException("The document with ID " + id + " doesn't exist.");
        }

        return documentMapper.fromEntityToResponse(entity);
    }

    public DocumentContent getContent(UUID id, EmployeeData employeeData) {
        DocumentEntity entity = findDocumentById(id);

        if (!canRead(entity, employeeData)) {
            throw new DocumentNotFoundException("The document with ID " + id + " doesn't exist.");
        }

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
                employeeData.hierarchy()
            )
        );

        documentEventPublisher.upload(entity.getId(), entity.getTitle());
        
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

        entity = documentRepository.save(entity);

        // logica para cambiar la confidencialidad (petición síncrona a rag-service)

        return new ChangeConfidentialityResponse(
            "The document's confidentiality level has been changed from " + oldConfidentality + " to " + currentConfidentality + "."
        );
    }

    private void canChangeConfidentiality(DocumentEntity entity, EmployeeData employeeData) {
        if (entity.getStatus() != DocumentStatus.CONTENT_READY_TO_READ
            || entity.getStatus() != DocumentStatus.READY_TO_READ_AND_CONSULT) {
            throw new ForbiddenDocumentUpdateException("The document hasn't ready yet");
        }
        
        if (employeeData.hierarchy().equals("OPERATIVE")) {
            throw new ForbiddenDocumentUpdateException("Operative employees can´t update documents");
        }
        
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

    private boolean canRead(DocumentEntity entity, EmployeeData employeeData) {
        if (entity.getStatus() != DocumentStatus.CONTENT_READY_TO_READ) return false;
        
        return switch (entity.getConfidentialityLevel()) {
            // PUBLIC se lista a cualquier empleado: el controller ya rechazó
            // a externos (sin claim "area") antes de llegar aquí.
            case PUBLIC -> true;
            case PRIVATE_ALL_AREAS -> true;
            case PRIVATE_ONLY_AREA -> entity.getArea().equals(employeeData.area());
            case CONFIDENTIAL -> "DIRECTOR".equals(employeeData.hierarchy());
        };
    }

    public void scanResult(DocumentScanResultEvent event) {
        if (event.accept()) {
            acceptContentDocument(event.documentId());
        } else {
            rejectContentDocument(event.documentId(), event.findings());
        }
    }

    private void acceptContentDocument(String id) {
        DocumentEntity document = findDocumentById(UUID.fromString(id));
        document.toContentReadyToRead();

        documentRepository.save(document);

        // logica websockets para avisar a mi frontend que el documento ya está subido usando document.getTitle()
    }

    private void rejectContentDocument(String id, List<String> findings) {
        DocumentEntity document = findDocumentById(UUID.fromString(id));

        String filePath = document.getLocation();
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            boolean deleted = file.delete();
            if (deleted) {
                System.out.println("[Reject] Archivo físico eliminado: " + filePath);
            } else {
                System.err.println("[Reject] No se pudo eliminar el archivo físico: " + filePath);
            }
        }

        document.toContentRejected();

        rejectedDocumentRepository.save(
            RejectedDocumentEntity.create(document.getId(), document.getTitle(), findings)
        );

        documentRepository.save(document);

        // logica websockets para avisar a mi frontend que el documento ha sido rechazado usando document.getTitle()
    }

    public void changeStatus(DocumentStatusChangedEvent event) {
        DocumentEntity document = findDocumentById(UUID.fromString(event.documentId()));

        switch (event.statusChanged()) {
            case "INDEXING" -> document.toIndexing();
            case "READY_TO_READ_AND_CONSULT" -> {
                // logica websockets para avisar a mi frontend que el documento ya puede ser consultado por el RAG
                document.toReadyToReadAndConsult();
            }
            case "CONTENT_READY_TO_READ" -> document.toContentReadyToRead();
            default -> document.toContentReadyToRead();
        };

        documentRepository.save(document);
    }

    private DocumentEntity findDocumentById(UUID id) {
        return documentRepository.findById(id.toString())
                                    .orElseThrow(() -> new DocumentNotFoundException("The document with ID " + id + " doesn't exist."));
    }

}
