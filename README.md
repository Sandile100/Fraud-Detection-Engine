# Capitec Fraud Detection Engine

A modular **Fraud Rule Engine** built with **Java 17, Spring Boot, DDD, and Clean Architecture**.

The system evaluates incoming financial transactions against configurable fraud rules and produces:

* Fraud checks (risk score + risk level)
* Fraud alerts when suspicious activity is detected

Rules are configurable via YAML, allowing risk policies to be adjusted **without code changes**.

---

# Architecture

This project follows **Clean Architecture / Hexagonal Architecture** principles.

```
HTTP Request
   ↓
Controller (API)
   ↓
Use Case (Application)
   ↓
Domain Rules (Fraud Engine)
   ↓
Ports
   ↓
Infrastructure Adapters (JPA / Database)
```

## Modules

**fraud-domain**
Business rules and domain models.

**fraud-application**
Use cases and port interfaces.

**fraud-infrastructure**
Persistence adapters (JPA + database).

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
| -------------- | ----------- |
| Fraud Engine   | 8080        |
| PostgreSQL     | 5432        |
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

# Grafana Observability Stack

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

# Technology Stack

* Java 17
* Spring Boot
* Spring Data JPA
* PostgreSQL
* Flyway
* Docker
* JUnit 5
* MockMvc
* OpenTelemetry
* Micrometer / Prometheus
* Grafana

---

# Author

Sandile Mbatha
