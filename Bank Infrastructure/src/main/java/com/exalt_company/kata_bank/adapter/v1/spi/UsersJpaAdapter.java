package com.exalt_company.kata_bank.adapter.v1.spi;

import com.exalt_company.kata_bank.entity.BankUser;
import com.exalt_company.kata_bank.mapper.BankUserMapper;
import com.exalt_company.kata_bank.repository.BankUserRepository;
import com.exalt_company.user_domain.domain.account.BankUserAccount;
import com.exalt_company.user_domain.shared.Page;
import com.exalt_company.user_domain.shared.exception.BankUserException;
import com.exalt_company.user_domain.spi.BankUsers;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * JPA adapter implementation of the BankUsers SPI.
 * Provides persistence operations for bank user accounts using JPA repositories.
 * Handles password encoding before persisting user accounts.
 */
@Component
public class UsersJpaAdapter implements BankUsers {
    private final BankUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsersJpaAdapter(BankUserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public BankUserAccount createAccount(BankUserAccount userAccount) throws BankUserException {
        try {
            BankUser user = BankUserMapper.fromDomain(userAccount);
            // Hash password before saving
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            BankUser saved = repository.save(user);
            return BankUserMapper.toDomain(saved);
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            throw new BankUserException("Could not create account: " + e.getMessage());
        }
    }

    //TODO: return an exception for this case
    @Override
    public boolean deleteAccount(UUID userId) {
        try {
            repository.deleteById(userId);
            return true;
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            return false;
        }
    }

    @Override
    public BankUserAccount editAccount(UUID userId, BankUserAccount userAccount) throws BankUserException {
        if(userId == null) throw new BankUserException("Failed to edit account: id required");

        repository.findById(userId).orElseThrow(() -> new BankUserException("Failed to edit account: does not exist"));

        userAccount.setId(userId);

        try {
            BankUser edited = repository.save(BankUserMapper.fromDomain(userAccount));
            return BankUserMapper.toDomain(edited);
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            throw new BankUserException("Could not edit account: " + e.getMessage());
        }
    }

    @Override
    public BankUserAccount findByEmail(String email) throws BankUserException {
        return BankUserMapper.toDomain(
                repository.findByEmail(email).orElseThrow(() -> new BankUserException("User not found"))
        );
    }

    @Override
    public BankUserAccount findById(UUID userId) throws BankUserException {
        return BankUserMapper.toDomain(
                repository.findById(userId).orElseThrow(() -> new BankUserException("User not found"))
        );
    }

    @Override
    public Page<BankUserAccount> pageAccounts(int page, int size) throws BankUserException {
        if(page < 0 || size < 1) throw new BankUserException("Wrong parameter values");

        org.springframework.data.domain.Page<BankUser> all = repository.findAll(PageRequest.of(page, size));

        return new Page<>(
                BankUserMapper.toDomain(all.getContent()),
                all.getNumber(),
                all.getTotalPages(),
                all.getTotalElements()
        );
    }

    @Override
    public Page<BankUserAccount> searchAccountsByEmail(String email, int page, int size) throws BankUserException {
        if(page < 0 || size < 1) throw new BankUserException("Wrong parameter values");

        org.springframework.data.domain.Page<BankUser> similarContacts =
                repository.findByEmailContainingIgnoreCase(email, PageRequest.of(page, size));

        return new Page<>(
                BankUserMapper.toDomain(similarContacts.getContent()),
                similarContacts.getNumber(),
                similarContacts.getTotalPages(),
                similarContacts.getTotalElements()
        );
    }
}
