package com.exalt_company.kata_bank.adapter.v1.spi;

import com.exalt_company.kata_bank.entity.BankTransaction;
import com.exalt_company.kata_bank.entity.BankUser;
import com.exalt_company.kata_bank.mapper.TransactionMapper;
import com.exalt_company.kata_bank.repository.BankTransactionRepository;
import com.exalt_company.kata_bank.repository.BankUserRepository;
import com.exalt_company.transaction_domain.domain.Transaction;
import com.exalt_company.transaction_domain.shared.TransactionException;
import com.exalt_company.transaction_domain.spi.Transactions;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class TransactionJpaAdapter implements Transactions {
    private final BankTransactionRepository repository;
    private final BankUserRepository userRepository;

    public TransactionJpaAdapter(BankTransactionRepository repository, BankUserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Transaction> getTransactionsByUserId(UUID userId) throws TransactionException {
        BankUser user = userRepository.findById(userId)
                .orElseThrow(() -> new TransactionException("Transaction user does not exist!"));

        if(user.getTransactions().isEmpty()) return List.of();

        return user.getTransactions().stream().map(bankTransaction -> {
            BankTransaction fetched = repository.findById(bankTransaction.getId()).orElse(null);

            if(fetched == null) return null;

            return TransactionMapper.toDomain(fetched);
        }).toList();
    }

    @Override
    public void createTransactionReport(Transaction transaction, UUID transactionOwner) throws TransactionException {
        BankUser user = userRepository.findById(transactionOwner)
                .orElseThrow(() -> new TransactionException("Transaction user does not exist!"));

        BankTransaction bT = TransactionMapper.fromDomain(transaction);
        BankTransaction savedTransaction = repository.save(bT);

        user.getTransactions().add(savedTransaction);
        userRepository.save(user);
    }
}
