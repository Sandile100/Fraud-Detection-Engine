package za.co.capitec.domain.rules;

import za.co.capitec.domain.model.RuleResult;
import za.co.capitec.domain.model.Transaction;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class BlacklistedMerchantRule implements FraudRule {

    private final Set<String> blacklistedMerchants;
    private final int score;

    public BlacklistedMerchantRule(Set<String> blacklistedMerchants, int score) {
        this.blacklistedMerchants = Objects.requireNonNull(blacklistedMerchants, "blacklistedMerchants must not be null")
                .stream()
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toUnmodifiableSet());

        if (score <= 0) {
            throw new IllegalArgumentException("score must be greater than zero");
        }
        this.score = score;
    }

    @Override
    public String getName() {
        return "BLACKLISTED_MERCHANT";
    }

    @Override
    public RuleResult evaluate(Transaction transaction) {
        String merchant = transaction.getMerchant().trim().toLowerCase();

        if (blacklistedMerchants.contains(merchant)) {
            return RuleResult.triggered(
                    getName(),
                    score,
                    "Merchant is blacklisted"
            );
        }

        return RuleResult.passed(getName());
    }
}
