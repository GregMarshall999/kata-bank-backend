package com.exalt_company.user_domain.domain;

import com.exalt_company.user_domain.api.AccountManagement;
import com.exalt_company.user_domain.api.resource.management.AdminResponse;
import com.exalt_company.user_domain.api.resource.management.AdminResponseState;
import com.exalt_company.user_domain.ddd.UserDomainService;
import com.exalt_company.user_domain.domain.account.BankUserAccount;
import com.exalt_company.user_domain.shared.Page;
import com.exalt_company.user_domain.shared.exception.BankUserException;
import com.exalt_company.user_domain.spi.BankUsers;

import java.util.UUID;

/**
 * Default Account Management implementation
 */
@UserDomainService
public class AccountManager implements AccountManagement {
    private final BankUsers bankUsers;

    public AccountManager(BankUsers bankUsers) {
        this.bankUsers = bankUsers;
    }

    public AdminResponse<BankUserAccount> createCustomAccount(BankUserAccount user) {
        AdminResponse<BankUserAccount> response;

        try {
            if(user.getPassword() == null) {
                String password = "random-admin-password";
                user.setPassword(password);
            }

            BankUserAccount created = bankUsers.createAccount(user);
            response = new AdminResponse<>(created, AdminResponseState.CREATED);
        } catch (BankUserException e) {
            response = new AdminResponse<>(
                    AdminResponseState.FAILED,
                    "Could not create user: " + e.getMessage()
            );
        }

        return response;
    }

    @Override
    public AdminResponse<Boolean> deleteAccount(UUID userId) {
        boolean deleted = bankUsers.deleteAccount(userId);

        return deleted ?
                new AdminResponse<>(true, AdminResponseState.DELETED)
                :
                new AdminResponse<>(false, AdminResponseState.FAILED, "Could not delete user");
    }

    @Override
    public AdminResponse<BankUserAccount> editAccount(UUID userId, BankUserAccount user) {
        if(userId == null) return new AdminResponse<>(AdminResponseState.FAILED, "No user to edit");

        try {
            return new AdminResponse<>(bankUsers.editAccount(userId, user), AdminResponseState.EDITED);
        } catch (BankUserException e) {
            return new AdminResponse<>(AdminResponseState.FAILED, "Could not edit user: " + e.getMessage());
        }
    }

    @Override
    public AdminResponse<Page<BankUserAccount>> listAccounts(int page, int size) {
        try {
            return new AdminResponse<>(bankUsers.pageAccounts(page, size), AdminResponseState.PAGED);
        } catch (BankUserException e) {
            return new AdminResponse<>(
                    AdminResponseState.FAILED,
                    "Could not fetch users: " + e.getMessage()
            );
        }
    }

    @Override
    public AdminResponse<BankUserAccount> getAccount(UUID userId) {
        try {
            return new AdminResponse<>(bankUsers.findById(userId), AdminResponseState.FOUND);
        } catch (BankUserException e) {
            return new AdminResponse<>(
                    AdminResponseState.FAILED,
                    "Could not find the account: " + e.getMessage()
            );
        }
    }
}
