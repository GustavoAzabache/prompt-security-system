package com.me.microservices.auth_service.employee.entity.enums;

import lombok.Getter;

@Getter
public enum HierarchyLevel {
    OPERATIVE(10),
    JEFE(20),
    GERENTE(30),
    DIRECTOR(40);

    private final int authorityLevel;

    HierarchyLevel(int authorityLevel) {
        this.authorityLevel = authorityLevel;
    }
}