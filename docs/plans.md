# ERP Core — Implementation Plan & Status

This file tracks the project roadmap, current module status, next steps, and product scope so context is preserved across sessions.

## 1. Project Overview

**ERP Core** is a Spring Boot backend for small-scale businesses to manage accounts and operations.

- **Model:** Organization-centric / multi-tenant. Every business entity belongs to an `Organization`.
- **Auth:** JWT access tokens (HttpOnly cookies) + opaque rotating refresh tokens (SHA-256 hashed in MySQL).
- **Base URL:** `http://localhost:8080/erp/api`
- **Tech stack:** Java 17, Spring Boot (Web MVC, Data JPA, Bean Validation), MySQL, jjwt, BCrypt, Maven.

## 2. Product Scope

**Target users:** Small-scale businesses that need a centralized backend to manage inventory, customers, suppliers, purchases, and sales — with data isolated per organization.

**Core product capabilities (planned/final scope):**

| Capability | Scope |
|------------|-------|
| Multi-tenancy | Every entity is org-scoped; users of Org A cannot see Org B's data |
| Authentication & Session | JWT access + rotating refresh tokens; login/logout/logout-all |
| Organization Management | Create org, get org details; `createdOn`/`modifiedOn` timestamps |
| Product Management | CRUD, paginated list, org-scoped, unique productCode/productName per org (composite constraints), price/tax validation, unit of measure, minimum stock |
| Inventory Management | Auto-create inventory on product creation; add/remove stock with validation (no negative stock); get inventory by product; paginated list; low-stock report (quantity ≤ minimumStock) |
| Customer Management | CRUD, paginated list, org-scoped, unique email per org, multiple addresses per customer |
| Supplier Management | (Planned) CRUD mirroring Customer; org-scoped |
| Purchase Management | (Planned) Create purchase orders; on receipt, increase inventory stock |
| Sale Management | (Planned) Create sales; on confirmation, decrease inventory stock with no-negative-stock validation |

**Out of scope (for now):** Multi-user roles within an org, financial accounting/ledger, barcode scanning, POS hardware integration, email/SMS notifications, mobile app.

## 3. Module Status

| Module | Status | Details |
|--------|--------|---------|
| Authentication | ✅ Complete | Login, refresh, logout, logout-all; JWT + refresh-token rotation |
| Organization | ✅ Complete | Create org, get org details |
| Product | ✅ Complete | CRUD, paginated list, org-scoped, unique code/name per org, price/tax validation |
| **Inventory** | ✅ Complete | Entity, repo, service, controller; auto-create on product; add/remove stock; low-stock; paginated list; org-scoped |
| **Customer** | ✅ Complete | CRUD, paginated list, duplicate-email check per org, addresses |
| **Supplier** | ✅ Complete | CRUD, paginated list, duplicate-email check per org, addresses |
| Purchase | ✅ Complete | Entity, repo, service, controller; DRAFT/COMPLETED/CANCELLED lifecycle; inventory integration on complete/cancel |
| Sale | ⬜ Entities done | `Sale`, `SalesItems`, `SalesStatus` entities complete; repo/service/controller pending |

## 4. Completed Work (this session)

### Inventory module (end-to-end)
- `Inventory` entity: `@Entity`, Lombok `@Getter`/`@Setter`, unique constraint `uk_inventory_org_product` on `(orgId, productId)`
- `InventoryRepo`: find by org+product, find low-stock (quantity ≤ product.minimumStock), find all by org (paginated), delete by product
- `InventoryService`: `getInventory`, `addStock`, `removeStock` (rejects negative stock), `listLowStock`, `listInventory` (paginated), auto-create at quantity 0
- `InventoryController`:
  - `GET /v1/inventory` — paginated list (org-scoped)
  - `GET /v1/inventory/{productId}`
  - `POST /v1/inventory/{productId}/add`
  - `POST /v1/inventory/{productId}/remove`
  - `GET /v1/inventory/low-stock`
- `ProductService` now auto-creates an inventory record when a product is created, and deletes it on product delete
- DTOs: `InventoryResponse`, `AdjustStockRequest` (`@Positive` quantity)
- Validation: quantity > 0; remove cannot exceed available stock; org scoping via `OrgContext`

### Customer module (stretch goal)
- `Customer` entity: `@Getter`/`@Setter`, unique constraint on `(orgId, customerEmail)`
- `CustomerRepo`: org-scoped find/list/exists-by-email
- `CustomerService` + `CustomerController`: full CRUD
  - `POST /v1/customers`
  - `GET /v1/customers/{customerId}`
  - `GET /v1/customers` (paginated)
  - `PATCH /v1/customers/{customerId}`
  - `DELETE /v1/customers/{customerId}`
- DTOs: `CreateCustomerRequest`, `UpdateCustomerRequest`, `CustomerResponse`, `AddressRequest`, `AddressResponse`
- Validation: duplicate email rejected per org

### Docs
- `docs/docs.md` updated with Customer, Inventory, and Supplier endpoint sections; known-gaps note adjusted

### Supplier module (completed)
- `Supplier` entity: `@Getter`/`@Setter`, unique constraint `uk_supplier_org_email` on `(orgId, supplierEmail)`
- `SupplierRepo`: find by org+supplierId, find all by org (paginated), exists-by-email, list by org
- `SupplierService` + `SupplierController`: full CRUD
  - `POST /v1/suppliers`
  - `GET /v1/suppliers/{supplierId}`
  - `GET /v1/suppliers` (paginated)
  - `PATCH /v1/suppliers/{supplierId}`
  - `DELETE /v1/suppliers/{supplierId}`
- DTOs: `CreateSupplierRequest`, `UpdateSupplierRequest`, `SupplierResponse`, `AddressRequest`, `AddressResponse`
- Validation: duplicate email rejected per org

### Entity fixes (completed)
- Product: replaced global `unique=true` on `productCode`/`productName` with composite unique constraints `uk_product_org_code` (`orgId, productCode`) and `uk_product_org_name` (`orgId, productName`) — two orgs can share the same product code
- Organization: added `modifiedOn` field with `@UpdateTimestamp` for consistency with other entities

### Purchase & Sale entities (completed)
- `Purchase` / `PurchaseItems` / `PurchaseStatus` — entities complete, compile-verified
- `Sale` / `SalesItems` / `SalesStatus` — entities complete, compile-verified
- Both parent entities: org-scoped, `@OneToMany` items with cascade/orphan-removal, `@ManyToOne` to Organization/Supplier/Customer, `purchasedDate` (`nullable=false`), `status` (default DRAFT), `createdOn`/`modifiedOn` timestamps

## 5. Verification (completed)
- `./mvnw compile` — BUILD SUCCESS
- `./mvnw test` — BUILD SUCCESS (context load test passes)
- Manual smoke test against running app + MySQL:
  - Inventory: auto-create → add → remove → low-stock trigger → remove-over-available rejected (400) → qty ≤ 0 rejected (400) → cross-org access → 404 → no auth → 401
  - Customer: create → get → paginated list → partial update → duplicate email → 400 → delete → 204 → 404
  - Supplier: create → get → paginated list → partial update → duplicate email → 400 → delete → 204 → 404
  - Purchase: create draft → complete (inventory +10) → cancel completed (inventory −10) → double cancel (400) → delete cancelled (400)

## 6. Next Implementation Plans (Priority Order)

### Purchase module (completed)
- `Purchase` / `PurchaseItems` / `PurchaseStatus` entities: org-scoped, status lifecycle DRAFT → COMPLETED/CANCELLED
- `PurchaseRepo`: find by org+id, paginated list by org, list by org
- `PurchaseService` + `PurchaseController`: full lifecycle
  - `POST /v1/purchases` — create DRAFT purchase with items (validates org, supplier, product ownership; quantity > 0)
  - `GET /v1/purchases/{purchaseId}` — get purchase (org-scoped)
  - `GET /v1/purchases` — paginated list (org-scoped)
  - `PATCH /v1/purchases/{purchaseId}` — update DRAFT purchase (items, supplier, date; recalculates totals)
  - `POST /v1/purchases/{purchaseId}/complete` — DRAFT → COMPLETED, increases inventory per item
  - `POST /v1/purchases/{purchaseId}/cancel` — DRAFT → CANCELLED (no inventory impact); COMPLETED → CANCELLED (reverses inventory); CANCELLED → blocked
  - `DELETE /v1/purchases/{purchaseId}` — delete DRAFT only
- Auto-calculates line totals, subtotal, tax, total
- DTOs: `CreatePurchaseRequest`, `PurchaseItemRequest`, `UpdatePurchaseRequest`, `PurchaseResponse`, `PurchaseItemResponse`
- Validation: duplicate completion blocked, already-cancelled blocked, insufficient stock on reverse blocked

## 6. Next Implementation Plans (Priority Order)

### Phase 1 — Purchase Workflow
✅ Complete

### Phase 2 — Sale Workflow
Link sales to inventory stock decrease.
- `Sale` / `SalesItems` / `SalesStatus` entities: ✅ Complete (org-scoped, status DRAFT/CONFIRMED/CANCELLED, items with cascade)
- Repo/Service/Controller: ⬜ Pending
  - `SaleRepo`: find by org+id, paginated list by org
  - `SaleService`: create sale, confirm sale (triggers Inventory removeStock for each item, validate no negative stock)
  - `SaleController`: `POST /v1/sales`, `GET /v1/sales/{id}`, `GET /v1/sales`, `PATCH /v1/sales/{id}`
- On confirmation → call `InventoryService.removeStock` for each item (decrease quantity)
- Prevent confirming a sale that would drive stock below zero

### Phase 4 — Cross-Cutting Improvements
- Add pagination/filtering to all list endpoints (products, customers, suppliers)
- Add integration tests (Testcontainers) and stronger validation
- Consider Flyway/Liquibase for schema migrations
- Add service-level authorization helper to centralize org scoping
- Soft delete for products (currently hard delete)

## 7. Logical Module Sequence

Product → Inventory → Customer → Supplier → Purchase → Sale

You are currently at: **Inventory, Customer, Supplier, Purchase complete; Purchase & Sale entities done; Sale repo/service/controller next.**

## 8. How to Run

```bash
cd /Users/mohan-23529/myspace/core
./mvnw spring-boot:run        # starts at http://localhost:8080/erp/api
./mvnw test                   # run tests
./mvnw clean package          # build jar
```

Prerequisites: Java 17, MySQL on `localhost:3306` (dev profile auto-creates `erp` database).
