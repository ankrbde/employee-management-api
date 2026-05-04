# Employee Management API

**Production-Style Event-Driven Backend System**

---

## Overview

This project is a **production-oriented backend system** that demonstrates how a typical CRUD service evolves into a **scalable, event-driven architecture**.

The focus is not just functionality, but **engineering maturity**:

* Designing for failure
* Ensuring data consistency across boundaries
* Enabling horizontal scalability
* Making explicit trade-offs between simplicity and correctness

---

## What This System Demonstrates

* Layered architecture (Controller → Service → Repository)
* Strict DTO boundaries (no entity leakage)
* Soft delete strategy (audit-friendly data lifecycle)
* Offset pagination with filtering
* Consistent exception handling (404, 409, 500)
* Database indexing for query performance
* Redis-based read caching
* Transactional Outbox Pattern for reliable event publishing
* Kafka-based event-driven communication

---

## Tech Stack

* Java + Spring Boot
* PostgreSQL (Docker)
* JPA / Hibernate
* Redis (Spring Cache)
* Kafka
* Lombok
* Logback

---

## System Architecture

```mermaid
flowchart LR

A[REST API - Spring Boot] --> B[PostgreSQL employees table]
A --> C[PostgreSQL outbox_events table]

C --> D[Outbox Publisher Scheduled Worker]
D --> E[Kafka Producer]

E --> F[Kafka Topic employee-events]

F --> G1[Audit Consumer Service]
F --> G2[DLQ Consumer Service]

G1 --> H1[Audit Storage]
G2 --> H2[Dead Letter Handling]

A --> R[Redis Cache]
```

---

## Architecture Flow (Write Path)

1. API request hits `EmployeeService`
2. Employee is persisted in PostgreSQL
3. A corresponding event is written to `outbox_events` (same transaction)
4. Background publisher polls outbox table
5. Events are published to Kafka
6. Consumers process events independently

This ensures:

* No lost events
* No dual-write inconsistency
* Safe retries

---

## Key Design Decisions

### 1. Transactional Outbox Pattern

Instead of publishing directly to Kafka within the request flow, events are first written to a database-backed outbox.

**Why this matters:**

* Avoids dual-write problems (DB + Kafka inconsistency)
* Guarantees event durability
* Enables retries without data loss

**Implementation details:**

* `processed = false` flag controls lifecycle
* Batched polling using a scheduled worker
* `FOR UPDATE SKIP LOCKED` ensures safe parallel processing
* Events marked processed only after successful publish

---

### 2. Event-Driven Architecture (Kafka)

* Domain events (`EmployeeEvent`) are emitted for all state changes
* Kafka acts as the central event backbone
* Consumers are decoupled and independently scalable

This allows:

* Async workflows
* Service decoupling
* Future extensibility (notifications, analytics, etc.)

---

### 3. Redis Caching (Read Optimization)

* `@Cacheable` used for employee reads
* `@CacheEvict` ensures cache consistency on updates/deletes

Trade-off:

* Slight complexity increase for significant read performance gains

---

### 4. Soft Delete Strategy

Instead of deleting records:

* Records are marked `INACTIVE`

Benefits:

* Preserves history
* Prevents accidental data loss
* Aligns with audit/compliance requirements

---

### 5. DTO-Based API Design

Entities are never exposed externally.

Benefits:

* Prevents tight coupling
* Enables independent API evolution
* Avoids ORM-related issues (lazy loading, serialization)

---

### 6. Validation Strategy

Email uniqueness enforced at:

* Application layer (better UX)
* Database layer (strong consistency)

---

### 7. Pagination Strategy

* Offset-based pagination implemented

Trade-off:

* Simple but inefficient for large datasets

Planned:

* Cursor-based pagination for scalability

---

## Components

### Employee Service

* Handles CRUD operations
* Applies business validation
* Writes to database and outbox

---

### Outbox Publisher

* Scheduled worker (`@Scheduled`)
* Polls unprocessed events in batches
* Publishes to Kafka
* Marks events as processed

---

### Kafka Consumers (Audit / DLQ)

* Consume `employee-events`
* Persist audit logs
* Handle failure scenarios (DLQ path)

---

### Redis Cache

* Speeds up read-heavy operations
* Keeps frequently accessed data in memory

---

## API Endpoints

### Create Employee

```http
POST /employees
```

```bash
curl -X POST http://localhost:8080/employees \
-H "Content-Type: application/json" \
-d '{
  "name": "Test User",
  "email": "test@example.com"
}'
```

---

### Get Employee

```http
GET /employees/{id}
```

---

### Update Employee

```http
PUT /employees/{id}
```

---

### Delete Employee (Soft Delete)

```http
DELETE /employees/{id}
```

---

### List Employees

```http
GET /employees?page=0&size=10&departmentId=<optional>
```

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

---

## Testing Strategy (Current State)

* Unit tests for service layer (mocked dependencies)
* Repository tests using JPA test slice
* Focus on:

    * Validation logic
    * Outbox event creation
    * Soft delete behavior

---

## Limitations (Current Design)

* Polling-based outbox (not real-time)
* Scheduler introduces latency (few seconds)
* Single-table outbox may need partitioning at scale

---

## Roadmap

### Scalability Improvements

* Cursor-based pagination
* Partitioned outbox processing
* Parallel publisher scaling

---

### Eventing Evolution

* Replace polling with CDC (Debezium)
* Stream database changes directly to Kafka
* Achieve near real-time propagation

---

### Performance

* Advanced Redis strategies (TTL, eviction tuning)
* Load testing (k6 / JMeter)

---

### Observability

* Metrics (Prometheus + Grafana)
* Structured logging
* Distributed tracing

---

### Reliability

* Idempotent consumers
* Retry + backoff strategies
* DLQ automation

---

### CI/CD

* Integration tests with Testcontainers
* GitHub Actions pipeline

---

## Design Philosophy

This system is intentionally built to reflect **real-world backend evolution**:

* Start simple (CRUD)
* Introduce consistency guarantees (Outbox)
* Move to asynchronous systems (Kafka)
* Optimize reads (Redis)
* Prepare for distributed scaling

The emphasis is on:

* Explicit trade-offs
* Incremental complexity
* Production readiness over shortcuts

---

## Next Step

Introduce **Change Data Capture (CDC)** using Debezium:

* Eliminate polling
* Stream DB changes directly to Kafka
* Reduce latency
* Align with industry-standard event streaming architectures

---
