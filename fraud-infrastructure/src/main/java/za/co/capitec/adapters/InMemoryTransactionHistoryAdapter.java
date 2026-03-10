package za.co.capitec.adapters;

import org.springframework.stereotype.Component;
import za.co.capitec.domain.model.Transaction;
import za.co.capitec.domain.rules.TransactionHistoryPort;

@Component
public class InMemoryTransactionHistoryAdapter implements TransactionHistoryPort {

    @Override
    public long countTransactionsForAccountWithinSeconds(Transaction transaction, long seconds) {
        return 0L;
    }
}
