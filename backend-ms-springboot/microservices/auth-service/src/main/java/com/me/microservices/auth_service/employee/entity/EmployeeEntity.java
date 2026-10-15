package com.me.microservices.auth_service.employee.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.me.microservices.auth_service.area.entity.Area;
import com.me.microservices.auth_service.common.exception.InvalidDataException;
import com.me.microservices.auth_service.employee.entity.enums.HierarchyLevel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "employees")
public class EmployeeEntity {

    @Id
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "area_id", nullable = true)
    private Area area;

    @Enumerated(EnumType.STRING)
    @Column(name = "hierarchy", nullable = true)
    private HierarchyLevel hierarchy;

    @Column(nullable = false, unique = true, updatable = false)
    private String email;

    @Column (name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static EmployeeEntity create(String firstName, String lastName, Area area, String email, String passwordHash) {
        validate(firstName, lastName, area, email, passwordHash);
        
        LocalDateTime now = LocalDateTime.now();
        
        return new EmployeeEntity(
            UUID.randomUUID(),
            firstName,
            lastName,
            area,
            HierarchyLevel.OPERATIVE,  // For security, all external request create a OPERATIVE employee
            email,
            passwordHash,
            now,
            now
        );
    }

    private static void validate(
        String firstName,
        String lastName,
        Area area,
        String email,
        String passwordHash
    ) {
        if (firstName == null || firstName.isBlank()) {
            throw new InvalidDataException("First name is required");
        }

        if (lastName == null || lastName.isBlank()) {
            throw new InvalidDataException("Last name is required");
        }

        if (area == null) {
            throw new InvalidDataException("Area is required for employees");
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

    public void promote() {
        HierarchyLevel actual = this.hierarchy;

        this.hierarchy = switch(actual) {
            case OPERATIVE -> HierarchyLevel.JEFE;
            case JEFE -> HierarchyLevel.GERENTE;
            case GERENTE -> HierarchyLevel.DIRECTOR;
            case DIRECTOR -> HierarchyLevel.DIRECTOR;
        };
    }

    public void unpromote() {
        HierarchyLevel actual = this.hierarchy;

        this.hierarchy = switch(actual) {
            case OPERATIVE -> HierarchyLevel.OPERATIVE;
            case JEFE -> HierarchyLevel.OPERATIVE;
            case GERENTE -> HierarchyLevel.JEFE;
            case DIRECTOR -> HierarchyLevel.GERENTE;
        };
    }
    
}
