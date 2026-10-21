package com.me.microservices.auth_service.session.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.me.microservices.auth_service.common.exception.InvalidDataException;
import com.me.microservices.auth_service.session.entity.enums.SessionUserType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "auth_sessions")
public class AuthSessionEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, updatable = false)
    private SessionUserType userType;

    @Column(name = "is_revoked", nullable = false)
    private boolean revoked;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    public static AuthSessionEntity create(UUID id, UUID userId, SessionUserType userType) {
        validate(id, userId, userType);

        return new AuthSessionEntity(
            id,
            userId,
            userType,
            false,
            LocalDateTime.now(),
            null
        );
    }

    private static void validate(UUID id, UUID userId, SessionUserType userType) {
        if (id == null) {
            throw new InvalidDataException("Session id (jti) is required");
        }

        if (userId == null) {
            throw new InvalidDataException("User id is required");
        }

        if (userType == null) {
            throw new InvalidDataException("User type is required");
        }
    }

    public void revoke() {
        if (!this.revoked) {
            this.revoked = true;
            this.revokedAt = LocalDateTime.now();
        }
    }
}
