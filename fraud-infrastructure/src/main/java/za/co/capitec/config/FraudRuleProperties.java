package za.co.capitec.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "fraud.rules")
public class FraudRuleProperties {

    private LargeAmount largeAmount = new LargeAmount();
    private Velocity velocity = new Velocity();
    private CountryMismatch countryMismatch = new CountryMismatch();
    private BlacklistedMerchant blacklistedMerchant = new BlacklistedMerchant();

    public LargeAmount getLargeAmount() {
        return largeAmount;
    }

    public void setLargeAmount(LargeAmount largeAmount) {
        this.largeAmount = largeAmount;
    }

    public Velocity getVelocity() {
        return velocity;
    }

    public void setVelocity(Velocity velocity) {
        this.velocity = velocity;
    }

    public CountryMismatch getCountryMismatch() {
        return countryMismatch;
    }

    public void setCountryMismatch(CountryMismatch countryMismatch) {
        this.countryMismatch = countryMismatch;
    }

    public BlacklistedMerchant getBlacklistedMerchant() {
        return blacklistedMerchant;
    }

    public void setBlacklistedMerchant(BlacklistedMerchant blacklistedMerchant) {
        this.blacklistedMerchant = blacklistedMerchant;
    }

    public static class LargeAmount {
        private boolean enabled = true;
        private BigDecimal threshold = new BigDecimal("10000");
        private int score = 50;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public BigDecimal getThreshold() {
            return threshold;
        }

        public void setThreshold(BigDecimal threshold) {
            this.threshold = threshold;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }
    }

    public static class Velocity {
        private boolean enabled = true;
        private long windowSeconds = 60;
        private long maxTransactions = 5;
        private int score = 30;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public long getWindowSeconds() {
            return windowSeconds;
        }

        public void setWindowSeconds(long windowSeconds) {
            this.windowSeconds = windowSeconds;
        }

        public long getMaxTransactions() {
            return maxTransactions;
        }

        public void setMaxTransactions(long maxTransactions) {
            this.maxTransactions = maxTransactions;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }
    }

    public static class CountryMismatch {
        private boolean enabled = true;
        private int score = 40;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }
    }

    public static class BlacklistedMerchant {
        private boolean enabled = true;
        private int score = 70;
        private List<String> merchants = new ArrayList<>();

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public List<String> getMerchants() {
            return merchants;
        }

        public void setMerchants(List<String> merchants) {
            this.merchants = merchants;
        }
    }
}
