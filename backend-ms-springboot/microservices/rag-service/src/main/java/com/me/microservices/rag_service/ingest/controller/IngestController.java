package com.me.microservices.rag_service.ingest.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.me.microservices.rag_service.ingest.dto.response.IngestDocumentResponse;
import com.me.microservices.rag_service.ingest.exception.ForbiddenIngestException;
import com.me.microservices.rag_service.ingest.service.IngestService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/rag/documents")
public class IngestController {

    private final IngestService ingestService;

    @PostMapping("/{id}/ingest")
    public ResponseEntity<IngestDocumentResponse> ingest(
        @PathVariable UUID id,
        @RequestHeader("Authorization") String authorization,
        @RequestParam(defaultValue = "false") boolean force,
        @AuthenticationPrincipal Jwt jwt
    ) {
        if (jwt.getClaimAsString("area") == null) {
            throw new ForbiddenIngestException("Only employees can ingest documents");
        }

        return ResponseEntity.ok(ingestService.ingest(id, authorization, force));
    }
}
