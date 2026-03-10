package za.co.capitec.domain.rules;

import za.co.capitec.domain.model.RuleResult;
import za.co.capitec.domain.model.Transaction;

public class VelocityRule implements FraudRule {

    private final TransactionHistoryPort transactionHistoryPort;
    private final long windowInSeconds;
    private final long maxTransactions;
    private final int score;

    public VelocityRule(
            TransactionHistoryPort transactionHistoryPort,
            long windowInSeconds,
            long maxTransactions,
            int score
    ) {
        if (windowInSeconds <= 0) {
            throw new IllegalArgumentException("windowInSeconds must be greater than zero");
        }
        if (maxTransactions <= 0) {
            throw new IllegalArgumentException("maxTransactions must be greater than zero");
        }
        if (score <= 0) {
            throw new IllegalArgumentException("score must be greater than zero");
        }

        this.transactionHistoryPort = transactionHistoryPort;
        this.windowInSeconds = windowInSeconds;
        this.maxTransactions = maxTransactions;
        this.score = score;
    }

    @Override
    public String getName() {
        return "VELOCITY";
    }

    @Override
    public RuleResult evaluate(Transaction transaction) {
        long count = transactionHistoryPort.countTransactionsForAccountWithinSeconds(transaction, windowInSeconds);

        if (count > maxTransactions) {
            return RuleResult.triggered(
                    getName(),
                    score,
                    "Transaction velocity exceeded max allowed transactions in time window"
            );
        }

        return RuleResult.passed(getName());
    }
}
