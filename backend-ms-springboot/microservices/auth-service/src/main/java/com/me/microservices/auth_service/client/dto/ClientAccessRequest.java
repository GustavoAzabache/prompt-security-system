package com.me.microservices.auth_service.client.dto;

import jakarta.validation.constraints.NotBlank;

public record ClientAccessRequest(
    @NotBlank(message = "Client name is required")
    String name,

    @NotBlank(message = "Client password is required")
    String password
) {}
