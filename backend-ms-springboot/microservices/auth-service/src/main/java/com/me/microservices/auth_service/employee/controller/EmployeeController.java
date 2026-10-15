package com.me.microservices.auth_service.employee.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.me.microservices.auth_service.common.dto.request.LoginRequest;
import com.me.microservices.auth_service.common.dto.request.RenewalAccessTokenRequest;
import com.me.microservices.auth_service.common.dto.response.AuthenticationResponse;
import com.me.microservices.auth_service.common.dto.response.RenewalAccessTokenResponse;
import com.me.microservices.auth_service.employee.dto.request.RegisterEmployeeRequest;
import com.me.microservices.auth_service.employee.dto.response.PromoteEmployeeResponse;
import com.me.microservices.auth_service.employee.dto.response.UnpromoteEmployeeResponse;
import com.me.microservices.auth_service.employee.service.EmployeeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterEmployeeRequest request) {
        return ResponseEntity.ok(employeeService.register(request));
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(employeeService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RenewalAccessTokenResponse> refresh(
        @Valid @RequestBody RenewalAccessTokenRequest request
    ) {
        return ResponseEntity.ok(employeeService.refresh(request));
    }

    @PatchMapping("/{id}/promote")
    public ResponseEntity<PromoteEmployeeResponse> promote(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeService.promote(id));
    }

    @PatchMapping("/{id}/unpromote")
    public ResponseEntity<UnpromoteEmployeeResponse> unpromote(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeService.unpromote(id));
    }
    

}
