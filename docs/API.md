# ERP Core — API Reference

Base URL: `http://localhost:8080/erp/api`

All business endpoints (except auth) require a valid JWT access token set via the `access_token` cookie. Organization identity is derived from the JWT subject (`orgId`) and enforced via `OrgContext` on every request.

---

## 1. Authentication

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `POST` | `/v1/login` | Login, sets `access_token` and `refresh_token` cookies | No |
| `POST` | `/v1/refresh` | Rotates refresh token and issues new access token | No (refresh cookie) |
| `POST` | `/v1/logout` | Revokes refresh token and clears cookies | Yes |
| `POST` | `/v1/logout-all` | Revokes all refresh tokens for the org | Yes |

### Login Request Body
```json
{
  "org_email": "string",
  "password": "string"
}
```

### Login Response
```json
{
  "message": "Login successfull",
  "orgId": 1,
  "status": "success"
}
```

---

## 2. Organization

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `POST` | `/v1/org` | Create organization | Yes |
| `GET` | `/v1/org/{orgId}` | Get organization details | Yes |

### Create Organization Request
```json
{
  "orgName": "string",
  "orgEmail": "string",
  "orgPassword": "string",
  "mobileNo": "string",
  "address": [
    {
      "street": "string",
      "city": "string",
      "district": "string",
      "pincode": "string",
      "state": "string",
      "country": "string"
    }
  ]
}
```

---

## 3. Product

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `POST` | `/v1/products` | Create product | Yes |
| `GET` | `/v1/products/{productId}` | Get product by ID | Yes |
| `GET` | `/v1/products` | Paginated list (`page`, `size`) | Yes |
| `PATCH` | `/v1/products/{productId}` | Partial update product | Yes |
| `DELETE` | `/v1/products/{productId}` | Delete product | Yes |

### Create Product Request
```json
{
  "productCode": "string",
  "productName": "string",
  "purchasePrice": 0.0,
  "sellingPrice": 0.0,
  "taxRate": 0.0,
  "minimumStock": 0.0,
  "unit": "KG"
}
```

### Product Rules
- Products are org-scoped
- `productCode` and `productName` are unique per organization
- Prices must be greater than zero
- Tax rate range: 0–100
- Pageable list supports `page` and `size`

---

## 4. Inventory

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `GET` | `/v1/inventory` | Paginated list of inventory for org (`page`, `size`) | Yes |
| `GET` | `/v1/inventory/{productId}` | Get inventory record for a product | Yes |
| `POST` | `/v1/inventory/{productId}/add` | Add stock (body: `{ "quantity": n }`) | Yes |
| `POST` | `/v1/inventory/{productId}/remove` | Remove stock (body: `{ "quantity": n }`) | Yes |
| `GET` | `/v1/inventory/low-stock` | List inventory at/below product's minimum stock | Yes |

### Add/Remove Stock Request
```json
{
  "quantity": 0.0
}
```

### Inventory Rules
- An inventory record is created automatically when a product is created
- Stock adjustments require a positive quantity (> 0)
- Removing more stock than available is rejected
- Inventory is org-scoped; quantity cannot drop below zero
- Low-stock = quantity ≤ product's minimumStock

---

## 5. Customer

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `POST` | `/v1/customers` | Create customer | Yes |
| `GET` | `/v1/customers/{customerId}` | Get customer by ID | Yes |
| `GET` | `/v1/customers` | Paginated list (`page`, `size`) | Yes |
| `PATCH` | `/v1/customers/{customerId}` | Partial update customer | Yes |
| `DELETE` | `/v1/customers/{customerId}` | Delete customer | Yes |

### Create Customer Request
```json
{
  "customerName": "string",
  "customerEmail": "string",
  "mobileNo": "string",
  "gstNo": "string",
  "address": [
    {
      "street": "string",
      "city": "string",
      "district": "string",
      "pincode": "string",
      "state": "string",
      "country": "string"
    }
  ]
}
```

### Update Customer Request
```json
{
  "customerName": "string",
  "customerEmail": "string",
  "mobileNo": "string",
  "gstNo": "string",
  "address": [...]
}
```

### Customer Rules
- Customers are org-scoped
- `customerEmail` is unique per organization
- Addresses support multiple entries per customer

---

## 6. Supplier

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `POST` | `/v1/suppliers` | Create supplier | Yes |
| `GET` | `/v1/suppliers/{supplierId}` | Get supplier by ID | Yes |
| `GET` | `/v1/suppliers` | Paginated list (`page`, `size`) | Yes |
| `PATCH` | `/v1/suppliers/{supplierId}` | Partial update supplier | Yes |
| `DELETE` | `/v1/suppliers/{supplierId}` | Delete supplier | Yes |

### Create Supplier Request
```json
{
  "supplierName": "string",
  "supplierEmail": "string",
  "mobileNo": "string",
  "gstNo": "string",
  "address": [
    {
      "street": "string",
      "city": "string",
      "district": "string",
      "pincode": "string",
      "state": "string",
      "country": "string"
    }
  ]
}
```

### Update Supplier Request
```json
{
  "supplierName": "string",
  "supplierEmail": "string",
  "mobileNo": "string",
  "gstNo": "string",
  "address": [...]
}
```

### Supplier Rules
- Suppliers are org-scoped
- `supplierEmail` is unique per organization
- Addresses support multiple entries per supplier

---

## 7. Purchase

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `POST` | `/v1/purchases` | Create purchase order (status: DRAFT) | Yes |
| `GET` | `/v1/purchases/{purchaseId}` | Get purchase by ID | Yes |
| `GET` | `/v1/purchases` | Paginated list (`page`, `size`) | Yes |
| `PATCH` | `/v1/purchases/{purchaseId}` | Update draft purchase | Yes |
| `POST` | `/v1/purchases/{purchaseId}/complete` | DRAFT → COMPLETED, increase inventory stock | Yes |
| `POST` | `/v1/purchases/{purchaseId}/cancel` | Cancel purchase (reverses inventory if COMPLETED) | Yes |
| `DELETE` | `/v1/purchases/{purchaseId}` | Delete draft purchase | Yes |

### Create Purchase Request
```json
{
  "supplierId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 10.0,
      "unitPrice": 50.0,
      "taxRate": 5.0,
      "discountRate": 0.0
    }
  ]
}
```

### Update Purchase Request
```json
{
  "supplierId": 1,
  "purchasedDate": "2026-10-18T15:24:24Z",
  "items": [...]
}
```

### Purchase Status Lifecycle
| From → To | Action | Inventory Impact |
|-----------|--------|------------------|
| `DRAFT` → `COMPLETED` | Complete purchase | ✅ Increases stock per item |
| `DRAFT` → `CANCELLED` | Cancel draft | ❌ No inventory change |
| `COMPLETED` → `CANCELLED` | Cancel completed | ✅ Reverses stock per item |
| `CANCELLED` → anything | Blocked | ❌ Not allowed |

### Purchase Rules
- Purchases are org-scoped
- Only `DRAFT` purchases can be updated or deleted
- Only `DRAFT` purchases can be completed (increases inventory per item)
- `COMPLETED` purchases can be cancelled (reverses inventory per item)
- `CANCELLED` purchases cannot be modified
- Auto-calculates line totals, subtotal, tax, and total amounts

---

## 8. Sale

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `POST` | `/v1/sales` | Create sale order (status: DRAFT) | Yes |
| `GET` | `/v1/sales/{saleId}` | Get sale by ID | Yes |
| `GET` | `/v1/sales` | Paginated list (`page`, `size`) | Yes |
| `PATCH` | `/v1/sales/{saleId}` | Update draft sale | Yes |
| `POST` | `/v1/sales/{saleId}/confirm` | DRAFT → COMPLETED, decrease inventory stock | Yes |
| `POST` | `/v1/sales/{saleId}/cancel` | Cancel sale (reverses inventory if COMPLETED) | Yes |
| `DELETE` | `/v1/sales/{saleId}` | Delete draft sale | Yes |

### Create Sale Request
```json
{
  "customerId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 5.0,
      "unitPrice": 65.0,
      "taxRate": 5.0,
      "discountRate": 0.0
    }
  ]
}
```

### Sale Status Lifecycle
| From → To | Action | Inventory Impact |
|-----------|--------|------------------|
| `DRAFT` → `COMPLETED` | Confirm sale | ✅ Decreases stock per item |
| `DRAFT` → `CANCELLED` | Cancel draft | ❌ No inventory change |
| `COMPLETED` → `CANCELLED` | Cancel confirmed | ✅ Reverses stock per item |
| `CANCELLED` → anything | Blocked | ❌ Not allowed |

### Sale Rules
- Sales are org-scoped
- Only `DRAFT` sales can be updated or deleted
- Only `DRAFT` sales can be confirmed (decreases inventory per item)
- `COMPLETED` sales can be cancelled (reverses inventory per item)
- `CANCELLED` sales cannot be modified
- Cannot confirm a sale that would drive stock below zero
- Auto-calculates line totals, subtotal, tax, and total amounts

---

## 9. Error Response Format

All errors return a consistent JSON body:

```json
{
  "errorMessage": "string",
  "errorCode": 400,
  "path": "/erp/api/v1/...",
  "timestamp": "2026-10-18T15:24:24.346079Z"
}
```

### Common HTTP Status Codes
| Code | Meaning |
|------|---------|
| 200 | OK |
| 204 | No Content (successful delete) |
| 400 | Bad Request (validation error, business rule violation) |
| 401 | Unauthorized (missing or invalid access token) |
| 404 | Not Found (entity not found or not accessible by org) |
| 500 | Internal Server Error |

---

## 10. Module Completion Status

| Module | Status | Endpoints Implemented |
|--------|--------|----------------------|
| Authentication | ✅ Complete | login, refresh, logout, logout-all |
| Organization | ✅ Complete | create, get |
| Product | ✅ Complete | create, get, list, update, delete |
| Inventory | ✅ Complete | list, get, add, remove, low-stock |
| Customer | ✅ Complete | create, get, list, update, delete |
| Supplier | ✅ Complete | create, get, list, update, delete |
| Purchase | ✅ Complete | create, get, list, update, complete, cancel, delete |
| Sale | ✅ Complete | create, get, list, update, confirm, cancel, delete |

---

## 11. How to Run

```bash
cd /Users/mohan-23529/myspace/core
./mvnw spring-boot:run        # starts at http://localhost:8080/erp/api
./mvnw test                   # run tests
./mvnw clean package          # build jar
```

**Prerequisites:** Java 17, MySQL on `localhost:3306` (dev profile auto-creates `erp` database).
