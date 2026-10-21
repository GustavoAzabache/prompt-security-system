package com.me.microservices.auth_service.session.service;

import java.time.Duration;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.me.microservices.auth_service.security.jwt.JwtService;
import com.me.microservices.auth_service.session.entity.AuthSessionEntity;
import com.me.microservices.auth_service.session.entity.enums.SessionUserType;
import com.me.microservices.auth_service.session.exception.SessionNotFoundException;
import com.me.microservices.auth_service.session.exception.SessionRevokedException;
import com.me.microservices.auth_service.session.repository.AuthSessionRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthSessionService {

    public static final String REVOKED_KEY_PREFIX = "revoked:";

    private final AuthSessionRepository authSessionRepository;
    private final JwtService jwtService;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public AuthSessionEntity create(UUID userId, SessionUserType userType) {
        return authSessionRepository.save(
            AuthSessionEntity.create(UUID.randomUUID(), userId, userType)
        );
    }

    @Transactional
    public AuthSessionEntity revoke(UUID sessionId) {
        AuthSessionEntity entity = findEntityById(sessionId);

        entity.revoke();

        return entity;
    }

    @Transactional(readOnly = true)
    public AuthSessionEntity requireActive(UUID sessionId) {
        AuthSessionEntity entity = findEntityById(sessionId);

        if (entity.isRevoked()) {
            throw new SessionRevokedException("Session is revoked: " + sessionId);
        }

        return entity;
    }

    @Transactional
    public void logout(String accessToken, String refreshToken) {
        Claims access = accessClaims(accessToken);
        Claims refresh = jwtService.validateRefreshToken(refreshToken);

        UUID accessJti = parseJti(access.getId());
        UUID refreshJti = parseJti(refresh.getId());

        if (!accessJti.equals(refreshJti) || !access.getSubject().equals(refresh.getSubject())) {
            throw new SessionNotFoundException("Token mismatch");
        }

        revoke(accessJti);

        long ttlSeconds = (access.getExpiration().getTime() - System.currentTimeMillis()) / 1000;
        if (ttlSeconds > 0) {
            redisTemplate.opsForValue().set(
                REVOKED_KEY_PREFIX + accessJti,
                "revoked",
                Duration.ofSeconds(ttlSeconds)
            );
        }
    }

    private Claims accessClaims(String accessToken) {
        try {
            return jwtService.validateAccessToken(accessToken);
        } catch (ExpiredJwtException expired) {
            return expired.getClaims();
        }
    }

    private UUID parseJti(String jti) {
        try {
            return UUID.fromString(jti);
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new SessionNotFoundException("Invalid session");
        }
    }

    private AuthSessionEntity findEntityById(UUID sessionId) {
        return authSessionRepository.findById(sessionId)
            .orElseThrow(() -> new SessionNotFoundException("Session not found: " + sessionId));
    }
}
