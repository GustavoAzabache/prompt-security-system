package com.me.microservices.auth_service.external.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.me.microservices.auth_service.external.entity.ExternalUserEntity;

public interface ExternalUserRepository extends JpaRepository<ExternalUserEntity, UUID> {

    Optional<ExternalUserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
