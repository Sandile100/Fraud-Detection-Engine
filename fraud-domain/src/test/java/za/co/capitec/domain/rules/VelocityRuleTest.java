package za.co.capitec.domain.rules;

import org.junit.jupiter.api.Test;
import za.co.capitec.domain.model.RuleResult;
import za.co.capitec.domain.model.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VelocityRuleTest {

    @Test
    void shouldTriggerWhenTransactionCountExceedsConfiguredLimit() {
        TransactionHistoryPort historyPort = (transaction, seconds) -> 6L;

        VelocityRule rule = new VelocityRule(historyPort, 60, 5, 30);

        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                "ACC-1",
                new BigDecimal("300"),
                "Takealot",
                "ZA",
                "ZA",
                Instant.now()
        );

        RuleResult result = rule.evaluate(transaction);

        assertTrue(result.isTriggered());
        assertEquals(30, result.getScore());
        assertEquals("VELOCITY", result.getRuleName());
    }

    @Test
    void shouldPassWhenTransactionCountDoesNotExceedConfiguredLimit() {
        TransactionHistoryPort historyPort = (transaction, seconds) -> 5L;

        VelocityRule rule = new VelocityRule(historyPort, 60, 5, 30);

        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                "ACC-1",
                new BigDecimal("300"),
                "Takealot",
                "ZA",
                "ZA",
                Instant.now()
        );

        RuleResult result = rule.evaluate(transaction);

        assertFalse(result.isTriggered());
        assertEquals(0, result.getScore());
    }
}
