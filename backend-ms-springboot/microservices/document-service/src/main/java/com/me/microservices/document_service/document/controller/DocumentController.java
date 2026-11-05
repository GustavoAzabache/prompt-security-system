package com.me.microservices.document_service.document.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.me.microservices.document_service.document.dto.DocumentContent;
import com.me.microservices.document_service.document.dto.EmployeeData;
import com.me.microservices.document_service.document.dto.request.ChangeConfidentialityRequest;
import com.me.microservices.document_service.document.dto.response.ChangeConfidentialityResponse;
import com.me.microservices.document_service.document.dto.response.DocumentResponse;
import com.me.microservices.document_service.document.dto.response.UploadDocumentResponse;
import com.me.microservices.document_service.document.service.DocumentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getAll(
        @RequestParam("pageNumber") int pageNumber,
        @AuthenticationPrincipal Jwt jwt
    ) {
        Pageable pageable = PageRequest.of(pageNumber, 10, Sort.by(Direction.ASC, "createdAt"));

        return ResponseEntity.ok(documentService.getAll(pageable, getEmployeeData(jwt)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getById(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(documentService.getById(id, getEmployeeData(jwt)));
    }

    @GetMapping("/{id}/content")
    public ResponseEntity<Resource> getContent(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        DocumentContent content = documentService.getContent(id, getEmployeeData(jwt));
        String safeFilename = content.fileName().replace("\"", "").replace("\n", "").replace("\r", "");

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(content.mediaType()))
            .contentLength(content.contentLength())
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "inline; filename=\"" + safeFilename + "\""
            )
            .body(content.resource());
    }

    @GetMapping("/not-index")
    public ResponseEntity<List<DocumentResponse>> getAllWithStatusContentReadyToRead(
        @RequestParam("pageNumber") int pageNumber,
        @AuthenticationPrincipal Jwt jwt
    ) {
        Pageable pageable = PageRequest.of(pageNumber, 10, Sort.by(Direction.ASC, "createdAt"));

        return ResponseEntity.ok(documentService.getAllWithStatusContentReadyToRead(pageable, getEmployeeData(jwt)));
    }
        
    @PostMapping
    public ResponseEntity<UploadDocumentResponse> upload(
        @RequestParam("file") MultipartFile file,
        @AuthenticationPrincipal Jwt jwt
    ) {
        UploadDocumentResponse response = documentService.upload(file, getEmployeeData(jwt));
        
        return ResponseEntity.accepted().body(response);
    }

    @PatchMapping("/{id}/change-confidentiality")
    public ResponseEntity<ChangeConfidentialityResponse> changeConfidentiality(
        @PathVariable UUID id,
        @Valid @RequestBody ChangeConfidentialityRequest request,
        @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.accepted().body(documentService.changeConfidentiality(id, request, getEmployeeData(jwt)));
    }

    private EmployeeData getEmployeeData(Jwt jwt) {
        return new EmployeeData(
            jwt.getSubject(),
            jwt.getClaimAsString("area"),
            jwt.getClaimAsString("hierarchy")
        );
    }

}