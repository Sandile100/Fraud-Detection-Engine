# Fraud Detection Engine

A modular, production-style fraud detection platform built with **Spring Boot**, **Clean Architecture**, and **Domain-Driven Design (DDD)** principles.
The system evaluates transactions against configurable fraud rules and persists fraud decisions and alerts.

The system evaluates incoming financial transactions against configurable fraud rules and produces:

* Fraud checks (risk score + risk level)
* Fraud alerts when suspicious activity is detected

Rules are configurable via YAML, allowing risk policies to be adjusted **without code changes**.

The project demonstrates how a fraud detection engine can be designed to be:

* Modular
* Event-driven
* Testable
* Extensible for real banking systems
---

# Architecture

This project follows **Clean Architecture / Hexagonal Architecture** principles.

```
                +----------------------+
                |      REST API        |
                |  TransactionController|
                +-----------+----------+
                            |
                            v
                +----------------------+
                |   Application Layer  |
                | ProcessTransactionUseCase
                +-----------+----------+
                            |
                            v
                +----------------------+
                |      Domain Layer    |
                | Fraud Rules Engine   |
                +-----------+----------+
                            |
                            v
                +----------------------+
                |   Infrastructure     |
                | PostgreSQL / Kafka   |
                +----------------------+
```

---

# Project Modules

```
fraud-detection-engine
├── fraud-domain
│   └── Core fraud detection rules and domain models
│
├── fraud-application
│   └── Application use cases and command objects
│
├── fraud-api
│   └── REST controllers and request/response DTOs
│
├── fraud-infrastructure
│   └── Persistence adapters and Kafka messaging
│
├── fraud-boot
│   └── Spring Boot runtime and configuration
│
└── docker-compose.yml
```

---

**fraud-api**
REST controllers and DTOs.

**fraud-boot**
Spring Boot application and configuration.

This structure ensures:

* Domain logic is independent of frameworks
* Infrastructure is replaceable
* The system is testable and maintainable

---

# Fraud Rules

Fraud rules are defined in:

```
fraud-boot/src/main/resources/fraud-rules.yml
```

Example configuration:

```yaml
fraud:
  rules:
    large-amount:
      enabled: true
      threshold: 10000
      score: 50

    velocity:
      enabled: true
      window-seconds: 60
      max-transactions: 5
      score: 30

    country-mismatch:
      enabled: true
      score: 40

    blacklisted-merchant:
      enabled: true
      score: 70
      merchants:
        - shady-store
        - scam-pay
```

Example outcome:

| Rule                 | Score    |
| -------------------- | -------- |
| Large Amount         | 50       |
| Country Mismatch     | 40       |
| Blacklisted Merchant | 70       |
| **Total Risk Score** | **160**  |
| **Risk Level**       | **HIGH** |

---

# API Endpoints

## Submit Transaction

```
POST /transactions
```

Example request:

```json
{
  "accountId": "ACC-123",
  "amount": 15000,
  "merchant": "shady-store",
  "country": "US",
  "accountHomeCountry": "ZA"
}
```

Example response:

```json
{
  "transactionId": "a14fa832-3786-406d-b33a-2277365bc546",
  "riskScore": 160,
  "riskLevel": "HIGH",
  "fraudSuspected": true,
  "evaluatedAt": "2026-03-10T20:52:27.908556195Z"
}
```

---

## Get Fraud Check

```
GET /transactions/{transactionId}/fraud-check
```

---

## Get Fraud Alerts

```
GET /fraud-alerts
```

Optional filter:

```
GET /fraud-alerts?transactionId={transactionId}
```

---

# Running the Application

## Run with Maven

```
mvn clean install
cd fraud-boot
mvn spring-boot:run
```

Application runs on:

```
http://localhost:8080
```

---

# Running with Docker

```
docker-compose up --build
```

Services:

| Service        | Port        |
|----------------|-------------|
| Fraud Engine   | 8080        |
| PostgreSQL     | 5432        |
| Kafka          | 9092        |
| Grafana        | 3000        |
| OTLP Collector | 4317 / 4318 |

---

# OpenTelemetry

The application includes **OpenTelemetry tracing** to track fraud evaluation across the system.

Trace example:

```
POST /transactions
   ↓
fraud.processTransaction
   ↓
Fraud rule evaluation
   ↓
Persist fraud_check
   ↓
Persist fraud_alert
```

Configuration example:

```yaml
otel:
  service:
    name: fraud-engine
  exporter:
    otlp:
      endpoint: http://localhost:4318
```

---

# Observability

The project includes a **local observability stack** using Docker.

Components:

| Component  | Purpose             |
| ---------- | ------------------- |
| Grafana    | Dashboards          |
| Tempo      | Distributed tracing |
| Prometheus | Metrics collection  |
| Loki       | Logs                |

Grafana UI:

```
http://localhost:3000
```

Default login:

```
admin
admin
```

---

## Prometheus Metrics

Metrics endpoint:

```
http://localhost:8080/actuator/prometheus
```

---

# Testing Strategy

### Domain Tests

Validate fraud rules independently.

Examples:

* VelocityRuleTest
* LargeAmountRuleTest

### Application Tests

Verify use case orchestration with mocked ports.

Example:

* ProcessTransactionUseCaseTest

### Infrastructure Tests

Verify persistence adapters and database queries.

Example:

* TransactionHistoryAdapterIntegrationTest

### API Tests

Run against the full Spring Boot application context.

Location:

```
fraud-boot/src/test
```

---

# Kafka Integration

The fraud detection engine also supports **asynchronous transaction ingestion using Apache Kafka**.

This allows the system to process transactions from multiple banking channels such as:

* ATM
* Branch systems
* Merchant / POS terminals
* Mobile banking
* Payment processors
* Card networks

Kafka enables the system to scale horizontally and process large volumes of transactions in real time.

## Kafka Architecture

```
External Channels
     |
     v
+---------------------+
|     Kafka Topics    |
| fraud.transactions  |
+----------+----------+
           |
           v
+---------------------------+
| TransactionKafkaConsumer  |
+-------------+-------------+
              |
              v
+---------------------------+
| ProcessTransactionUseCase |
+-------------+-------------+
              |
              v
+---------------------------+
| Fraud Detection Engine    |
+-------------+-------------+
              |
              v
+---------------------------+
| PostgreSQL Persistence    |
+---------------------------+
```

Kafka acts as **an additional inbound adapter** while preserving the core application and domain layers.

Both REST and Kafka ingestion paths invoke the same application use case.

---

# Kafka Event Model

Kafka messages contain transaction data and metadata describing the source of the event.

Example Kafka event:

```json
{
  "eventId": "evt-001",
  "source": "ATM",
  "occurredAt": "2026-03-12T10:15:00Z",
  "transaction": {
    "accountId": "ACC-123",
    "amount": 15000,
    "merchant": "shady-store",
    "country": "US",
    "accountHomeCountry": "ZA"
  }
}
```

---

# Kafka Topics

| Topic                    | Description                         |
| ------------------------ | ----------------------------------- |
| `fraud.transactions.v1`  | Incoming transaction events         |
| `fraud.transactions.dlq` | Dead-letter queue for failed events |

---

# Idempotency Handling

Kafka consumers may reprocess messages during retries or partition rebalancing.

To prevent duplicate fraud checks, the system implements **idempotent event processing** using a `processed_event` table.

Each event contains a unique `eventId`.

Processing flow:

```
Kafka Event Received
        |
        v
Check processed_event table
        |
        +-- Already processed → ignore
        |
        +-- Not processed → process transaction
                               |
                               v
                     Save eventId to table
```

---

# Database Schema

### processed_event

| Column       | Description             |
| ------------ | ----------------------- |
| event_id     | Unique event identifier |
| source       | Event origin channel    |
| processed_at | Processing timestamp    |

---

# Testing Strategy

Kafka integration includes multiple test layers.

### Unit Tests

Test the Kafka consumer logic independently.

Example:

```
TransactionKafkaConsumerTest
```

Validates:

* correct mapping of Kafka messages
* invocation of application use case
* duplicate event handling

---

### Integration Tests

Kafka integration tests use **Embedded Kafka** to simulate a real broker.

Example:

```
TransactionKafkaIntegrationTest
```

Validates:

* Kafka listener wiring
* event consumption
* fraud processing pipeline
* idempotency behavior

---

# Running the Project

Start infrastructure services:

```
docker-compose up --build
```

Services started:

| Service      | Port |
| ------------ | ---- |
| Fraud Engine | 8080 |
| PostgreSQL   | 5432 |
| Kafka        | 9092 |
| Grafana LGTM | 3000 |

---

# Technology Stack

* Java 17
* Spring Boot
* Spring Data JPA
* PostgreSQL
* Flyway
* Docker
* JUnit 5
* MockMvc
* Kafka
* OpenTelemetry
* Micrometer / Prometheus
* Grafana

---

# Author

Sandile Mbatha
