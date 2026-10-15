package com.me.microservices.auth_service.external.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.me.microservices.auth_service.common.dto.request.LoginRequest;
import com.me.microservices.auth_service.common.dto.request.RenewalAccessTokenRequest;
import com.me.microservices.auth_service.common.dto.response.AuthenticationResponse;
import com.me.microservices.auth_service.common.dto.response.RenewalAccessTokenResponse;
import com.me.microservices.auth_service.external.dto.request.RegisterExternalRequest;
import com.me.microservices.auth_service.external.service.ExternalUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth/external-users")
public class ExternalUserController {

    private final ExternalUserService externalUserService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterExternalRequest request) {
        return ResponseEntity.ok(externalUserService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(externalUserService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RenewalAccessTokenResponse> refresh(
        @Valid @RequestBody RenewalAccessTokenRequest request
    ) {
        return ResponseEntity.ok(externalUserService.refresh(request));
    }
    

}
