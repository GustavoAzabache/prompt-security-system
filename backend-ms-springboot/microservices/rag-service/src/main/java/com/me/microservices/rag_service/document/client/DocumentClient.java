package com.me.microservices.rag_service.document.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.me.microservices.rag_service.document.dto.DocumentMetadata;

@FeignClient(name = "document-service")
public interface DocumentClient {

    @GetMapping("/api/v1/documents/{id}/content")
    ResponseEntity<Resource> getContent(
        @PathVariable UUID id,
        @RequestHeader("Authorization") String authorization
    );

    @GetMapping("/api/v1/documents/{id}")
    DocumentMetadata getMetadata(
        @PathVariable UUID id,
        @RequestHeader("Authorization") String authorization
    );
}
