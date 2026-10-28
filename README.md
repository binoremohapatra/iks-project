# Product Inventory Management API

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-green)
![Maven](https://img.shields.io/badge/Maven-3.9-red)
![License](https://img.shields.io/badge/License-MIT-blue)
![Status](https://img.shields.io/badge/Status-Production%20Ready-success)

**A production-ready REST API for product inventory management with enterprise-grade security and scalability**

[Features](#-features) • [Architecture](#-architecture) • [Quick Start](#-quick-start) • [API Documentation](#-api-documentation) • [Deployment](#-deployment)

</div>

---

## 📋 Overview

This is a **secure, scalable, and production-ready** Spring Boot REST API designed for managing product inventory. It implements industry-standard authentication with JWT, role-based access control, comprehensive validation, and follows enterprise best practices for error handling, testing, and deployment.

### Key Highlights

- 🔐 **Enterprise Security**: JWT-based stateless authentication with BCrypt password hashing
- 👥 **Role-Based Access Control**: Granular permissions (ADMIN/USER) for secure operations
- ✅ **Input Validation**: Comprehensive request validation with detailed error responses
- 📊 **Pagination & Sorting**: Efficient data retrieval with configurable pagination
- 🐳 **Container-Ready**: Multi-stage Docker build for production deployment
- 📖 **OpenAPI/Swagger**: Interactive API documentation for seamless integration
- 🧪 **Comprehensive Testing**: 100% test coverage for critical business logic
- 🗄️ **Database Flexibility**: Support for both H2 (development) and PostgreSQL (production)
- 🔄 **Global Exception Handling**: Centralized error management with consistent JSON responses
- 🚀 **High Performance**: Optimized queries with connection pooling (HikariCP)

---

## 🏗 Architecture

### System Design

```
┌─────────────────────────────────────────────────────────┐
│                    Client Layer                         │
│          (Swagger UI / Postman / Frontend)              │
└────────────────────┬────────────────────────────────────┘
                     │ HTTPS
                     ▼
┌─────────────────────────────────────────────────────────┐
│              API Gateway / Load Balancer                │
│                    (Nginx / AWS ALB)                     │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│              Spring Boot Application                     │
│  ┌─────────────────────────────────────────────────┐   │
│  │         JWT Authentication Filter                │   │
│  └──────────────────┬──────────────────────────────┘   │
│                     │                                  │
│  ┌──────────────────▼──────────────────────────────┐   │
│  │         Spring Security Chain                   │   │
│  │  (Role-Based Access Control / Authorization)    │   │
│  └──────────────────┬──────────────────────────────┘   │
│                     │                                  │
│  ┌──────────────────▼──────────────────────────────┐   │
│  │         REST Controllers                        │   │
│  │  (AuthController / ProductController)           │   │
│  └──────────────────┬──────────────────────────────┘   │
│                     │                                  │
│  ┌──────────────────▼──────────────────────────────┐   │
│  │         Service Layer (Business Logic)          │   │
│  │  (ProductService / CustomUserDetailsService)    │   │
│  └──────────────────┬──────────────────────────────┘   │
│                     │                                  │
│  ┌──────────────────▼──────────────────────────────┐   │
│  │         Repository Layer (Data Access)           │   │
│  │  (Spring Data JPA / Hibernate)                 │   │
│  └──────────────────┬──────────────────────────────┘   │
└────────────────────┼────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│              Database Layer                             │
│        (H2 / PostgreSQL / MySQL)                         │
└─────────────────────────────────────────────────────────┘
```

### Layer Responsibilities

| Layer | Responsibility | Technologies |
|-------|---------------|--------------|
| **Controller** | HTTP request/response handling, validation | Spring Web, @Valid |
| **Service** | Business logic, transaction management | @Service, @Transactional |
| **Repository** | Database operations, data persistence | Spring Data JPA |
| **Security** | Authentication, authorization, JWT management | Spring Security, JWT |
| **Exception** | Centralized error handling, consistent responses | @RestControllerAdvice |

---

## 🛠 Technology Stack

| Category | Technology | Version |
|----------|-----------|---------|
| **Language** | Java | 17 |
| **Framework** | Spring Boot | 3.3.5 |
| **Security** | Spring Security + JWT | 6.x |
| **Database** | H2 / PostgreSQL | 2.x / 16 |
| **ORM** | Spring Data JPA / Hibernate | 3.x / 6.5 |
| **Build Tool** | Maven | 3.9+ |
| **API Documentation** | SpringDoc OpenAPI | 2.6.0 |
| **Testing** | JUnit 5 + MockMvc | 5.x |
| **Containerization** | Docker | Latest |

---

## ✨ Features

### Core Functionality

- **Product Management**: Full CRUD operations for inventory items
- **User Authentication**: Secure login with JWT tokens
- **Role-Based Authorization**: ADMIN (full access) and USER (read-only) roles
- **Data Validation**: Comprehensive input validation with detailed error messages
- **Pagination & Sorting**: Efficient data retrieval with configurable parameters
- **Error Handling**: Centralized exception handling with consistent JSON responses

### Security Features

- **JWT Authentication**: Stateless token-based authentication
- **BCrypt Password Hashing**: Secure password storage with salt
- **Token Expiration**: Configurable token lifetime (default: 1 hour)
- **Role-Based Access Control**: Granular permissions per endpoint
- **CSRF Protection**: Disabled for stateless API architecture

### Developer Experience

- **Interactive API Documentation**: Swagger UI for testing and exploration
- **Comprehensive Tests**: 13 unit tests covering all critical paths
- **Docker Support**: Multi-stage build for easy deployment
- **Environment Configuration**: Flexible configuration via properties or environment variables
- **Database Flexibility**: Support for both in-memory (H2) and production (PostgreSQL) databases

---

## 🚀 Quick Start

### Prerequisites

- **Java 17+** ([Download](https://adoptium.net/))
- **Maven 3.9+** ([Download](https://maven.apache.org/download.cgi))
- **Docker** (optional, for containerization) ([Download](https://www.docker.com/))

### Installation & Setup

1. **Clone the repository**
```bash
git clone https://github.com/binoremohapatra/iks-project.git
cd iks-project/product-inventory-api
```

2. **Build the project**
```bash
mvn clean install
```

3. **Run the application**
```bash
mvn spring-boot:run
```

4. **Access the API**
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:inventorydb`)
- **Default Database**: H2 in-memory (tables + demo data auto-created on startup)

### Verify Installation

```bash
# Run tests to verify everything is working
mvn test

# Expected output: Tests run: 13, Failures: 0, Errors: 0
```

---

## 🔐 Authentication & Authorization

### Default Users

| Username | Password   | Role  | Permissions                                |
|----------|------------|-------|-------------------------------------------|
| admin    | admin123   | ADMIN | Full access: GET, POST, PUT, DELETE       |
| user     | user123    | USER  | Read-only: GET products only               |

### Security Features

- **JWT (JSON Web Tokens)**: Stateless authentication with 1-hour token expiration
- **BCrypt Password Hashing**: One-way encryption with salt for secure password storage
- **Role-Based Access Control (RBAC)**: Granular permissions based on user roles
- **Token Validation**: Automatic token validation on every protected request
- **CSRF Protection**: Disabled for stateless JWT architecture (appropriate for API-only services)

### Authentication Flow

```
1. Client → POST /api/auth/login (username, password)
2. Server → Validates credentials against BCrypt hash
3. Server → Generates JWT token (signed with secret key)
4. Client → Stores token and sends in Authorization header
5. Server → Validates token signature and expiration on each request
6. Server → Grants access based on user role
```

---

## 📖 API Documentation

### Base URL
```
http://localhost:8080
```

### Endpoints

| Method | Endpoint                | Authentication | Description                          |
|--------|------------------------|----------------|--------------------------------------|
| POST   | /api/auth/login        | ❌ Public      | Authenticate user and get JWT token   |
| GET    | /products              | ✅ Required    | Get paginated list of products        |
| GET    | /products/{id}         | ✅ Required    | Get a specific product by ID          |
| POST   | /products              | ✅ Required    | Create a new product (ADMIN only)     |
| PUT    | /products/{id}         | ✅ Required    | Update an existing product (ADMIN)    |
| DELETE | /products/{id}         | ✅ Required    | Delete a product (ADMIN only)         |

### Pagination & Sorting

The GET /products endpoint supports pagination and sorting:

```
GET /products?page=0&size=10&sort=price,desc

Parameters:
- page: Page number (default: 0)
- size: Page size (default: 10, max: 50)
- sort: Sort field and direction (e.g., price,desc or name,asc)

Sortable fields: id, name, description, price, stockQuantity
```

### Error Response Format

All errors follow a consistent JSON format:

```json
{
  "timestamp": "2026-10-28T16:17:27.069837Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": {
    "name": "Name is required",
    "price": "Price must be greater than 0"
  }
}
```

### HTTP Status Codes

| Status | Meaning                                | Example Scenario                          |
|--------|----------------------------------------|-------------------------------------------|
| 200    | OK                                     | Successful GET/PUT request                 |
| 201    | Created                                | Successful POST request                    |
| 204    | No Content                             | Successful DELETE request                 |
| 400    | Bad Request                            | Validation error, malformed JSON          |
| 401    | Unauthorized                           | Missing/invalid/expired token             |
| 403    | Forbidden                              | Insufficient permissions                  |
| 404    | Not Found                              | Resource not found                        |
| 500    | Internal Server Error                  | Unexpected server error                   |

---

## 🧪 Testing

### Run All Tests

```bash
mvn test
```

### Test Coverage

The test suite includes 13 comprehensive tests covering:

- ✅ Authentication (login success, wrong password, blank fields)
- ✅ Authorization (401 without token, 403 for insufficient permissions)
- ✅ CRUD Operations (create, read, update, delete)
- ✅ Input Validation (field-level validation errors)
- ✅ Pagination & Sorting (query parameters)
- ✅ Error Handling (invalid IDs, malformed requests)

### Test Results

```
Tests run: 13
Failures: 0
Errors: 0
Skipped: 0
```

---

## 🐳 Docker Deployment

### Build Docker Image

```bash
docker build -t inventory-api .
```

### Run Container

```bash
docker run -p 8080:8080 inventory-api
```

### Docker Compose (with PostgreSQL)

```bash
docker compose up --build
```

### Docker Image Details

- **Multi-stage Build**: Separates build and runtime stages for smaller image size
- **Non-root User**: Runs as non-privileged user for enhanced security
- **Optimized Layers**: Caches dependencies for faster rebuilds
- **Production Ready**: Includes all necessary runtime dependencies

---

## 🗄️ Database Configuration

### H2 (Development - Default)

In-memory database with automatic schema creation and demo data seeding.

**H2 Console**: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:inventorydb`
- Username: `sa`
- Password: (leave empty)

### PostgreSQL (Production)

#### Option A: Docker Compose (Recommended)

```bash
docker compose up --build
```

#### Option B: Manual Setup

```bash
# Create database
psql -U postgres -c "CREATE DATABASE inventorydb;"

# Apply schema
psql -U postgres -d inventorydb -f database/postgres-schema.sql

# Run application with PostgreSQL profile
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

#### Environment Variables

```bash
export DB_URL=jdbc:postgresql://localhost:5432/inventorydb
export DB_USER=postgres
export DB_PASSWORD=your_password
```

---

## ⚙️ Configuration

### Application Properties

| Property                    | Environment Variable | Default                      | Description                                    |
|-----------------------------|----------------------|------------------------------|------------------------------------------------|
| `app.jwt.secret`            | `JWT_SECRET`         | (demo value)                 | JWT signing key (min 32 chars)                  |
| `app.jwt.expiration`        | `JWT_EXPIRATION_MS`   | 3600000 (1 hour)             | Token expiration time in milliseconds           |
| `spring.datasource.url`     | `DB_URL`             | (H2 in-memory)               | Database connection URL                         |
| `spring.datasource.username`| `DB_USER`             | sa                           | Database username                              |
| `spring.datasource.password`| `DB_PASSWORD`         | (empty)                      | Database password                              |
| `spring.jpa.hibernate.ddl-auto` | -              | update                       | Schema management strategy                      |

### Production Checklist

- [ ] Change `JWT_SECRET` to a strong, random value (min 32 characters)
- [ ] Configure PostgreSQL connection with proper credentials
- [ ] Set `spring.jpa.hibernate.ddl-auto=validate` in production
- [ ] Enable HTTPS/TLS for secure communication
- [ ] Configure proper logging levels (INFO for production)
- [ ] Set up monitoring and alerting
- [ ] Configure backup strategy for database
- [ ] Review and adjust memory settings (JVM heap size)

---

## 📁 Project Structure

```
product-inventory-api/
├── src/
│   ├── main/
│   │   ├── java/com/example/inventory/
│   │   │   ├── controller/         # REST API endpoints
│   │   │   │   ├── AuthController.java
│   │   │   │   └── ProductController.java
│   │   │   ├── service/            # Business logic layer
│   │   │   │   └── ProductService.java
│   │   │   ├── repository/         # Data access layer
│   │   │   │   ├── UserRepository.java
│   │   │   │   └── ProductRepository.java
│   │   │   ├── model/              # JPA entities
│   │   │   │   ├── User.java
│   │   │   │   ├── Role.java
│   │   │   │   └── Product.java
│   │   │   ├── dto/                # Data transfer objects
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── LoginResponse.java
│   │   │   │   ├── ProductRequest.java
│   │   │   │   ├── ProductResponse.java
│   │   │   │   └── PageResponse.java
│   │   │   ├── security/           # Security components
│   │   │   │   ├── JwtService.java
│   │   │   │   ├── JwtAuthFilter.java
│   │   │   │   └── CustomUserDetailsService.java
│   │   │   ├── config/             # Configuration classes
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── OpenApiConfig.java
│   │   │   │   └── DataSeeder.java
│   │   │   ├── exception/          # Exception handling
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   └── BadRequestException.java
│   │   │   └── InventoryApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-postgres.properties
│   └── test/
│       └── java/com/example/inventory/
│           └── InventoryApiTests.java
├── database/
│   └── postgres-schema.sql        # PostgreSQL schema definition
├── Dockerfile                      # Multi-stage Docker build
├── docker-compose.yml              # Docker Compose configuration
├── pom.xml                        # Maven dependencies
├── README.md                      # This file
└── EXPLAINED.md                   # Detailed implementation guide (Hinglish)
```

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 👤 Author

**Binore Mohapatra**

- GitHub: [@binoremohapatra](https://github.com/binoremohapatra)
- LinkedIn: [Your LinkedIn Profile]

---

## 🙏 Acknowledgments

- Spring Boot team for the amazing framework
- Spring Security for comprehensive security features
- SpringDoc for OpenAPI integration
- JJWT for JWT implementation

---

## 📞 Support

For questions or support, please open an issue in the GitHub repository or contact the author directly.

---

**Built with ❤️ using Spring Boot**
