package za.co.capitec.domain.rules;

import za.co.capitec.domain.model.RuleResult;
import za.co.capitec.domain.model.Transaction;

public class CountryMismatchRule implements FraudRule {

    private final int score;

    public CountryMismatchRule(int score) {
        if (score <= 0) {
            throw new IllegalArgumentException("score must be greater than zero");
        }
        this.score = score;
    }

    @Override
    public String getName() {
        return "COUNTRY_MISMATCH";
    }

    @Override
    public RuleResult evaluate(Transaction transaction) {
        if (!transaction.getCountry().equalsIgnoreCase(transaction.getAccountHomeCountry())) {
            return RuleResult.triggered(
                    getName(),
                    score,
                    "Transaction country does not match account home country"
            );
        }

        return RuleResult.passed(getName());
    }
}
