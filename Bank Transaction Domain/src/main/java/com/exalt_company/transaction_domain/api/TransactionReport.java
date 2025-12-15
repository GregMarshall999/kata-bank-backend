package com.exalt_company.transaction_domain.api;

import com.exalt_company.transaction_domain.domain.Transaction;
import com.exalt_company.transaction_domain.shared.TransactionException;

import java.util.List;
import java.util.UUID;

public interface TransactionReport {
    List<Transaction> getUserTransactionHistory(UUID userId) throws TransactionException;
    void reportTransaction(Transaction transaction, UUID transactionOwnerId, UUID transactionSourceId) throws TransactionException;
    void reportExternalTransaction(
            UUID senderId,
            Transaction senderTransaction,
            UUID receiverId,
            Transaction receiverTransaction
    ) throws TransactionException;
}
