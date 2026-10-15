-- Seed ejecutado por Hibernate DESPUÉS de generar el esquema (ddl-auto=create/create-drop).
-- Necesario porque con create-drop Flyway corre ANTES de que Hibernate borre y
-- recree todo, así que los INSERTs de las migraciones no sobreviven al arranque.
-- Cuando se migre a ddl-auto=validate, la fuente de seeds pasa a ser V1.
INSERT INTO areas (name, director_id, created_at, updated_at) VALUES
    ('TI', NULL, NOW(), NOW()),
    ('RRHH', NULL, NOW(), NOW()),
    ('ACCOUNTING', NULL, NOW(), NOW()),
    ('MARKETING', NULL, NOW(), NOW());
