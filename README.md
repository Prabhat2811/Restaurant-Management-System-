# 🍽️ Savor — Food Ordering & Restaurant Management System

A full-stack food ordering platform built with **Spring Boot** and **Vanilla HTML/CSS/JS**, featuring customer ordering, restaurant management, delivery tracking, and a comprehensive admin dashboard.

---

## 📌 Table of Contents

- [Tech Stack](#tech-stack)
- [Architecture Overview](#architecture-overview)
- [Features](#features)
- [Data Model & Entities](#data-model--entities)
- [Business Logic & Rules](#business-logic--rules)
- [API Endpoints](#api-endpoints)
- [Frontend Pages](#frontend-pages)
- [Workflow](#workflow)
- [Setup & Installation](#setup--installation)
- [Default Credentials](#default-credentials)
- [Project Structure](#project-structure)

---

## 🛠️ Tech Stack

### Backend
| Technology | Purpose |
|---|---|
| Java 17+ | Core language |
| Spring Boot 3.x | Application framework |
| Spring Security | Stateless JWT-based authentication |
| Spring Data JPA | ORM and database access |
| Hibernate | JPA implementation |
| MySQL | Relational database |
| BCrypt (via Spring Security) | Password hashing |
| Maven | Build tool |

### Frontend
| Technology | Purpose |
|---|---|
| HTML5 | Page structure |
| CSS3 (inline) | Styling |
| Vanilla JavaScript | Dynamic UI, API calls |
| Fetch API | REST communication with backend |
| localStorage | Client-side session management |

---

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────┐
│               Frontend (Static HTML)            │
│  login · register · restaurants · menu ·        │
│  order · orders · admin · menu-management       │
└────────────────────┬────────────────────────────┘
                     │ HTTP / JSON (Fetch API)
                     │ Authorization: Bearer <token>
┌────────────────────▼────────────────────────────┐
│           Spring Boot REST API                  │
│                                                 │
│  ┌─────────────┐  ┌──────────────────────────┐  │
│  │  Security   │  │      Controllers          │  │
│  │  Filter     │  │  Auth · User · Restaurant │  │
│  │  Chain      │  │  Menu · Order · Category  │  │
│  │  (Stateless)│  │  DeliveryAgent            │  │
│  └─────────────┘  └──────────────┬───────────┘  │
│                                  │               │
│  ┌───────────────────────────────▼───────────┐  │
│  │              Service Layer                 │  │
│  │  Business rules · Validations · Mappings  │  │
│  └───────────────────────────────┬───────────┘  │
│                                  │               │
│  ┌───────────────────────────────▼───────────┐  │
│  │         Repository Layer (JPA)             │  │
│  └───────────────────────────────┬───────────┘  │
└──────────────────────────────────│──────────────┘
                                   │
                    ┌──────────────▼──────────────┐
                    │           MySQL              │
                    │  users · customers ·         │
                    │  restaurants · menu_items ·  │
                    │  orders · order_items ·      │
                    │  payments · delivery_agents  │
                    └─────────────────────────────┘
```

---

## ✅ Features

### 👤 Authentication & Authorization
- Stateless REST-based auth (no server-side sessions)
- JWT token stored in `localStorage`
- Role-based access: `CUSTOMER`, `ADMIN`
- Registration creates both `User` + `Customer` rows atomically
- `localStorage` stores `customer.id` (not `user.id`) for downstream order operations
- Admin bootstrap endpoint: `POST /api/auth/setup-admin`
- BCrypt password hashing throughout

### 🏪 Restaurant Management (Admin)
- Create, update, delete restaurants
- Toggle restaurant active/inactive status
- View all restaurants with metadata

### 📋 Menu Management (Admin)
- Create, update, delete menu items per restaurant
- Assign items to categories
- Toggle item availability
- Items carry: name, description, price, category, availability flag

### 📦 Order Management
- Customers place orders by selecting items from a restaurant's menu
- Orders contain one or more `OrderItem` rows (item + quantity + price snapshot)
- Total amount computed server-side from order items
- Payment method captured at order creation (CASH, UPI, NET_BANKING, CARD)
- Order status lifecycle enforced:

```
PLACED → CONFIRMED → PREPARING → READY → OUT_FOR_DELIVERY → DELIVERED
                                                          ↘ CANCELLED
```

### 🛵 Delivery Agent Management
- Agents registered in system with name, phone, status
- Admin assigns agent to an order when status is `READY`
- Status transitions to `OUT_FOR_DELIVERY` and `DELIVERED` are **blocked** unless a delivery agent is assigned
- Agent name shown on order row once assigned

### 🔔 Order Status Rules (enforced on frontend)
| Target Status | Requirement |
|---|---|
| `OUT_FOR_DELIVERY` | Delivery agent **must** be assigned; auto-opens assign modal if not |
| `DELIVERED` | Delivery agent **must** be assigned; blocked with toast if not |
| All others | No restriction |

### 🧑‍💼 Admin Dashboard
- Overview stats: total orders, restaurants, customers, agents
- Orders panel: filter by restaurant ID and status
- Change order status inline with enforcement rules
- Assign delivery agent modal
- Restaurant, menu item, category, customer, delivery agent management panels
- Admin-only access enforced via role check

### 👥 Customer Portal
- Browse active restaurants
- View menu by restaurant
- Place orders with item selection
- View personal order history and tracking

---

## 🗃️ Data Model & Entities

### Enums
```java
Role          → CUSTOMER, ADMIN
OrderStatus   → PLACED, CONFIRMED, PREPARING, READY,
                OUT_FOR_DELIVERY, DELIVERED, CANCELLED
PaymentMethod → CASH, UPI, NET_BANKING, CARD
PaymentStatus → PENDING, COMPLETED, FAILED, REFUNDED
AgentStatus   → AVAILABLE, BUSY, OFFLINE
```

### Entity Relationships (ERD Summary)
```
User ──────────── 1:1 ──────────── Customer
                                       │
                                    1:N │
                                     Orders
                                       │
                              ┌────────┴────────┐
                           1:N │                │ 1:1
                          OrderItems         Payment
                              │
                           N:1 │
                           MenuItem
                              │
                           N:1 │
                          Restaurant ─── 1:N ─── MenuItem
                                                    │
                                                 N:1 │
                                               Category

Order ─── N:1 ─── DeliveryAgent
DeliveryAgent ─── 1:1 ─── User
```

### Key JPA Annotations Used
- `@OneToOne`, `@OneToMany`, `@ManyToOne`, `@JoinColumn`
- `@JsonIgnore` on back-references to prevent circular serialization
- `@Builder` on response DTOs
- `@Enumerated(EnumType.STRING)` on all enum fields
- `@GeneratedValue(strategy = GenerationType.IDENTITY)` on all `@Id` fields

---

## 💼 Business Logic & Rules

### Registration
1. Check email not already taken → throw `409` if duplicate
2. Encode password with BCrypt
3. Save `User` with role `CUSTOMER`
4. Save `Customer` linked to that `User`
5. Return `customer.getId()` in response (not `user.getId()`)

### Login
1. Look up `User` by email → throw `401` if not found
2. `passwordEncoder.matches(raw, encoded)` → throw `401` if mismatch
3. Return user info + `customerId` in response body
4. Frontend stores token/customerId in `localStorage`

### Order Placement
1. Validate customer exists
2. Validate restaurant exists and is active
3. Validate each `menuItemId` belongs to the given restaurant
4. Snapshot item price at time of order (not live price)
5. Compute `totalAmount` server-side: `sum(item.price × quantity)`
6. Create `Payment` record with `PENDING` status
7. Set initial `OrderStatus` to `PLACED`

### Order Status Update
- Admin selects new status from dropdown
- Frontend blocks `OUT_FOR_DELIVERY` / `DELIVERED` if `deliveryAgent == null`
- On block: show warning toast; auto-open assign agent modal for `OUT_FOR_DELIVERY`
- Backend applies the status change via `PUT /order/status/{id}?status=`

### Delivery Agent Assignment
- Admin opens assign modal from order row
- Fetches available agents from `/delivery-agent/all`
- `PUT /order/{orderId}/assign-agent/{agentId}` persists the assignment
- UI refreshes order row — agent name shown, "Assign Agent" button hidden

### Password Security
- All passwords encoded with `BCryptPasswordEncoder` on save
- `CommandLineRunner` on startup re-encodes any plain-text passwords found in DB (migration safety)
- No plain-text passwords stored at any point in normal flow

---

## 🔌 API Endpoints

### Auth
| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Register new customer |
| POST | `/api/auth/login` | Public | Login, returns user info |

### Users
| Method | Endpoint | Description |
|---|---|---|
| GET | `/user/all` | List all customers |
| GET | `/user/me` | Current user info |

### Restaurants
| Method | Endpoint | Description |
|---|---|---|
| GET | `/restaurant/all` | List all restaurants |
| GET | `/restaurant/{id}` | Get restaurant by ID |
| POST | `/restaurant/add` | Create restaurant |
| PUT | `/restaurant/update/{id}` | Update restaurant |
| DELETE | `/restaurant/delete/{id}` | Delete restaurant |

### Menu Items
| Method | Endpoint | Description |
|---|---|---|
| GET | `/menu/restaurant/{id}` | Menu for a restaurant |
| POST | `/menu/add` | Add menu item |
| PUT | `/menu/update/{id}` | Update menu item |
| DELETE | `/menu/delete/{id}` | Delete menu item |

### Categories
| Method | Endpoint | Description |
|---|---|---|
| GET | `/category/all` | All categories |
| POST | `/category/add` | Add category |
| DELETE | `/category/delete/{id}` | Delete category |

### Orders
| Method | Endpoint | Description |
|---|---|---|
| POST | `/order/place` | Place a new order |
| GET | `/order/customer/{customerId}` | Orders by customer |
| GET | `/order/restaurant/{restaurantId}` | Orders by restaurant |
| PUT | `/order/status/{orderId}?status=` | Update order status |
| PUT | `/order/{orderId}/assign-agent/{agentId}` | Assign delivery agent |

### Delivery Agents
| Method | Endpoint | Description |
|---|---|---|
| GET | `/delivery-agent/all` | All agents |
| POST | `/delivery-agent/add` | Register agent |
| PUT | `/delivery-agent/update/{id}` | Update agent |
| DELETE | `/delivery-agent/delete/{id}` | Delete agent |

---

## 🖥️ Frontend Pages

| File | Purpose |
|---|---|
| `index.html` | Landing / home page |
| `login.html` | Customer & admin login |
| `register.html` | Customer registration |
| `admin-login.html` | Admin-specific login |
| `restaurants.html` | Browse active restaurants |
| `menu.html` / `menu-view.html` | View restaurant menu |
| `order.html` | Place an order |
| `orders.html` | Customer order history & tracking |
| `admin.html` | Full admin dashboard |
| `menu-management.html` | Admin menu item management |

---

## 🔄 Workflow

### Customer Flow
```
Register → Login → Browse Restaurants → View Menu
→ Select Items → Place Order → View Order Status
```

### Admin Flow
```
Admin Login → Dashboard Overview
    ├── Orders: Load by Restaurant ID → Filter by Status
    │       → Change Status (agent-gated for delivery statuses)
    │       → Assign Delivery Agent
    ├── Restaurants: Add / Edit / Delete
    ├── Menu Items: Add / Edit / Delete per Restaurant
    ├── Categories: Add / Delete
    ├── Customers: View all registered customers
    └── Delivery Agents: Add / Edit / Delete / Assign
```

### Auth Flow
```
POST /api/auth/login
    → Server validates email + BCrypt password match
    → Returns { userId, customerId, name, email, role }
    → Frontend stores customerId in localStorage
    → All subsequent requests use customerId for order operations
```

---

## ⚙️ Setup & Installation

### Prerequisites
- Java 17 or higher
- Maven 3.8+
- MySQL 8.0+
- Any modern browser

### 1. Clone the Repository
```bash
https://github.com/Prabhat2811/Restaurant-Management-System-.git
cd savor
```

### 2. Configure Database
Create a MySQL database:
```sql
CREATE DATABASE savor_db;
```

Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/savor_db
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

### 3. Build & Run
```bash
mvn clean install
mvn spring-boot:run
```

The server starts at `http://localhost:8080`

### 4. Open the App
Navigate to `http://localhost:8080/index.html` in your browser.

---

## 📁 Project Structure

```
savor/
├── src/
│   ├── main/
│   │   ├── java/com/savor/
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java       # SecurityFilterChain, CORS, stateless session
│   │   │   │   └── CorsConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── UserController.java
│   │   │   │   ├── RestaurantController.java
│   │   │   │   ├── MenuItemController.java
│   │   │   │   ├── CategoryController.java
│   │   │   │   ├── OrderController.java
│   │   │   │   └── DeliveryAgentController.java
│   │   │   ├── dto/
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── RegisterRequest.java
│   │   │   │   ├── OrderRequest.java
│   │   │   │   └── ...
│   │   │   ├── entity/
│   │   │   │   ├── User.java
│   │   │   │   ├── Customer.java
│   │   │   │   ├── Restaurant.java
│   │   │   │   ├── MenuItem.java
│   │   │   │   ├── Category.java
│   │   │   │   ├── Order.java
│   │   │   │   ├── OrderItem.java
│   │   │   │   ├── Payment.java
│   │   │   │   └── DeliveryAgent.java
│   │   │   ├── enums/
│   │   │   │   ├── Role.java
│   │   │   │   ├── OrderStatus.java
│   │   │   │   ├── PaymentMethod.java
│   │   │   │   ├── PaymentStatus.java
│   │   │   │   └── AgentStatus.java
│   │   │   ├── exception/
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── repository/
│   │   │   │   └── ...Repository.java (one per entity)
│   │   │   └── service/
│   │   │       └── ...Service.java (one per entity)
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   │           ├── index.html
│   │           ├── login.html
│   │           ├── register.html
│   │           ├── admin.html
│   │           ├── restaurants.html
│   │           ├── menu.html
│   │           ├── order.html
│   │           ├── orders.html
│   │           └── menu-management.html
└── pom.xml
```

---

## 🔒 Security Configuration Summary

- **Session policy**: `STATELESS` — no server-side sessions
- **CSRF**: Disabled (REST API with token auth)
- **CORS**: Configured globally via `CorsConfigurationSource`
- **Permitted without auth**: all static pages, `/api/auth/**`, all REST endpoints (auth enforced client-side via `localStorage` token + `customerId`)
- **Auth failure response**: JSON `{"success": false, "message": "Unauthorized. Please log in."}` (not HTML redirect)
- **Form login**: Disabled
- **HTTP Basic**: Disabled

---

## 🐛 Common Issues & Fixes

| Issue | Fix |
|---|---|
| `ERR_TOO_MANY_REDIRECTS` | Spring Security was redirecting static pages — add them to `permitAll()` |
| `Unexpected token '<'` | Spring returned HTML error page instead of JSON — fixed by custom `authenticationEntryPoint` |
| `PasswordEncoder` bean conflict | Removed duplicate bean definition, kept single `@Bean` in `SecurityConfig` |
| `double path /user/user/me` | Controller had `@RequestMapping("/user")` + `@GetMapping("/user/me")` — fixed to `@GetMapping("/me")` |
| Reactive vs Servlet CORS import | Used `org.springframework.web.cors.CorsConfiguration` (not reactive package) |
| Registration not creating Customer | Fixed service to save both `User` and `Customer` in same transaction |
| `localStorage` storing wrong ID | Changed to store `customer.getId()` instead of `user.getId()` |

---

## 📄 License

MIT License — free to use, modify, and distribute.

---

> Built by **Prabhat Ranjan** · Savor — *Because great food deserves great software.*
