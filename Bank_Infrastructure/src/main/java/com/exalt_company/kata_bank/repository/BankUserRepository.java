package com.exalt_company.kata_bank.repository;

import com.exalt_company.kata_bank.entity.BankUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BankUserRepository extends BaseRepository<BankUser> {
    Optional<BankUser> findByEmail(String email);

    @Query("SELECT u FROM BankUser u JOIN u.contacts c WHERE c.contactId = :contactId")
    Optional<BankUser> findByContactId(@Param("contactId") UUID contactId);
}
