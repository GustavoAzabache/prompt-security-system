package com.me.microservices.rag_service.document.controller;

import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.me.microservices.rag_service.document.service.DocumentService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/rag/documents")
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping("/{id}/content")
    public ResponseEntity<Resource> getContent(
        @PathVariable UUID id,
        @RequestHeader("Authorization") String authorization
    ) {
        return ResponseEntity.ok().body(documentService.getContent(id, authorization));
    }

}
