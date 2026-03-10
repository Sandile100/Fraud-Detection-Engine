package za.co.capitec.domain.rules;

import org.junit.jupiter.api.Test;
import za.co.capitec.domain.model.RuleResult;
import za.co.capitec.domain.model.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CountryMismatchRuleTest {

    @Test
    void shouldTriggerWhenTransactionCountryDiffersFromHomeCountry() {
        CountryMismatchRule rule = new CountryMismatchRule(40);

        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                "ACC-1",
                new BigDecimal("500"),
                "Takealot",
                "US",
                "ZA",
                Instant.now()
        );

        RuleResult result = rule.evaluate(transaction);

        assertTrue(result.isTriggered());
        assertEquals(40, result.getScore());
        assertEquals("COUNTRY_MISMATCH", result.getRuleName());
    }

    @Test
    void shouldPassWhenTransactionCountryMatchesHomeCountry() {
        CountryMismatchRule rule = new CountryMismatchRule(40);

        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                "ACC-1",
                new BigDecimal("500"),
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
