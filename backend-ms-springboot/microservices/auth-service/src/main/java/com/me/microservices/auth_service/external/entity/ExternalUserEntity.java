package com.me.microservices.auth_service.external.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.me.microservices.auth_service.common.exception.InvalidDataException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter 
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "external_users")
public class ExternalUserEntity {

    @Id
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true, updatable = false)
    private String email;

    @Column (name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static ExternalUserEntity create(String firstName, String lastName, String email, String passwordHash) {
        validate(firstName, lastName, email, passwordHash);
        
        LocalDateTime now = LocalDateTime.now();
        
        return new ExternalUserEntity(UUID.randomUUID(), firstName, lastName, email, passwordHash, now, now);
    }

    private static void validate(
        String firstName,
        String lastName,
        String email,
        String passwordHash
    ) {
        if (firstName == null || firstName.isBlank()) {
            throw new InvalidDataException("First name is required");
        }

        if (lastName == null || lastName.isBlank()) {
            throw new InvalidDataException("Last name is required");
        }

        if (email == null || email.isBlank()) {
            throw new InvalidDataException("Email is required");
        }

        if (passwordHash == null || passwordHash.isBlank()) {
            throw new InvalidDataException("Password hash is required");
        }
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
