package com.me.microservices.auth_service.employee.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.me.microservices.auth_service.area.entity.Area;
import com.me.microservices.auth_service.area.exception.AreaNotFoundException;
import com.me.microservices.auth_service.area.repository.AreaRepository;
import com.me.microservices.auth_service.common.dto.request.LoginRequest;
import com.me.microservices.auth_service.common.dto.request.RenewalAccessTokenRequest;
import com.me.microservices.auth_service.common.dto.response.AuthenticationResponse;
import com.me.microservices.auth_service.common.dto.response.RenewalAccessTokenResponse;
import com.me.microservices.auth_service.common.exception.EmailAlreadyUsedException;
import com.me.microservices.auth_service.common.exception.IncorrectCredentialException;
import com.me.microservices.auth_service.common.exception.InvalidDataException;
import com.me.microservices.auth_service.common.exception.UserNotFoundException;
import com.me.microservices.auth_service.employee.dto.request.RegisterEmployeeRequest;
import com.me.microservices.auth_service.employee.dto.response.PromoteEmployeeResponse;
import com.me.microservices.auth_service.employee.dto.response.UnpromoteEmployeeResponse;
import com.me.microservices.auth_service.employee.entity.EmployeeEntity;
import com.me.microservices.auth_service.employee.entity.enums.HierarchyLevel;
import com.me.microservices.auth_service.employee.repository.EmployeeRepository;
import com.me.microservices.auth_service.security.jwt.JwtService;
import com.me.microservices.auth_service.session.entity.AuthSessionEntity;
import com.me.microservices.auth_service.session.entity.enums.SessionUserType;
import com.me.microservices.auth_service.session.exception.SessionNotFoundException;
import com.me.microservices.auth_service.session.service.AuthSessionService;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AreaRepository areaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthSessionService authSessionService;

    public AuthenticationResponse register(RegisterEmployeeRequest request) {
        String cleanEmail = request.email().trim().toLowerCase();
        
        if (employeeRepository.existsByEmail(cleanEmail)) {
            throw new EmailAlreadyUsedException("Email is already registered");
        }

        Area area = areaRepository.findByName(request.area())
                    .orElseThrow(() -> new AreaNotFoundException("Area not found: " + request.area()));

        EmployeeEntity entity = employeeRepository.save(
            EmployeeEntity.create(
                request.firstName(),
                request.lastName(),
                area,
                cleanEmail,
                passwordEncoder.encode(request.password())
            )
        );
        
        AuthSessionEntity session = authSessionService.create(entity.getId(), SessionUserType.EMPLOYEE);
        String jti = session.getId().toString();

        return new AuthenticationResponse(
            jwtService.generateEmployeeAccessToken(
                entity.getId().toString(),
                entity.getArea().getName(),
                entity.getHierarchy().name(),
                jti
            ),
            jwtService.generateRefreshToken(entity.getId().toString(), jti)
        );
    }

    @Transactional
    public AuthenticationResponse login(LoginRequest request) {
        String cleanEmail = request.email().trim().toLowerCase();
        EmployeeEntity entity = employeeRepository.findByEmail(cleanEmail)
                                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.password(), entity.getPasswordHash())) {
            throw new IncorrectCredentialException("Incorrect email or password");
        }
        
        AuthSessionEntity session = authSessionService.create(entity.getId(), SessionUserType.EMPLOYEE);
        String jti = session.getId().toString();

        return new AuthenticationResponse(
            jwtService.generateEmployeeAccessToken(
                entity.getId().toString(),
                entity.getArea().getName(),
                entity.getHierarchy().name(),
                jti
            ),
            jwtService.generateRefreshToken(entity.getId().toString(), jti)
        );
    }

    @Transactional(readOnly = true)
    public RenewalAccessTokenResponse refresh(RenewalAccessTokenRequest request) {
        Claims claims = jwtService.validateRefreshToken(request.refreshToken());

        authSessionService.requireActive(parseJti(claims.getId()));

        EmployeeEntity entity = employeeRepository.findById(UUID.fromString(claims.getSubject()))
                                    .orElseThrow(() -> new UserNotFoundException("User not found"));

        return new RenewalAccessTokenResponse(
            jwtService.generateEmployeeAccessToken(
                entity.getId().toString(),
                entity.getArea().getName(),
                entity.getHierarchy().name(),
                claims.getId()                
            )
        );
    }

    private UUID parseJti(String jti) {
        try {
            return UUID.fromString(jti);
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new SessionNotFoundException("Invalid session");
        }
    }

    public PromoteEmployeeResponse promote(UUID id) {
        EmployeeEntity entity = findEntityById(id);

        String oldHierarchy = entity.getHierarchy().name();

        entity.promote();

        String currentHierarchy = entity.getHierarchy().name();

        return new PromoteEmployeeResponse("User promote from " + oldHierarchy + " to " + currentHierarchy + ".");
    }

    public UnpromoteEmployeeResponse unpromote(UUID id) {
        List<EmployeeEntity> directors = employeeRepository.findByHierarchyOrderByIdAsc(HierarchyLevel.DIRECTOR);
        
        if (directors.size() == 1 && directors.get(0).getId().equals(id)) {
            throw new InvalidDataException("Cannot demote the last DIRECTOR");
        }
        
        EmployeeEntity entity = findEntityById(id);

        String oldHierarchy = entity.getHierarchy().name();
        
        entity.unpromote();

        String currentHierarchy = entity.getHierarchy().name();

        return new UnpromoteEmployeeResponse("User unpromote from " + oldHierarchy + " to " + currentHierarchy + ".");
    }
    

    private EmployeeEntity findEntityById(UUID id) {
        return employeeRepository.findById(id)
                                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));
    }

}
