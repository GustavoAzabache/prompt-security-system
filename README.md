# prompt-security-system

Estado: en desarrollo

Este proyecto aún está en desarrollo. Algunas funcionalidades pueden cambiar o no estar disponibles todavía.

## ¿Qué es?

Sistema fullstack de gestión de manuales con control de acceso. El backend son microservicios Spring Boot detrás de un API Gateway (config centralizada en Config Server, descubrimiento en Eureka). El frontend es una app React + TypeScript que consume el gateway.

## ¿Qué problema resuelve?

- Centraliza manuales en un solo lugar con niveles de confidencialidad por área y jerarquía.
- Separa empleados (con área y jerarquía) de usuarios externos.
- Permite ingerir documentos a una base vectorial y hacer preguntas sobre ellos (RAG).

## Tecnologías

Java 25, Spring Boot 4.1.1, Spring Cloud (Config, Eureka, Gateway), Spring AI 2.0.1, Maven, PostgreSQL 17, MongoDB 7.0.12, pgvector, Docker y Docker Compose, TypeScript, React 19, React Router 7, Vite 8, Tailwind CSS 4, Axios, React Hook Form, Zod.

## Estructura del proyecto

```text
prompt-security-system/
  backend-ms-springboot/
    api-gateway/         # Gateway y seguridad JWT, puerto 8080
    config-server/       # Configuración central (src/main/resources/config/*.yaml), puerto 8888
    discovery-server/    # Eureka, puerto 8761
    microservices/
      auth-service/      # Registro/login/refresh, JWT, Flyway, puerto 8081
      document-service/  # Subida y lectura de PDFs + MongoDB, puerto 8082
      rag-service/       # Ingesta a pgvector y preguntas con IA, puerto 8083
    postgres-init/       # 01-create-databases.sql
    docker-compose.yml
    pom.xml
    .env                 # Variables locales (no se versiona)
  frontend-react-typescript/
    src/
      pages/             # Home, Login, Register, Documents, Admin
      routes/            # AppRouter, paths, ProtectedRoute
      services/          # documentService, employeeService, externalUserService
      components/ config/ lib/
    vite.config.ts
    package.json
```

## Requisitos previos

- Java 25
- Maven (hay wrapper `mvnw` en cada microservicio)
- Node.js LTS + npm
- Docker + Docker Compose
- Una `GOOGLE_API_KEY` válida (la usa `rag-service`)

## Cómo clonarlo

No hay remoto git configurado en este entorno, así que entra al directorio del proyecto:

```bash
cd prompt-security-system
```

## Cómo levantarlo

### 1. Dependencias

```bash
# Frontend
cd frontend-react-typescript
npm install
```

El backend se compila dentro de Docker (`mvn clean package -DskipTests`), no hace falta instalar nada más en local.

### 2. Variables de entorno

No hay `.env.example` en el repo. Crea los `.env` a mano con estos nombres (sin valores reales aquí):

Crea `backend-ms-springboot/.env`:

| Variable | Para qué sirve | Obligatoria |
|---|---|---|
| `POSTGRESQL_DB_PORT` | Puerto local de PostgreSQL (auth) | Sí |
| `POSTGRESQL_DB_USERNAME` | Usuario de PostgreSQL | Sí |
| `POSTGRESQL_DB_PASSWORD` | Contraseña de PostgreSQL | Sí |
| `MONGO_DB_PORT` | Puerto local de MongoDB (documents) | Sí |
| `MONGO_DB_USERNAME` | Usuario root de MongoDB | Sí |
| `MONGO_DB_PASSWORD` | Contraseña root de MongoDB | Sí |
| `CONFIG_SERVER_PORT` | Puerto de config-server | Sí |
| `DISCOVERY_SERVER_PORT` | Puerto de Eureka | Sí |
| `API_GATEWAY_PORT` | Puerto del gateway | Sí |
| `AUTH_SERVICE_PORT` | Puerto de auth-service | Sí |
| `AUTH_SERVICE_DB_NAME` | Nombre de la BD de auth (`prompt_sec_auth_ms`) | Sí |
| `DOCUMENT_SERVICE_PORT` | Puerto de document-service | Sí |
| `DOCUMENT_SERVICE_DB_NAME` | Nombre de la BD de documents en Mongo | Sí |
| `RAG_POSTGRES_PORT` | Puerto local del PostgreSQL con pgvector | Sí |
| `RAG_SERVICE_PORT` | Puerto de rag-service | Sí |
| `RAG_SERVICE_DB_NAME` | Nombre de la BD de rag | Sí |
| `GOOGLE_API_KEY` | Clave para embeddings y chat de GenAI | Sí |

Crea `frontend-react-typescript/.env`:

| Variable | Para qué sirve | Obligatoria |
|---|---|---|
| `VITE_API_BASE_URL` | URL base del gateway, ej. `http://localhost:8080` | Sí |

### 3. Base de datos

No hay que ejecutar migraciones a mano:

- `postgres-init/01-create-databases.sql` crea la BD de auth al arrancar el contenedor `postgres`.
- `auth-service` aplica migraciones con Flyway al arrancar.
- MongoDB y pgvector se crean con los volúmenes de Compose (`mongo-data`, `pgvector-data`, `postgres-data`).

### 4. Arranque

```bash
# Backend completo (postgres, mongo, pgvector + 6 servicios Java)
cd backend-ms-springboot
docker compose up --build
```

```bash
# Frontend (otra terminal)
cd frontend-react-typescript
npm run dev
```

El frontend queda en `http://localhost:5173` (origen permitido por el CORS del gateway). El gateway queda en `http://localhost:8080`.

Producción local del frontend (usa el `build` real del proyecto):

```bash
cd frontend-react-typescript
npm run build
npm run preview
```

## Cómo probarlo

No hay suite de tests con comando único documentado (solo tests de arranque `*ApplicationTests` por servicio y sin script de test en el frontend), así que la forma de probarlo es con la API:

```bash
# 1. Login de empleado (público)
curl -X POST http://localhost:8080/api/v1/auth/employees/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"empleado@empresa.com","password":"secreto123"}'
# Devuelve accessToken y refreshToken.

# 2. Subir un documento (autenticado, solo empleados)
curl -X POST http://localhost:8080/api/v1/documents \
  -H "Authorization: Bearer $TOKEN" \
  -F 'file=@manual.pdf'

# 3. Preguntar al RAG (autenticado)
curl -X POST http://localhost:8080/api/v1/rag/ask \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"query":"¿Qué dice el manual sobre seguridad?","topK":5}'
```

No hay Swagger/OpenAPI ni colección Postman en el repo.

## Endpoints

**Base URL:** `http://localhost:8080` (todo pasa por el gateway).

### Auth — empleados

| Método | Ruta | Descripción | Auth | Roles |
|---|---|---|---|---|
| POST | `/api/v1/auth/employees/register` | Registra un empleado y devuelve tokens | Sí (Bearer JWT) | JEFE, GERENTE o DIRECTOR |
| POST | `/api/v1/auth/employees/login` | Login de empleado, devuelve tokens | No | - |
| POST | `/api/v1/auth/employees/refresh` | Renueva el access token con el refresh token | No | - |
| PATCH | `/api/v1/auth/employees/{id}/promote` | Asciende a un empleado | Sí (Bearer JWT) | DIRECTOR |
| PATCH | `/api/v1/auth/employees/{id}/unpromote` | Revierte el ascenso de un empleado | Sí (Bearer JWT) | DIRECTOR |

**POST `/api/v1/auth/employees/register`**
- Body: `firstName` (string, obligatorio), `lastName` (string, obligatorio), `area` (string, obligatorio), `email` (string, obligatorio), `password` (string, obligatorio, 8-50 caracteres)

**POST `/api/v1/auth/employees/login`**
- Body: `email` (string, obligatorio), `password` (string, obligatorio)

**POST `/api/v1/auth/employees/refresh`**
- Body: `refreshToken` (string, obligatorio)

### Auth — usuarios externos

| Método | Ruta | Descripción | Auth | Roles |
|---|---|---|---|---|
| POST | `/api/v1/auth/external-users/register` | Registra un usuario externo y devuelve tokens | No | - |
| POST | `/api/v1/auth/external-users/login` | Login de usuario externo, devuelve tokens | No | - |
| POST | `/api/v1/auth/external-users/refresh` | Renueva el access token con el refresh token | No | - |

**POST `/api/v1/auth/external-users/register`**
- Body: `firstName` (string, obligatorio), `lastName` (string, obligatorio), `email` (string, obligatorio), `password` (string, obligatorio, 8-50 caracteres)

**POST `/api/v1/auth/external-users/login`**
- Body: `email` (string, obligatorio), `password` (string, obligatorio)

**POST `/api/v1/auth/external-users/refresh`**
- Body: `refreshToken` (string, obligatorio)

### Documentos

| Método | Ruta | Descripción | Auth | Roles |
|---|---|---|---|---|
| GET | `/api/v1/documents?pageNumber=0` | Lista documentos paginados de 10 en 10 | Sí (Bearer JWT) | Empleados (con área) |
| GET | `/api/v1/documents/{id}` | Obtiene el metadato de un documento | Sí (Bearer JWT) | Empleados (con área) |
| GET | `/api/v1/documents/{id}/content` | Descarga el PDF | Sí (Bearer JWT) | Empleados (con área) |
| POST | `/api/v1/documents` | Sube un PDF | Sí (Bearer JWT) | Empleados (con área) |
| PATCH | `/api/v1/documents/{id}/change-confidentiality` | Cambia el nivel de confidencialidad | Sí (Bearer JWT) | Empleados con jerarquía distinta de OPERATIVE |

**POST `/api/v1/documents`**
- Body: `file` (multipart, obligatorio)

**PATCH `/api/v1/documents/{id}/change-confidentiality`**
- Body: `confidentialityLevel` (enum, obligatorio: `PUBLIC`, `PRIVATE_ALL_AREAS`, `PRIVATE_ONLY_AREA`, `CONFIDENTIAL`)

### RAG

| Método | Ruta | Descripción | Auth | Roles |
|---|---|---|---|---|
| POST | `/api/v1/rag/documents/{id}/ingest?force=false` | Ingiere un documento a la base vectorial | Sí (Bearer JWT) | Empleados (con área) |
| POST | `/api/v1/rag/query` | Recupera fragmentos relevantes sin generar respuesta | Sí (Bearer JWT) | - |
| POST | `/api/v1/rag/ask` | Pregunta con respuesta generada por IA | Sí (Bearer JWT) | - |
| GET | `/api/v1/rag/documents/{id}/content` | Obtiene el contenido vía rag-service (reenvía la autorización a document-service) | Sí (Bearer JWT) | - |

**POST `/api/v1/rag/query` y POST `/api/v1/rag/ask`**
- Body: `query` (string), `topK` (integer, opcional)

**POST `/api/v1/rag/documents/{id}/ingest`**
- Query param: `force` (boolean, opcional, por defecto `false`)

## Autenticación

Esquema JWT Bearer. El login o register devuelve `accessToken` y `refreshToken`. Las rutas protegidas se llaman con el header `Authorization: Bearer <accessToken>`. Cuando el access token caduca, se renueva con `POST .../refresh` enviando `{"refreshToken": "..."}`. El registro de empleados y el cambio de jerarquía (`promote`/`unpromote`) exigen además los roles indicados en la tabla.
