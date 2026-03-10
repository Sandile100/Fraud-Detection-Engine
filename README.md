# Capitec Fraud Detection Engine

A modular **Fraud Rule Engine** built with **Java 17, Spring Boot, DDD, and Clean Architecture**.

The system evaluates incoming financial transactions against configurable fraud rules and produces:

* Fraud checks (risk score + risk level)
* Fraud alerts when suspicious activity is detected

Rules are configurable via YAML, allowing risk policies to be adjusted without code changes.

---

# Architecture

This project follows **Clean Architecture / Hexagonal Architecture** principles.

HTTP Request
→ Controller (API)
→ Use Case (Application)
→ Domain Rules (Fraud Engine)
→ Ports
→ Infrastructure Adapters (JPA / Database)

## Modules

fraud-domain
Business rules and domain models

fraud-application
Use cases and port interfaces

fraud-infrastructure
Persistence adapters (JPA + database)

fraud-api
REST controllers and DTOs

fraud-boot
Spring Boot application + configuration

This structure ensures:

* Domain logic is independent of frameworks
* Infrastructure is replaceable
* The system is testable and maintainable

---

# Fraud Rules

Fraud rules are defined in:

fraud-boot/src/main/resources/fraud-rules.yml

Example:

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

Rule                    | Score

Large Amount            | 50

Country Mismatch        | 40

Blacklisted Merchant    | 70

Total Risk Score = **160**
Risk Level = **HIGH**

---

# API Endpoints

## Submit Transaction

POST /transactions

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
  "evaluatedAt": "2026-03-10T20:52:27.908556195Z",
  "ruleResults": [
    {
      "ruleName": "LARGE_AMOUNT",
      "triggered": true,
      "score": 50,
      "reason": "Transaction amount exceeds threshold of 10000"
    },
    {
      "ruleName": "VELOCITY",
      "triggered": false,
      "score": 0,
      "reason": "Rule passed"
    },
    {
      "ruleName": "COUNTRY_MISMATCH",
      "triggered": true,
      "score": 40,
      "reason": "Transaction country does not match account home country"
    },
    {
      "ruleName": "BLACKLISTED_MERCHANT",
      "triggered": true,
      "score": 70,
      "reason": "Merchant is blacklisted"
    }
  ]
}
```

---

## Get Fraud Check

GET /transactions/{transactionId}/fraud-check

Example response:
```json
{
"transactionId": "a14fa832-3786-406d-b33a-2277365bc546",
"riskScore": 160,
"riskLevel": "HIGH",
"fraudSuspected": true,
"evaluatedAt": "2026-03-10T20:52:27.908556Z",
"ruleResults": []
}
```

---

## Get Fraud Alerts

GET /fraud-alerts

Example response:

```json
[
  {
    "id": "8b5d8db9-8129-471f-94d9-793b425e88d1",
    "transactionId": "a14fa832-3786-406d-b33a-2277365bc546",
    "riskScore": 160,
    "riskLevel": "HIGH",
    "triggeredRules": [
      "LARGE_AMOUNT",
      "COUNTRY_MISMATCH",
      "BLACKLISTED_MERCHANT"
    ],
    "createdAt": "2026-03-10T20:52:27.935983Z"
  }
]
```

Optional filter:

GET /fraud-alerts?transactionId={transactionId}

Example response:

```json
[
  {
    "id": "8b5d8db9-8129-471f-94d9-793b425e88d1",
    "transactionId": "a14fa832-3786-406d-b33a-2277365bc546",
    "riskScore": 160,
    "riskLevel": "HIGH",
    "triggeredRules": [
      "LARGE_AMOUNT",
      "COUNTRY_MISMATCH",
      "BLACKLISTED_MERCHANT"
    ],
    "createdAt": "2026-03-10T20:52:27.935983Z"
  }
]
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

Fraud Engine → port 8080
PostgreSQL → port 5432

Database credentials:

database: frauddb
username: fraud
password: fraud

---

# Example cURL Requests

Submit transaction:

```
curl -X POST http://localhost:8080/transactions \
-H "Content-Type: application/json" \
-d '{
"accountId": "ACC-1",
"amount": 15000,
"merchant": "shady-store",
"country": "US",
"accountHomeCountry": "ZA"
}'
```

Get alerts:

```
curl http://localhost:8080/fraud-alerts
```

Get fraud check:

```
curl http://localhost:8080/transactions/{transactionId}/fraud-check
```

---

# Database Schema

Flyway migrations create:

transactions
fraud_checks
fraud_alerts

---

# Testing Strategy

Tests are structured by architecture layer.

### Domain Tests

Validate fraud rules independently.

Examples:

VelocityRuleTest
LargeAmountRuleTest

### Application Tests

Verify use case orchestration with mocked ports.

Example:

ProcessTransactionUseCaseTest

### Infrastructure Tests

Verify persistence adapters and database queries.

Example:

TransactionHistoryAdapterIntegrationTest

### API Tests

Run against the full Spring Boot application context.

Located in:

fraud-boot/src/test

Example tests:

TransactionControllerApiTest
FraudQueryControllerApiTest

These tests validate the full flow:

HTTP → Controller → Use Case → Infrastructure → Database

---

# Velocity Fraud Rule

Detects rapid transaction bursts.

Example configuration:

```yaml
velocity:
  window-seconds: 60
  max-transactions: 5
```

If more than 5 transactions occur within 60 seconds, the rule triggers.

---

# Technology Stack

Java 17
Spring Boot
Spring Data JPA
PostgreSQL
Flyway
Docker
JUnit 5
MockMvc

---

# Design Decisions

* Clean Architecture to isolate business rules
* Fraud rules configurable via YAML
* Ports & Adapters to decouple persistence
* Multi-module Maven structure

---

# Future Improvements

- Persist rule evaluation results
- Add Kafka fraud event streaming
- Add monitoring with Prometheus/Grafana
- Add fraud analytics dashboard

---

# Author

Sandile Mbatha

