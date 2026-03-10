package za.co.capitec.domain.rules;

import org.junit.jupiter.api.Test;
import za.co.capitec.domain.model.RuleResult;
import za.co.capitec.domain.model.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LargeAmountRuleTest {

    @Test
    void shouldTriggerWhenAmountExceedsThreshold() {
        LargeAmountRule rule = new LargeAmountRule(new BigDecimal("10000"), 50);

        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                "ACC-1",
                new BigDecimal("15000"),
                "Takalot",
                "RSA",
                "RSA",
                Instant.now()
        );

        RuleResult result = rule.evaluate(transaction);

        assertTrue(result.isTriggered());
        assertEquals(50, result.getScore());
        assertEquals("LARGE_AMOUNT", result.getRuleName());
    }

    @Test
    void shouldPassWhenAmountDoesNotExceedThreshold() {
        LargeAmountRule rule = new LargeAmountRule(new BigDecimal("10000"), 50);

        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                "ACC-1",
                new BigDecimal("9000"),
                "Takalot",
                "RSA",
                "RSA",
                Instant.now()
        );

        RuleResult result = rule.evaluate(transaction);

        assertFalse(result.isTriggered());
        assertEquals(0, result.getScore());
        assertEquals("LARGE_AMOUNT", result.getRuleName());
    }
}
