package com.exalt_company.kata_bank.repository;

import com.exalt_company.kata_bank.entity.BankTransaction;
import org.springframework.stereotype.Repository;

@Repository
public interface BankTransactionRepository extends BaseRepository<BankTransaction> {
}
