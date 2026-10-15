package com.me.microservices.auth_service.external.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.me.microservices.auth_service.common.dto.request.LoginRequest;
import com.me.microservices.auth_service.common.dto.request.RenewalAccessTokenRequest;
import com.me.microservices.auth_service.common.dto.response.AuthenticationResponse;
import com.me.microservices.auth_service.common.dto.response.RenewalAccessTokenResponse;
import com.me.microservices.auth_service.common.exception.EmailAlreadyUsedException;
import com.me.microservices.auth_service.common.exception.IncorrectCredentialException;
import com.me.microservices.auth_service.common.exception.UserNotFoundException;
import com.me.microservices.auth_service.external.dto.request.RegisterExternalRequest;
import com.me.microservices.auth_service.external.entity.ExternalUserEntity;
import com.me.microservices.auth_service.external.repository.ExternalUserRepository;
import com.me.microservices.auth_service.security.jwt.JwtService;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class ExternalUserService {

    private final ExternalUserRepository externalUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthenticationResponse register(RegisterExternalRequest request) {
        String cleanEmail = request.email().trim().toLowerCase();
        
        if (externalUserRepository.existsByEmail(cleanEmail)) {
            throw new EmailAlreadyUsedException("Email is already registered");
        }

        ExternalUserEntity entity = externalUserRepository.save(
            ExternalUserEntity.create(
                request.firstName(),
                request.lastName(),
                cleanEmail,
                passwordEncoder.encode(request.password())
            )
        );

        return new AuthenticationResponse(
            jwtService.generateExternalAccessToken(entity.getId().toString()),
            jwtService.generateRefreshToken(entity.getId().toString())
        );
    }

    @Transactional(readOnly = true)
    public AuthenticationResponse login(LoginRequest request) {
        String cleanEmail = request.email().trim().toLowerCase();
        ExternalUserEntity entity = externalUserRepository.findByEmail(cleanEmail)
                                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.password(), entity.getPasswordHash())) {
            throw new IncorrectCredentialException("Incorrect email or password");
        }

        return new AuthenticationResponse(
            jwtService.generateExternalAccessToken(entity.getId().toString()),
            jwtService.generateRefreshToken(entity.getId().toString())
        );
    }

    @Transactional(readOnly = true)
    public RenewalAccessTokenResponse refresh(RenewalAccessTokenRequest request) {
        Claims claims = jwtService.validateRefreshToken(request.refreshToken());

        ExternalUserEntity entity = externalUserRepository.findById(UUID.fromString(claims.getSubject()))
                                         .orElseThrow(() -> new UserNotFoundException("User not found"));

        return new RenewalAccessTokenResponse(
            jwtService.generateExternalAccessToken(entity.getId().toString())
        );
    }

}
