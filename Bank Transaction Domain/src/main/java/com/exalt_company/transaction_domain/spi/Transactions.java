package com.exalt_company.transaction_domain.spi;

import com.exalt_company.transaction_domain.domain.Transaction;
import com.exalt_company.transaction_domain.shared.TransactionException;

import java.util.List;
import java.util.UUID;

public interface Transactions {
    List<Transaction> getTransactionsByUserId(UUID userId) throws TransactionException;
    void createTransactionReport(Transaction transaction, UUID transactionOwner) throws TransactionException;
}
