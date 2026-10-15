package com.me.microservices.auth_service.area.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.me.microservices.auth_service.area.entity.Area;


public interface AreaRepository extends JpaRepository<Area, Long> {
    Optional<Area> findByName(String name);
}
