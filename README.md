# Fraud Detection Engine

A modular, production-style fraud detection platform built with **Spring Boot**, **Clean Architecture**, and **Domain-Driven Design (DDD)** principles.
The system evaluates transactions against configurable fraud rules and persists fraud decisions and alerts.

The project demonstrates how a fraud detection engine can be designed to be:

* Modular
* Event-driven
* Testable
* Extensible for real banking systems

---

# Architecture

The system follows **Clean Architecture / Hexagonal Architecture**, separating domain logic from infrastructure and adapters.

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

# Domain Concepts

### Transaction

Represents a financial activity initiated by a customer.

### Fraud Check

Represents an evaluation of a transaction against fraud rules.

### Fraud Alert

Generated when a rule violation indicates potential fraud.

---

# Fraud Rules

Example rules implemented:

| Rule                | Description                             |
| ------------------- | --------------------------------------- |
| High Amount         | Flags transactions above threshold      |
| Foreign Transaction | Flags transactions outside home country |
| Suspicious Merchant | Flags transactions with risky merchants |

Rules can be extended by adding new domain services.

---

# REST API

Transactions can be submitted synchronously via HTTP.

## Submit Transaction

```
POST /transactions
```

### Example Request

```json
{
  "accountId": "ACC-123",
  "amount": 15000,
  "merchant": "shady-store",
  "country": "US",
  "accountHomeCountry": "ZA"
}
```

### Example Response

```json
{
  "status": "FLAGGED",
  "alerts": [
    "HIGH_AMOUNT",
    "FOREIGN_TRANSACTION"
  ]
}
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

# Observability

The platform includes observability tooling using the **Grafana LGTM stack**.

Components:

* OpenTelemetry
* Prometheus
* Loki
* Grafana

This enables monitoring of:

* fraud rule evaluation
* transaction throughput
* Kafka consumer lag
* database performance

---

# Design Goals

This project demonstrates how to build a fraud detection system that is:

* Event-driven
* Domain-driven
* Horizontally scalable
* Highly testable
* Infrastructure-agnostic

---

# Future Enhancements

Potential improvements include:

* rule engine configuration via database
* machine learning fraud models
* streaming analytics
* rule DSL for fraud policies
* real-time alert notifications
* Kafka Streams risk scoring
* distributed transaction enrichment

---

# Author

Sandile Mbatha

