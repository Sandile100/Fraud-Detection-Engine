CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    account_id VARCHAR(100) NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    merchant VARCHAR(255) NOT NULL,
    country VARCHAR(20) NOT NULL,
    account_home_country VARCHAR(20) NOT NULL,
    timestamp TIMESTAMP NOT NULL
);

CREATE TABLE fraud_checks (
    transaction_id UUID PRIMARY KEY,
    risk_score INT NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    evaluated_at TIMESTAMP NOT NULL,
    suspected_fraud BOOLEAN NOT NULL
);

CREATE TABLE fraud_alerts (
    id UUID PRIMARY KEY,
    transaction_id UUID NOT NULL,
    risk_score INT NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    triggered_rules VARCHAR(2000) NOT NULL,
    created_at TIMESTAMP NOT NULL
);