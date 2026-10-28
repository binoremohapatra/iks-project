# Product Inventory Management API

Spring Boot 3.3 REST API with JWT authentication, role-based access (ADMIN / USER),
validation, global exception handling, pagination/sorting, Swagger UI and Docker support.

**Tech:** Java 17, Spring Boot 3.3.5, Spring Web, Spring Security, Spring Data JPA, H2 (default) / PostgreSQL (optional), jjwt, springdoc-openapi, JUnit 5 + MockMvc.

> This is a backend-only service. Swagger UI is the UI for trying the API. No separate frontend is needed.

## Quick start (no database setup needed)

Prerequisites: **Java 17+** and **Maven 3.9+**

```bash
mvn spring-boot:run
```

- Swagger UI: http://localhost:8080/swagger-ui.html
- Default DB: H2 in-memory (tables + demo data are created automatically on start)

## Demo users

| Username | Password   | Role  | Can do                         |
|----------|------------|-------|--------------------------------|
| admin    | admin123   | ADMIN | GET, POST, PUT, DELETE products |
| user     | user123    | USER  | GET products only              |

## Try it in Swagger UI

1. Open http://localhost:8080/swagger-ui.html
2. `POST /api/auth/login` with `{"username":"admin","password":"admin123"}` and copy the `token`.
3. Click **Authorize** (top right), paste the token (no "Bearer " prefix needed), click Authorize.
4. Call any `/products` endpoint.

## Or with curl

```bash
TOKEN=$(curl -s -X POST localhost:8080/api/auth/login -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | sed -E 's/.*"token":"([^"]+)".*/\1/')

curl -H "Authorization: Bearer $TOKEN" "localhost:8080/products?page=0&size=5&sort=price,desc"

curl -X POST localhost:8080/products -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"name":"Monitor","description":"24 inch","price":9999.99,"stockQuantity":15}'
```

## Endpoints

| Method | URL                | Role        | Description                          |
|--------|--------------------|-------------|--------------------------------------|
| POST   | /api/auth/login    | public      | Returns JWT token                    |
| GET    | /products          | USER, ADMIN | Paginated list (`page`, `size`, `sort`) |
| GET    | /products/{id}     | USER, ADMIN | Get one product                      |
| POST   | /products          | ADMIN       | Create product (201)                 |
| PUT    | /products/{id}     | ADMIN       | Update product                       |
| DELETE | /products/{id}     | ADMIN       | Delete product (204)                 |

Sortable fields: `id, name, description, price, stockQuantity`. Max page size: 50.

## Error format (same everywhere)

```json
{
  "timestamp": "2026-10-26T10:15:30Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": { "name": "Name is required", "price": "Price must be greater than 0" }
}
```

| Status | When |
|--------|------|
| 400 | validation error, malformed JSON, invalid id/sort field |
| 401 | wrong login, missing/invalid/expired token |
| 403 | logged in but role not allowed |
| 404 | product (or URL) not found |
| 500 | unexpected error (details only in server logs) |

## Run tests

```bash
mvn test
```

Covers login (success / wrong password / blank fields), 401 & 403 rules, product create/get/update/delete,
validation errors, pagination and invalid sort.

## Docker

```bash
docker build -t inventory-api .
docker run -p 8080:8080 inventory-api
```

The Dockerfile is multi-stage (Maven build stage, then a small JRE-only runtime stage, non-root user).

## PostgreSQL (optional)

Schema file: `database/postgres-schema.sql`

**Option A - Docker Compose (easiest):**
```bash
docker compose up --build
```

**Option B - local PostgreSQL:**
```bash
psql -U postgres -c "CREATE DATABASE inventorydb;"
psql -U postgres -d inventorydb -f database/postgres-schema.sql
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```
Override connection with env vars `DB_URL`, `DB_USER`, `DB_PASSWORD`.
Demo users/products are inserted automatically on first start.

## Configuration

| Property / env var | Default | Meaning |
|---|---|---|
| `JWT_SECRET` | demo value | HMAC key, min 32 chars. **Change in production.** |
| `JWT_EXPIRATION_MS` | 3600000 | Token lifetime (1 hour) |

## Project structure

```
src/main/java/com/example/inventory/
  controller/  AuthController, ProductController      (HTTP layer)
  service/     ProductService                          (business logic)
  repository/  UserRepository, ProductRepository      (database access)
  model/       User, Role, Product                     (JPA entities)
  dto/         request/response objects                (API contract)
  security/    JwtService, JwtAuthFilter, CustomUserDetailsService
  config/      SecurityConfig, OpenApiConfig, DataSeeder
  exception/   GlobalExceptionHandler + custom exceptions
```

See `EXPLAINED.md` for a beginner-friendly walkthrough of how everything works.
