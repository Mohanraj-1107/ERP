# ERP Core — Internal Documentation

This document contains implementation details for contributors. For a project overview, see [`README.md`](README.md).

## Architecture Summary

- Spring Boot web application with `server.servlet.context-path=/erp/api`
- REST controllers under `/v1`
- JWT access tokens in HttpOnly cookies
- Opaque refresh tokens (SHA-256 hashed) in MySQL, rotated on refresh
- Multi-tenant model: almost every business entity belongs to an `Organization`

## Project Structure

```
core/
├── pom.xml
├── src/main/java/com/erp/core/
│   ├── CoreApplication.java
│   ├── common/                 # JwtUtils, PasswordConfig, OrgContext
│   ├── controller/             # REST controllers
│   ├── service/                # Business logic
│   ├── repository/             # Spring Data JPA repositories
│   ├── entity/                 # JPA entities and DTOs
│   ├── exception/              # Exceptions and global handler
│   └── filters/                # JwtFilter
└── src/main/resources/
    ├── application.properties
    ├── application-dev.properties
    └── application-prod.properties
```

## Implemented Modules

### Authentication and session management
Endpoints:
- `POST /erp/api/v1/login` — login, sets `access_token` and `refresh_token`
- `POST /erp/api/v1/refresh` — rotates refresh token and issues new access token
- `POST /erp/api/v1/logout` — revokes refresh token and clears cookies
- `POST /erp/api/v1/logout-all` — revokes all refresh tokens for the org

Auth details:
- Access token: JWT, HS256, default 15 minutes, cookie `access_token`
- Refresh token: opaque, 7 days, cookie `refresh_token`, stored as SHA-256 hash in `refresh_token`
- Refresh tokens rotate on every refresh and support reuse detection
- `JwtFilter` sets `OrgContext` for protected routes and clears it in `finally`

### Customer management
Endpoints:
- `POST /erp/api/v1/customers` — create customer
- `GET /erp/api/v1/customers/{customerId}` — get customer
- `GET /erp/api/v1/customers` — paginated list (`page`, `size`)
- `PATCH /erp/api/v1/customers/{customerId}` — partial update
- `DELETE /erp/api/v1/customers/{customerId}` — delete customer

Customer rules:
- Customers are org-scoped
- `customerEmail` is unique per organization

### Supplier management
Endpoints:
- `POST /erp/api/v1/suppliers` — create supplier
- `GET /erp/api/v1/suppliers/{supplierId}` — get supplier
- `GET /erp/api/v1/suppliers` — paginated list (`page`, `size`)
- `PATCH /erp/api/v1/suppliers/{supplierId}` — partial update
- `DELETE /erp/api/v1/suppliers/{supplierId}` — delete supplier

Supplier rules:
- Suppliers are org-scoped
- `supplierEmail` is unique per organization

### Purchase management
Endpoints:
- `POST /erp/api/v1/purchases` — create purchase order (body: `{ "supplierId": n, "items": [...] }`)
- `GET /erp/api/v1/purchases/{purchaseId}` — get purchase details
- `GET /erp/api/v1/purchases` — paginated list (`page`, `size`)
- `PATCH /erp/api/v1/purchases/{purchaseId}` — update draft purchase
- `POST /erp/api/v1/purchases/{purchaseId}/complete` — mark DRAFT → COMPLETED, increase inventory stock
- `POST /erp/api/v1/purchases/{purchaseId}/cancel` — cancel purchase (reverses inventory if COMPLETED)
- `DELETE /erp/api/v1/purchases/{purchaseId}` — delete draft purchase

Purchase rules:
- Purchases are org-scoped
- Only DRAFT purchases can be updated or deleted
- Only DRAFT purchases can be completed (increases inventory per item)
- COMPLETED purchases can be cancelled (reverses inventory per item)
- CANCELLED purchases cannot be modified

### Inventory management
Endpoints:
- `GET /erp/api/v1/inventory` — paginated list of inventory for the org (`page`, `size`)
- `GET /erp/api/v1/inventory/{productId}` — get the inventory record for a product
- `POST /erp/api/v1/inventory/{productId}/add` — add stock (body: `{ "quantity": n }`)
- `POST /erp/api/v1/inventory/{productId}/remove` — remove stock (body: `{ "quantity": n }`)
- `GET /erp/api/v1/inventory/low-stock` — list inventory at or below each product's minimum stock

Inventory rules:
- An inventory record is created automatically when a product is created
- Stock adjustments require a positive quantity
- Removing more stock than available is rejected
- Inventory is org-scoped; `quantity` cannot drop below zero

### Organization management
Endpoints:
- `POST /erp/api/v1/org` — create organization
- `GET /erp/api/v1/org/{orgId}` — get organization details

### Product management
Endpoints:
- `POST /erp/api/v1/products` — create product
- `GET /erp/api/v1/products/{productId}` — get product
- `GET /erp/api/v1/products` — paginated list
- `PATCH /erp/api/v1/products/{productId}` — update product
- `DELETE /erp/api/v1/products/{productId}` — delete product

Product rules:
- Products are org-scoped
- `productCode` and `productName` are unique per organization
- Prices must be greater than zero
- Tax rate range: 0–100
- Pageable list endpoint supports `page` and `size`

## Domain Model

- `Organization` — top-level tenant/account
- `Address` — embeddable address reused by org/customer/supplier
- `Customer` — organization-scoped customer
- `Supplier` — organization-scoped supplier
- `Product` — organization-scoped product with unit and pricing
- `Inventory` — organization-scoped stock per product with low-stock support
- `Purchase` / `Sale` — placeholder modules

## Security and Multitenancy

- Organization identity is derived from the JWT subject (`orgId`).
- `JwtFilter` validates token before the controller runs.
- `OrgContext` stores the current org identity for the request thread.
- Business queries should always be filtered by `orgId`.
- Passwords are BCrypt-hashed.
- JWT secret should be provided by environment in production.

## Configuration

Main properties:
- `server.servlet.context-path=/erp/api`
- `app.jwt.secret` (`JWT_SECRET_KEY`)
- `app.jwt.expiration` (`JWT_EXPIRATION`, default 900000)
- `app.refresh-token.bytes` (`REFRESH_TOKEN_BYTES`, default 64)
- `app.refresh-token.expiry` (`REFRESH_TOKEN_EXPIRY`, default 604800000)
- `cookie.accesstoken.expiry=900000`
- `cookie.refreshtoken.expiry=604800000`
- `cookie.secure` (`COOKIE_SECURE`, default false)

Profiles:
- `dev`: MySQL `localhost:3306/erp`, `ddl-auto=update`, SQL logging enabled
- `prod`: production datasource and secure cookie settings; verify before deployment

## Known Gaps / Work in Progress

- Only Organization and Product have full controller/service/repository flows.
- `Inventory` lacks complete entity/service/controller implementation.
- `Supplier` lacks repositories/services/controllers. (`Supplier` module is complete; `Purchase` remains.)
- `Purchase` and `Sale` are empty placeholders.
- `Product` hard delete is currently used; soft delete may be better later.
- `OrgService` still throws checked `InvalidPathVariableException` in some places.
- Integration tests are minimal (context load only).

## Suggested Next Steps

1. Complete Inventory module with stock adjustments and low-stock support.
2. Implement Customer and Supplier CRUD.
3. Implement Purchase and Sale workflows with inventory updates.
4. Add pagination/filtering to all list endpoints.
5. Add integration tests (Testcontainers) and stronger validation.
6. Consider Flyway/Liquibase for schema migrations.
7. Add service-level authorization helper to centralize org scoping.

## Running and Testing

Prerequisites: Java 17, MySQL on `localhost:3306`.

```bash
./mvnw spring-boot:run
./mvnw test
./mvnw clean package
```

Default dev URL: `http://localhost:8080/erp/api`

## Notes

- Production profile must be completed before deployment.
- Keep `cookie.secure=true` in production.
- Rotate `JWT_SECRET_KEY` and do not rely on the default development secret.
