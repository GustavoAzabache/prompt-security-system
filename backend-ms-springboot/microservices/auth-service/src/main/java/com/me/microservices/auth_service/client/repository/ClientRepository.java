package com.me.microservices.auth_service.client.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.me.microservices.auth_service.client.entity.ClientEntity;

public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {
    boolean existsByName(String name);

    Optional<ClientEntity> findByName(String name);
}
