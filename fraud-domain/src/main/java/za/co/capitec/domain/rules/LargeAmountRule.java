package za.co.capitec.domain.rules;

import za.co.capitec.domain.model.RuleResult;
import za.co.capitec.domain.model.Transaction;

import java.math.BigDecimal;
import java.util.Objects;

public class LargeAmountRule implements FraudRule {

    private final BigDecimal threshold;
    private final int score;

    public LargeAmountRule(BigDecimal threshold, int score) {
        this.threshold = Objects.requireNonNull(threshold, "threshold must not be null");
        this.score = score;

        if (threshold.signum() < 0) {
            throw new IllegalArgumentException("threshold must not be negative");
        }
        if (score <= 0) {
            throw new IllegalArgumentException("score must be greater than zero");
        }
    }

    @Override
    public String getName() {
        return "LARGE_AMOUNT";
    }

    @Override
    public RuleResult evaluate(Transaction transaction) {
        if (transaction.getAmount().compareTo(threshold) > 0) {
            return RuleResult.triggered(
                    getName(),
                    score,
                    "Transaction amount exceeds threshold of " + threshold
            );
        }

        return RuleResult.passed(getName());
    }
}
