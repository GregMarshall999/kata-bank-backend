package com.exalt_company.kata_bank.mapper;

import com.exalt_company.kata_bank.adapter.v1.resource.FundRequest;
import com.exalt_company.kata_bank.adapter.v1.resource.TransactionHistory;
import com.exalt_company.kata_bank.adapter.v1.resource.TransactionHistoryType;
import com.exalt_company.kata_bank.entity.BankTransaction;
import com.exalt_company.transaction_domain.domain.Transaction;
import com.exalt_company.transaction_domain.domain.TransactionType;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionMapper {
    static List<TransactionHistory> fromDomain(List<Transaction> userTransactionHistory) {
        return userTransactionHistory.stream().map(transaction -> new TransactionHistory(
                transaction.getName(),
                transaction.getAmount(),
                transaction.getDate(),
                parseType(transaction.getType())
        )).toList();
    }

    static BankTransaction fromDomain(Transaction transaction) {
        return new BankTransaction(
                transaction.getName(),
                transaction.getAmount(),
                transaction.getDate(),
                parseType(transaction.getType())
        );
    }

    static Transaction toDomain(BankTransaction fetchedTransaction) {
        TransactionType type = switch (fetchedTransaction.getType()) {
            case DEPOSIT -> TransactionType.DEPOSIT;
            case WITHDRAW -> TransactionType.WITHDRAW;
            case TRANSFER -> TransactionType.TRANSFER;
        };

        return new Transaction(
                fetchedTransaction.getName(),
                fetchedTransaction.getAmount(),
                fetchedTransaction.getDate(),
                type
        );
    }

    private static TransactionHistoryType parseType(TransactionType type) {
        return switch (type) {
            case DEPOSIT -> TransactionHistoryType.DEPOSIT;
            case WITHDRAW -> TransactionHistoryType.WITHDRAW;
            case TRANSFER -> TransactionHistoryType.TRANSFER;
        };
    }

    static Transaction toDomain(FundRequest request, TransactionType type) {
        return new Transaction(request.operationDescription(), request.amount(), LocalDateTime.now(), type);
    }
}
