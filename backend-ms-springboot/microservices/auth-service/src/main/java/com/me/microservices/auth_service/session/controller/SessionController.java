package com.me.microservices.auth_service.session.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.me.microservices.auth_service.session.dto.request.LogoutRequest;
import com.me.microservices.auth_service.session.dto.response.LogoutResponse;
import com.me.microservices.auth_service.session.service.AuthSessionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth/sessions")
public class SessionController {

    private final AuthSessionService authSessionService;

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(@Valid @RequestBody LogoutRequest request) {
        authSessionService.logout(request.accessToken(), request.refreshToken());

        return ResponseEntity.ok(new LogoutResponse("Session closed"));
    }
}
