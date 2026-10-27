package com.me.microservices.auth_service.client.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.me.microservices.auth_service.client.dto.ClientAccessRequest;
import com.me.microservices.auth_service.client.service.ClientService;
import com.me.microservices.auth_service.common.dto.response.AuthenticationResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth/clients")
public class ClientController {

    private final ClientService clientService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody ClientAccessRequest request) {
        return ResponseEntity.ok(clientService.login(request));
    }

}
