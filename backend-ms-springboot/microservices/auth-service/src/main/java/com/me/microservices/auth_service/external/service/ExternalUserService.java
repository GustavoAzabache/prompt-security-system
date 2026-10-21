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
import com.me.microservices.auth_service.session.entity.AuthSessionEntity;
import com.me.microservices.auth_service.session.entity.enums.SessionUserType;
import com.me.microservices.auth_service.session.exception.SessionNotFoundException;
import com.me.microservices.auth_service.session.service.AuthSessionService;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class ExternalUserService {

    private final ExternalUserRepository externalUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthSessionService authSessionService;

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

        AuthSessionEntity session = authSessionService.create(entity.getId(), SessionUserType.EXTERNAL);
        String jti = session.getId().toString();

        return new AuthenticationResponse(
            jwtService.generateExternalAccessToken(entity.getId().toString(), jti),
            jwtService.generateRefreshToken(entity.getId().toString(), jti)
        );
    }

    @Transactional
    public AuthenticationResponse login(LoginRequest request) {
        String cleanEmail = request.email().trim().toLowerCase();
        ExternalUserEntity entity = externalUserRepository.findByEmail(cleanEmail)
                                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.password(), entity.getPasswordHash())) {
            throw new IncorrectCredentialException("Incorrect email or password");
        }

        AuthSessionEntity session = authSessionService.create(entity.getId(), SessionUserType.EXTERNAL);
        String jti = session.getId().toString();

        return new AuthenticationResponse(
            jwtService.generateExternalAccessToken(entity.getId().toString(), jti),
            jwtService.generateRefreshToken(entity.getId().toString(), jti)
        );
    }

    @Transactional(readOnly = true)
    public RenewalAccessTokenResponse refresh(RenewalAccessTokenRequest request) {
        Claims claims = jwtService.validateRefreshToken(request.refreshToken());

        authSessionService.requireActive(parseJti(claims.getId()));

        ExternalUserEntity entity = externalUserRepository.findById(UUID.fromString(claims.getSubject()))
                                         .orElseThrow(() -> new UserNotFoundException("User not found"));

        return new RenewalAccessTokenResponse(
            jwtService.generateExternalAccessToken(entity.getId().toString(), claims.getId())
        );
    }

    private UUID parseJti(String jti) {
        try {
            return UUID.fromString(jti);
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new SessionNotFoundException("Invalid session");
        }
    }

}
