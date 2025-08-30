package com.exalt_company.kata_bank_api.repository;

import com.exalt_company.kata_bank_api.entity.BankUser;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * The final step before the database.
 * The repository will have all ORM functions to call database events.
 * The marker helps for the injection in the service.
 */
@Repository
public interface BankUserRepository extends BaseRepository<BankUser> {
    Optional<BankUser> findByCredentialsEmail(String email);
}
