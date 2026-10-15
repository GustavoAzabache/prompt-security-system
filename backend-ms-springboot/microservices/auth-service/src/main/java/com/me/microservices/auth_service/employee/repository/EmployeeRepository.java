package com.me.microservices.auth_service.employee.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.me.microservices.auth_service.employee.entity.EmployeeEntity;
import com.me.microservices.auth_service.employee.entity.enums.HierarchyLevel;

import jakarta.persistence.LockModeType;


public interface EmployeeRepository extends JpaRepository<EmployeeEntity, UUID> {

    Optional<EmployeeEntity> findByEmail(String email);
    
    boolean existsByEmail(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<EmployeeEntity> findByHierarchyOrderByIdAsc(HierarchyLevel hierarchy);

}
