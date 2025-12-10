package com.exalt_company.kata_bank.repository;

import com.exalt_company.kata_bank.entity.BankUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankUserRepository extends BaseRepository<BankUser> {
    Optional<BankUser> findByEmail(String email);
    Page<BankUser> findByEmailContainingIgnoreCase(String email, Pageable pageable);
}
