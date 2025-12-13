package com.exalt_company.transaction_domain.domain;

import com.exalt_company.transaction_domain.api.TransactionReport;
import com.exalt_company.transaction_domain.ddd.TransactionDomainService;
import com.exalt_company.transaction_domain.shared.TransactionException;
import com.exalt_company.transaction_domain.spi.Transactions;

import java.util.List;
import java.util.UUID;

@TransactionDomainService
public class TransactionReporter implements TransactionReport {
    private final Transactions transactions;

    public TransactionReporter(Transactions transactions) {
        this.transactions = transactions;
    }

    @Override
    public List<Transaction> getUserTransactionHistory(UUID userId) throws TransactionException {
        return transactions.getTransactionsByUserId(userId);
    }

    @Override
    public void reportTransaction(Transaction transaction, UUID transactionOwner) throws TransactionException {
        transactions.createTransactionReport(transaction, transactionOwner);
    }
}
