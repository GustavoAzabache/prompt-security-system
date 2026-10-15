package com.me.microservices.rag_service.ask.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.me.microservices.rag_service.ask.dto.request.AskRequest;
import com.me.microservices.rag_service.ask.dto.response.AskResponse;
import com.me.microservices.rag_service.ask.service.AskService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/rag/ask")
public class AskController {

    private final AskService askService;

    @PostMapping
    public ResponseEntity<AskResponse> ask(
        @RequestBody AskRequest request,
        @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(askService.ask(request.query(), request.topK(), jwt));
    }
}
