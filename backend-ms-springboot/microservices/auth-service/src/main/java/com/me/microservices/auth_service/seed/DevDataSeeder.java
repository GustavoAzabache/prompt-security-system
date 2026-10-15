package com.me.microservices.auth_service.seed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.me.microservices.auth_service.area.entity.Area;
import com.me.microservices.auth_service.area.repository.AreaRepository;
import com.me.microservices.auth_service.employee.entity.EmployeeEntity;
import com.me.microservices.auth_service.employee.repository.EmployeeRepository;
import com.me.microservices.auth_service.external.entity.ExternalUserEntity;
import com.me.microservices.auth_service.external.repository.ExternalUserRepository;

import lombok.RequiredArgsConstructor;

/**
 * TEMPORAL (dev): siembra usuarios de prueba. Eliminar antes de producción.
 * Requiere las áreas (Flyway V1 en validate, import.sql en create-drop).
 */
@Component
@RequiredArgsConstructor
public class DevDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataSeeder.class);

    private static final String DEV_PASSWORD = "Password123";
    private static final String AREA_TI = "TI";
    private static final String AREA_MARKETING = "MARKETING";

    private final EmployeeRepository employeeRepository;
    private final ExternalUserRepository externalUserRepository;
    private final AreaRepository areaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        Area ti = areaRepository.findByName(AREA_TI)
                .orElseThrow(() -> new IllegalStateException(
                        "DevDataSeeder requiere el área TI (Flyway V1 / import.sql)"));
        Area marketing = areaRepository.findByName(AREA_MARKETING)
                .orElseThrow(() -> new IllegalStateException(
                        "DevDataSeeder requiere el área MARKETING (Flyway V1 / import.sql)"));

        seedEmployee("director@demo.test", "Adan", "Director", ti, 3);
        seedEmployee("gerente@demo.test", "Eva", "Gerente", ti, 2);
        seedEmployee("jefe@demo.test", "Caín", "Jefe", ti, 1);
        seedEmployee("operativo@demo.test", "Abel", "Operativo", ti, 0);

        seedEmployee("director-marketing@demo.test", "Adana", "Directora", marketing, 3);
        seedEmployee("gerente-marketing@demo.test", "Evana", "Gerenta", marketing, 2);
        seedEmployee("jefe-marketing@demo.test", "Caína", "Jefa", marketing, 1);
        seedEmployee("operativo-marketing@demo.test", "Abela", "Operativa", marketing, 0);

        if (externalUserRepository.existsByEmail("externo@demo.test")) {
            log.info("DevDataSeeder: externo@demo.test ya existe, se omite");
        } else {
            externalUserRepository.save(ExternalUserEntity.create(
                    "Enoc", "Externo", "externo@demo.test",
                    passwordEncoder.encode(DEV_PASSWORD)));
            log.info("DevDataSeeder: creado externo@demo.test");
        }

        log.warn("DevDataSeeder TEMPORAL activo: usuarios demo con password '{}'. Eliminar antes de producción.",
                DEV_PASSWORD);
    }

    private void seedEmployee(String email, String firstName, String lastName, Area area, int promotions) {
        if (employeeRepository.existsByEmail(email)) {
            log.info("DevDataSeeder: {} ya existe, se omite", email);
            return;
        }
        EmployeeEntity entity = EmployeeEntity.create(
                firstName, lastName, area, email, passwordEncoder.encode(DEV_PASSWORD));
        for (int i = 0; i < promotions; i++) {
            entity.promote();
        }
        employeeRepository.save(entity);
        log.info("DevDataSeeder: creado {} ({} promociones)", email, promotions);
    }
}
