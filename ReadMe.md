# Employee Management API (Event-Driven Backend in Progress)

This project is a **production-style backend system** built to simulate how real-world services evolve beyond simple CRUD APIs.

The goal isn’t just to “make it work,” but to **design, build, and iterate like a senior backend engineer**—focusing on scalability, maintainability, and real system trade-offs.

---

## What This Project Demonstrates

* Clean layered architecture (Controller → Service → Repository)
* DTO-based API design (no entity leakage)
* Soft delete strategy (instead of destructive deletes)
* Pagination & filtering (offset-based, with future cursor upgrade)
* Proper exception handling (404, 409, 500)
* Database indexing for performance
* Thoughtful API design and trade-offs

---

## Tech Stack

* Java + Spring Boot
* PostgreSQL (Docker)
* JPA / Hibernate
* Lombok
* Logback

> Upcoming: Redis, Kafka, MongoDB, Load Testing, CI/CD

---

## Architecture Overview

EmployeeService
↓
PostgreSQL (Employee Table)
↓
Outbox Table (event durability layer)
↓
Scheduled Outbox Publisher (@Scheduled polling)
↓
Kafka Topic: employee-events
↓
Kafka Consumer (Audit Service)
↓
Audit Storage (MongoDB / DB)

## ⚙How to Run Locally

### 1. Start PostgreSQL

```bash
docker-compose up -d
```

### 2. Run the application

```bash
./mvnw spring-boot:run -DskipTests
```

### 3. Verify

* App runs on: `http://localhost:8080`
* Logs should show employee creation (via DataLoader)

---

## API Endpoints (Current)

### Create Employee

```http
POST /employees

E.g.

curl -X POST http://localhost:8080/employees \
-H "Content-Type: application/json" \
-d '{
  "name": "Test User",
  "email": "test100@example.com"
}'
```

### Get Employee by ID

```http
GET /employees/{id}
```

### Update Employee

```http
PUT /employees/{id}

E.g.

curl -X PUT http://localhost:8080/employees/<id> \
-H "Content-Type: application/json" \
-d '{
  "name": "Updated User",
  "email": "updated100@example.com"
}' 
```

### Soft Delete Employee

```http
DELETE /employees/{id}

E.g.

curl -X DELETE http://localhost:8080/employees/<id>
```

### List Employees (Pagination + Filter)

```http
GET /employees?page=0&size=10&departmentId=<optional>
```

---

---

## Key Design Patterns Implemented

### 1. Outbox Pattern
Ensures **no event loss between DB and Kafka**

- Events first stored in DB (outbox_events)
- Publisher asynchronously sends to Kafka
- Marks events as processed after successful publish

---

### 2. Event-Driven Architecture
- Employee service emits domain events
- Kafka acts as event backbone
- Consumers independently process events

---

### 3. Idempotent Event Handling
- Event IDs used for traceability
- Consumer designed to handle duplicates safely (future enhancement)

---

##  Components

### Employee Service
- CRUD operations
- Writes to Employee DB
- Writes to Outbox table

---

### Outbox Publisher
- Polls DB every 5 seconds
- Publishes unprocessed events to Kafka
- Marks events as processed

---

### Kafka Consumer (Audit Service)
- Consumes `employee-events`
- Writes audit logs
- Logs successful processing

---

## Tested Flows

###  Employee Creation Flow
1. API call → Employee created
2. Outbox row created
3. Publisher sends Kafka event
4. Consumer receives event
5. Audit log persisted

---

## Sample Event

```json
{
  "eventId": "uuid",
  "eventType": "CREATE",
  "employeeId": "uuid",
  "details": "Employee created"
}
```

### 2. DTO Separation

Entities are **never exposed directly**.

Why:

* Prevents tight coupling
* Enables API evolution
* Avoids lazy-loading issues

---

### 3. Application + DB Validation

Email uniqueness is enforced at:

* Service layer → better user experience
* Database level → data integrity

---

### 4. Offset Pagination (for now)

Simple and fast to implement.

Trade-off:

* Doesn’t scale well for deep pages

Planned: Cursor-based pagination

---

### 5. Indexing Strategy

Indexes added on:

* `status` → for filtering active employees
* `email` → for lookup performance

---

## TODO / Roadmap

### Core Enhancements

* [ ] Cursor-based pagination (replace offset)
* [ ] Sorting support
* [ ] Search (name/email)

### Performance & Scalability

* [ ] Redis caching (read optimization)
* [ ] Cache eviction strategy
* [ ] Load testing (k6 / JMeter)

### Distributed System Features

* [ ] Kafka event publishing (`employee.created`)
* [ ] Notifier service (consumer)
* [ ] Idempotency handling

### Observability & Production Readiness

* [ ] Metrics (Prometheus/Grafana)
* [ ] Structured logging
* [ ] API documentation (OpenAPI)

### Testing & CI/CD

* [ ] Integration tests
* [ ] Testcontainers
* [ ] GitHub Actions pipeline

---

## Future Experiment

A parallel branch will reimplement this system using **Quarkus** to compare:

* Startup time
* Memory usage
* Developer experience

---

## What I’m Practicing

This project is intentionally built to improve:

* Backend system design thinking
* Trade-off analysis (not just implementation)
* Writing production-like code under constraints
* Explaining decisions clearly (interview readiness)

---

## Current Status

✔ CRUD APIs
✔ Pagination & filtering
✔ Soft delete
✔ Exception handling
✔ Indexing

➡ Next: **Redis caching layer**

---

##  Notes

This is an evolving project. The focus is:

> consistency > perfection

Each phase builds toward a **realistic, production-style backend system** rather than a one-off demo.

---