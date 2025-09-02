package com.exalt_company.kata_bank_api.repository;

import com.exalt_company.kata_bank_api.entity.BankUser;
import com.exalt_company.kata_bank_api.entity.Fund;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FundRepository extends BaseRepository<Fund> {
    Optional<Fund> findByOwner(BankUser owner);
}
