package com.me.microservices.auth_service.client.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.me.microservices.auth_service.client.dto.ClientAccessRequest;
import com.me.microservices.auth_service.client.entity.ClientEntity;
import com.me.microservices.auth_service.client.repository.ClientRepository;
import com.me.microservices.auth_service.common.dto.response.AuthenticationResponse;
import com.me.microservices.auth_service.common.exception.IncorrectCredentialException;
import com.me.microservices.auth_service.security.jwt.JwtService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ClientService {

    private static final String CONTENT_INTEGRITY_SCOPE = "documents:scan";

    private final ClientRepository clientRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public AuthenticationResponse login(ClientAccessRequest request) {
        ClientEntity client = clientRepository.findByName(request.name())
                        .orElseThrow(() -> new IncorrectCredentialException("Invalid client credentials"));

        if (!passwordEncoder.matches(request.password(), client.getPasswordHash())) {
            throw new IncorrectCredentialException("Invalid client credentials");
        }

        return new AuthenticationResponse(
            jwtService.generateClientAccessToken(client.getId().toString(), client.getName(), CONTENT_INTEGRITY_SCOPE),
            null
        );
    }

}
