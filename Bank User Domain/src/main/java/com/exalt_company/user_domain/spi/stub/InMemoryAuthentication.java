package com.exalt_company.user_domain.spi.stub;

import com.exalt_company.user_domain.domain.account.BankUserAccount;
import com.exalt_company.user_domain.shared.exception.AuthenticationException;
import com.exalt_company.user_domain.spi.Authentication;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InMemoryAuthentication implements Authentication<String> {
    private final Map<UUID, BankUserAccount> bankUserAccounts = new HashMap<>();

    @Override
    public String generateLoginUserToken(BankUserAccount userAccount) throws AuthenticationException {
        if(userAccount.getId() == null)
            throw new AuthenticationException("Unable to generate token: Account non existent");

        BankUserAccount found = bankUserAccounts.get(userAccount.getId());

        if(!found.getPassword().equals(userAccount.getPassword()))
            throw new AuthenticationException("Unable to generate token: Wrong Credentials");

        return "stub-token-string";
    }

    @Override
    public String generateNewUserToken(BankUserAccount userAccount) throws AuthenticationException {
        if(userAccount.getId() == null)
            throw new AuthenticationException("Unable to generate token: Account non existent");

        bankUserAccounts.put(userAccount.getId(), userAccount);

        return "stub-token-string";
    }
}
