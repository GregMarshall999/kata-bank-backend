package com.exalt_company.kata_bank_api.repository;

import com.exalt_company.kata_bank_api.entity.BankUser;
import com.exalt_company.kata_bank_api.entity.Saving;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SavingRepository extends BaseRepository<Saving> {
    Optional<Saving> findByOwner(BankUser owner);
}
