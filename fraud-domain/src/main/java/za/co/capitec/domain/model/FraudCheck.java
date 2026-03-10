package za.co.capitec.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FraudCheck {

    private final UUID transactionId;
    private final int riskScore;
    private final RiskLevel riskLevel;
    private final List<RuleResult> ruleResults;
    private final Instant evaluatedAt;

    public FraudCheck(
            UUID transactionId,
            int riskScore,
            RiskLevel riskLevel,
            List<RuleResult> ruleResults,
            Instant evaluatedAt
    ) {
        this.transactionId = Objects.requireNonNull(transactionId, "transactionId must not be null");
        this.riskScore = riskScore;
        this.riskLevel = Objects.requireNonNull(riskLevel, "riskLevel must not be null");
        this.ruleResults = List.copyOf(Objects.requireNonNull(ruleResults, "ruleResults must not be null"));
        this.evaluatedAt = Objects.requireNonNull(evaluatedAt, "evaluatedAt must not be null");
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public List<RuleResult> getRuleResults() {
        return ruleResults;
    }

    public Instant getEvaluatedAt() {
        return evaluatedAt;
    }

    public boolean isFraudSuspected() {
        return riskLevel == RiskLevel.HIGH;
    }
}
