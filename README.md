# ERP Core

ERP Core is a Spring Boot backend for managing accounts and operations of small-scale businesses. It is built around an **organization-centric** model, where organizations act as tenants/accounts and business modules (products, customers, suppliers, sales, purchases) are scoped to an organization.

The project is currently **under active construction**. Organization onboarding/authentication and the Product module are functional; inventory, customer, supplier, sales, and purchase workflows are being implemented.

**Detailed technical documentation is available in [`docs/docs.md`](docs/docs.md).**

## Overview

ERP Core provides:
- Organization registration and login
- JWT-based authentication with rotating opaque refresh tokens
- Product management (org-scoped, paginated)
- A foundation for inventory, customer, supplier, sales, and purchase modules

## Tech Stack

- Java 17
- Spring Boot (Spring Web MVC, Spring Data JPA, Bean Validation)
- MySQL
- JWT access tokens (`jjwt`)
- BCrypt password hashing
- Maven

## Quick Start

Prerequisites: Java 17 and MySQL running on `localhost:3306`.

```bash
cd core
./mvnw spring-boot:run
```

The application starts at:

```text
http://localhost:8080/erp/api
```

Run tests:

```bash
./mvnw test
```

Build a jar:

```bash
./mvnw clean package
```

## Project Structure

```
core/
├── pom.xml
├── src/main/java/com/erp/core/
│   ├── common/        # JWT, password config, request context
│   ├── controller/    # REST controllers
│   ├── service/       # Business logic
│   ├── repository/    # Data access
│   ├── entity/        # JPA entities and request/response DTOs
│   ├── exception/     # Exception handling
│   └── filters/       # Authentication filter
└── src/main/resources/
    ├── application.properties
    ├── application-dev.properties
    └── application-prod.properties
```

## Essential Configuration

Profiles:
- `dev` — local development with MySQL on `localhost:3306`
- `prod` — production settings (must be completed before deployment)

Important environment variables:
- `JWT_SECRET_KEY`
- `JWT_EXPIRATION`
- `REFRESH_TOKEN_EXPIRY`
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `COOKIE_SECURE`

See [`docs/docs.md`](docs/docs.md) for the full configuration list and API details.

## Documentation

- [`docs/docs.md`](docs/docs.md) — architecture, API endpoints, domain model, security, configuration, known gaps, and next steps

## Repository

- GitHub: https://github.com/Mohanraj-1107/ERP
- Author: Mohanraj Subramani
- Year: 2026
- Status: Work in progress (`0.0.1-SNAPSHOT`)
