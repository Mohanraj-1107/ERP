# ERP Core

A Spring Boot backend for managing accounts of small-scale businesses (ERP-style), built around an organization-centric model. The project is currently **under construction**: organization onboarding/authentication and the domain entity model are in place, while product, inventory, sales, purchase, customer and supplier workflows are still being implemented.

- Repository: https://github.com/Mohanraj-1107/ERP
- Author: Mohanraj Subramani
- Status: Work in progress (`0.0.1-SNAPSHOT`)

## Tech Stack

- Java 17
- Spring Boot 4.1.x (Spring Web MVC, Spring Data JPA, Bean Validation)
- Spring Security Crypto (BCrypt password hashing)
- MySQL (JPA/Hibernate, `ddl-auto=update` in dev)
- JWT via `jjwt` 0.11.5 (`HS256`, 1 hour expiry by default)
- Lombok
- Maven (wrapper included: `./mvnw`)

## Project Structure

```
core/
├── pom.xml
├── src/main/java/com/erp/core/
│   ├── CoreApplication.java          # Spring Boot entry point
│   ├── controller/                  # REST controllers
│   │   ├── LoginController.java     # POST /v1/login
│   │   └── OrgController.java       # /v1/org endpoints
│   ├── service/                     # Business logic
│   │   └── OrgService.java
│   ├── repository/                  # Spring Data JPA repositories
│   │   └── OrganizationRepo.java
│   ├── entity/                      # JPA entities + request/response DTOs
│   │   ├── common/                  # Address, JwtUtils, PasswordConfig, ErrorResponse
│   │   ├── organization/            # Organization, CreateOrgRequest, GetOrgResponse, LoginRequest/Response
│   │   ├── product/                 # Product, Unit (KG/LITER/PCS/GRAMS)
│   │   ├── inventory/               # Inventory (placeholder entity)
│   │   ├── customer/                # Customer
│   │   ├── supplier/                # Supplier
│   │   ├── purchase/                # Purchase (empty placeholder)
│   │   └── sale/                    # Sale (empty placeholder)
│   ├── exception/                   # Custom exceptions + GlobalExceptionHandler
│   └── filters/                     # JwtFilter (cookie-based JWT auth)
└── src/main/resources/
    ├── application.properties
    ├── application-dev.properties
    └── application-prod.properties
```

## Domain Model (implemented)

- **Organization** — top-level tenant/account. Fields: `orgId`, `orgName`, `orgEmail` (unique), `orgPassword` (BCrypt, write-only in JSON), `address` (list of `Address`), `mobileNo`, `createdOn`.
- **Address** — embeddable, reused by Organization/Customer/Supplier (`city`, `street`, `district`, `pincode`, `state`, `country`).
- **Customer** — belongs to an organization; name, email, mobile, GST number, addresses.
- **Supplier** — belongs to an organization; name, email, mobile, GST number, addresses.
- **Product** — belongs to an organization; code, name (unique), purchase/selling price, tax rate, minimum stock, unit (`KG`, `LITER`, `PCS`, `GRAMS`).
- **Inventory** — intended stock level per product/org (entity annotation missing — see Suggestions).
- **Purchase / Sale** — currently empty placeholder classes.

All major entities are organization-scoped via a `@ManyToOne` relation to `Organization` (serialized as `@JsonIgnore`).

## REST API

Base path is `/erp/api` (set via `server.servlet.context-path`). JWT is read from the `access_token` HttpOnly cookie.

### Authentication

Access tokens are short-lived JWTs (15 minutes) in the `access_token` cookie.
Refresh tokens are opaque random strings (7 days) in the `refresh_token` cookie, stored only as **SHA-256 hashes** in the `refresh_token` table, with rotation and reuse detection.

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `POST` | `/erp/api/v1/login` | Public | Login with organization email + password. Sets `access_token` and `refresh_token` cookies. |
| `POST` | `/erp/api/v1/refresh` | Public (cookie) | Rotates the refresh token and issues a new access token. |
| `POST` | `/erp/api/v1/logout` | Public (cookie) | Revokes the refresh token and clears cookies. |
| `POST` | `/erp/api/v1/logout-all` | JWT required | Revokes all refresh tokens for the org. |

Request body (`LoginRequest`):
```json
{
  "org_email": "org@example.com",
  "password": "secret123"
}
```

Response (`LoginResponse`):
```json
{
  "orgId": 1,
  "message": "Login successfull",
  "status": "success"
}
```

### Organization management

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `POST` | `/erp/api/v1/org` | Public | Create a new organization (validated). |
| `GET`  | `/erp/api/v1/org/{orgId}` | JWT required | Fetch organization details. |

Create organization request (`CreateOrgRequest`):
```json
{
  "orgName": "Acme Traders",
  "orgEmail": "acme@example.com",
  "orgPassword": "secret123",
  "address": [
    { "city": "Chennai", "street": "Main St", "district": "Chennai", "pincode": "600001", "state": "TN", "country": "India" }
  ],
  "mobileNo": "9876543210"
}
```

`mobileNo` must be a 10-digit Indian mobile number starting with 6–9. `orgName` must be 5–100 chars, password 8–64 chars.

## Configuration

`src/main/resources/application.properties`:
- `server.servlet.context-path=/erp/api`
- `app.jwt.secret` (env: `JWT_SECRET_KEY`, default bundled — **change in production**)
- `app.jwt.expiration` (env: `JWT_EXPIRATION`, default `3600000` ms)
- `cookie.accesstoken.expiry`, `cookie.refreshtoken.expiry`

Profiles:
- **dev** (`application-dev.properties`): port `8080` (currently `8080` after local edit), MySQL on `localhost:3306/erp` (auto-created), `ddl-auto=update`, SQL logging enabled.
- **prod** (`application-prod.properties`): currently empty — must be filled before deployment.

Active profile is set in `application.properties` via `spring.profiles.active=dev`.

## Running Locally

Prerequisites: Java 17, MySQL running on `localhost:3306`.

```bash
# from core/
./mvnw spring-boot:run
```

Or build a jar:
```bash
./mvnw clean package
java -jar target/core-0.0.1-SNAPSHOT.jar
```

Smoke test:
```bash
curl -X POST http://localhost:8080/erp/api/v1/org \
  -H "Content-Type: application/json" \
  -d '{"orgName":"Acme Traders","orgEmail":"acme@example.com","orgPassword":"secret123","address":[{"city":"Chennai","street":"Main St","district":"Chennai","pincode":"600001"}],"mobileNo":"9876543210"}'
```

## Known Gaps / Work Under Construction

- Only `Organization` has a full controller + service + repository. Product, Inventory, Customer, Supplier, Purchase and Sale have entities (mostly) but no repositories, services, or controllers.
- `Purchase` and `Sale` are empty placeholder classes.
- `Inventory` is missing the `@Entity` annotation.
- `Product` has no getters/setters, so it does not serialize cleanly as JSON and cannot be bound from a request.
- Access tokens expire after 15 minutes; clients should call `/v1/refresh` on `401` and retry.
- `JwtFilter` public-URL allowlist uses the full request URI including context path (`/erp/api/v1/...`) to match `request.getRequestURI()`.
- Cookie handling (including `Secure`, `SameSite`, `HttpOnly`) is done in `AuthController` via Spring `ResponseCookie`.
- Default JWT secret is committed in `application.properties`; rotate and require it via env in production.
- Production profile is empty.
- Tests are limited to the Spring Boot context-load test.

## Suggestions for Next Steps

1. Finish CRUD modules in priority order: Product → Inventory → Customer → Supplier → Purchase → Sale, each with repository + service + controller + DTOs + validation.
2. Make `Inventory` a proper `@Entity` with unique constraint on `(org, product)` and stock adjustment methods; auto-create inventory rows when a product is created.
3. Add Lombok `@Getter/@Setter` (or explicit accessors) to `Product` and any entity missing them; expose DTOs separate from entities to avoid leaking password/ids.
4. ✅ Refresh tokens implemented (opaque, SHA-256 hashed, rotated, reuse-detected) with `/v1/refresh`, `/v1/logout`, `/v1/logout-all`.
6. Fix the `JwtFilter` public-URL matching (compare against the path without context, or store the full URI) and protect all non-public routes.
7. Add request validation to `LoginController` (`@Valid`) and validate nested `Address` fields (`@Valid` on the `address` list, with `@NotBlank` on `city`, `street`, etc.).
8. Strengthen security: require `JWT_SECRET_KEY` in prod, set cookie `Secure`, `SameSite`, `Path=/`; consider Spring Security filter chain instead of a hand-rolled `Filter`.
9. Add exception mapping for JWT/auth errors in `GlobalExceptionHandler` and a generic 500 handler; fix the `errorMessgae` typo in `ErrorResponse`.
10. Add pagination/filtering to list endpoints once CRUD is done; add unit/integration tests with Testcontainers for MySQL.
11. Fill in `application-prod.properties` (datasource, port, logging, secret) and document environment variables.
12. Set project metadata in `pom.xml` (name, description) and add a `LICENSE`; bump cleanup of `@Component` from request/response DTOs.
