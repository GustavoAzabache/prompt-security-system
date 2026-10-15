package com.me.microservices.document_service.document.dto.request;

import com.me.microservices.document_service.document.entity.enums.ConfidentialityLevel;

import jakarta.validation.constraints.NotNull;

public record ChangeConfidentialityRequest(

    @NotNull(message = "The Confidentiality Level is required")
    ConfidentialityLevel confidentialityLevel
) {}
