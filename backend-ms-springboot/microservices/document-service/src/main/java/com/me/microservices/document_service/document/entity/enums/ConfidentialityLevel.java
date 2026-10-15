package com.me.microservices.document_service.document.entity.enums;

public enum ConfidentialityLevel {
    PUBLIC, // Include: Employees + External Users
    PRIVATE_ALL_AREAS, // Any employee from any area can read
    PRIVATE_ONLY_AREA, // Any employee only from the specific area can read
    CONFIDENTIAL // Only employee with DIRECTOR hierarchy
}