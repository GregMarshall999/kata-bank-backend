package com.exalt_company.user_domain.api;

import com.exalt_company.user_domain.domain.account.AdminResponse;
import com.exalt_company.user_domain.domain.account.BankUserAccount;
import com.exalt_company.user_domain.domain.shared.Page;

import java.util.UUID;

public interface AccountManagement {
    AdminResponse deleteAccount(UUID userId);
    AdminResponse editAccount(UUID userId, BankUserAccount user);
    Page<BankUserAccount> listAccounts(int page, int size);
}
