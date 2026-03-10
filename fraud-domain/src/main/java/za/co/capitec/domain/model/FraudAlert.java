package za.co.capitec.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FraudAlert {

    private final UUID id;
    private final UUID transactionId;
    private final int riskScore;
    private final RiskLevel riskLevel;
    private final List<String> triggeredRules;
    private final Instant createdAt;

    public FraudAlert(
            UUID id,
            UUID transactionId,
            int riskScore,
            RiskLevel riskLevel,
            List<String> triggeredRules,
            Instant createdAt
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.transactionId = Objects.requireNonNull(transactionId, "transactionId must not be null");
        this.riskScore = riskScore;
        this.riskLevel = Objects.requireNonNull(riskLevel, "riskLevel must not be null");
        this.triggeredRules = List.copyOf(Objects.requireNonNull(triggeredRules, "triggeredRules must not be null"));
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
    }

    public UUID getId() {
        return id;
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

    public List<String> getTriggeredRules() {
        return triggeredRules;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
