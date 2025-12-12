package com.exalt_company.kata_bank.repository;

import com.exalt_company.kata_bank.entity.BankContact;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BankContactRepository extends BaseRepository<BankContact> {
    Optional<BankContact> findByContactId(UUID contactId);
}
