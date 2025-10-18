package com.exalt_company.user_domain.spi.stub;

import com.exalt_company.user_domain.domain.account.BankUserAccount;
import com.exalt_company.user_domain.shared.exception.AuthenticationException;
import com.exalt_company.user_domain.spi.Authentication;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InMemoryAuthentication implements Authentication<String> {
    private final Map<UUID, String> userPassword = new HashMap<>();

    @Override
    public String generateLoginUserToken(BankUserAccount userAccount, String password) throws AuthenticationException {
        if(userAccount.getId() == null)
            throw new AuthenticationException("Unable to generate token: Account non existent");

        if(!password.equals(userPassword.get(userAccount.getId())))
            throw new AuthenticationException("Unable to generate token: Wrong Credentials");

        return "stub-token-string";
    }

    @Override
    public String generateNewUserToken(BankUserAccount userAccount, String password) throws AuthenticationException {
        if(userAccount.getId() == null)
            throw new AuthenticationException("Unable to generate token: Account non existent");

        userPassword.put(userAccount.getId(), password);

        return "stub-token-string";
    }
}
