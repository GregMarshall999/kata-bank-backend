package com.exalt_company.user_domain.spi.stub;

import com.exalt_company.user_domain.domain.account.BankUserAccount;
import com.exalt_company.user_domain.shared.exception.BankUserException;
import com.exalt_company.user_domain.spi.BankUsers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InMemoryBankUserAccounts implements BankUsers {
    private final Map<UUID, BankUserAccount> bankUserAccounts = new HashMap<>();

    @Override
    public BankUserAccount createAccount(BankUserAccount userAccount, String password) throws BankUserException {
        BankUserAccount found = find(userAccount.getEmail());

        if(found != null) throw new BankUserException("User with this email already exists");

        UUID id = UUID.randomUUID();
        while (bankUserAccounts.containsKey(id)) id = UUID.randomUUID();

        userAccount.setId(id);

        bankUserAccounts.put(id, userAccount);

        return userAccount;
    }

    @Override
    public BankUserAccount findByEmail(String email) throws BankUserException {
        BankUserAccount found = find(email);

        if(found == null) throw new BankUserException("User not found");

        return found;
    }

    private BankUserAccount find(String email) {
        List<BankUserAccount> found = bankUserAccounts.values()
                .stream()
                .filter(userAccount -> email.equals(userAccount.getEmail()))
                .toList();

        if(found.isEmpty()) return null;
        if(found.size() > 1) return null;

        return found.get(0);
    }
}
