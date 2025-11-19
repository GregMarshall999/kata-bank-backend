package com.exalt_company.kata_bank.repository;

import com.exalt_company.kata_bank.entity.BankFund;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BankFundRepository extends BaseRepository<BankFund> {
    Optional<BankFund> findByOwnerId(UUID ownerId);
}
