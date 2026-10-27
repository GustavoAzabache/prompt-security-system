CREATE TABLE IF NOT EXISTS clients (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_clients_name ON clients (name);
