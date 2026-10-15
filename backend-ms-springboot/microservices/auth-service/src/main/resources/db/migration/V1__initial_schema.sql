CREATE TABLE areas (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    director_id UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE employees (
    id UUID PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    area_id BIGINT REFERENCES areas(id),
    hierarchy VARCHAR(20),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE external_users (
    id UUID PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE UNIQUE INDEX ux_employees_email_lower ON employees (LOWER(email));
CREATE UNIQUE INDEX ux_external_users_email_lower ON external_users (LOWER(email));

INSERT INTO areas (name, director_id, created_at, updated_at) VALUES
    ('TI', NULL, NOW(), NOW()),
    ('RRHH', NULL, NOW(), NOW()),
    ('ACCOUNTING', NULL, NOW(), NOW());
