package com.me.microservices.auth_service.session.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.me.microservices.auth_service.session.entity.AuthSessionEntity;

public interface AuthSessionRepository extends JpaRepository<AuthSessionEntity, UUID> {

    List<AuthSessionEntity> findByUserId(UUID userId);
}
