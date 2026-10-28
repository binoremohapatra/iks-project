-- PostgreSQL schema for Product Inventory API
-- Optional: the app can also create these tables itself (spring.jpa.hibernate.ddl-auto=update).
-- Usage:  psql -U postgres -c "CREATE DATABASE inventorydb;"
--         psql -U postgres -d inventorydb -f database/postgres-schema.sql
-- Demo users (admin/admin123, user/user123) and sample products are inserted automatically
-- by the app on first start (DataSeeder), so no INSERTs are needed here.

CREATE TABLE IF NOT EXISTS users (
    id        BIGSERIAL PRIMARY KEY,
    username  VARCHAR(255) NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL,              -- BCrypt hash
    role      VARCHAR(20)  NOT NULL CHECK (role IN ('ADMIN', 'USER'))
);

CREATE TABLE IF NOT EXISTS products (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100)   NOT NULL,
    description     VARCHAR(500),
    price           NUMERIC(12, 2) NOT NULL CHECK (price > 0),
    stock_quantity  INTEGER        NOT NULL CHECK (stock_quantity >= 0)
);
