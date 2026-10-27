package com.me.microservices.document_service.internal.controller;

import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.me.microservices.document_service.document.dto.DocumentContent;
import com.me.microservices.document_service.internal.service.InternalService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/documents/internal")
public class InternalController {

    private final InternalService internalService;

    @GetMapping("/{id}/content")
    public ResponseEntity<Resource> getContent(@PathVariable UUID id) {
        DocumentContent content = internalService.getContent(id);
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
    

}
