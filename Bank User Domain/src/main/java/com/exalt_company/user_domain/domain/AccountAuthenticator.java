package com.exalt_company.user_domain.domain;

import com.exalt_company.user_domain.api.AccountAuthentication;
import com.exalt_company.user_domain.api.resource.AuthenticationResponse;
import com.exalt_company.user_domain.api.resource.SignInUser;
import com.exalt_company.user_domain.api.resource.SignUpUser;
import com.exalt_company.user_domain.domain.account.BankUserAccount;
import com.exalt_company.user_domain.shared.exception.AuthenticationException;
import com.exalt_company.user_domain.shared.exception.BankUserException;
import com.exalt_company.user_domain.spi.Authentication;
import com.exalt_company.user_domain.spi.BankUsers;

/**
 * Default Authentication implementation
 * Token type is String
 */
public class AccountAuthenticator implements AccountAuthentication<String> {
    private final Authentication<String> authentication;
    private final BankUsers bankUsers;

    public AccountAuthenticator(Authentication<String> authentication, BankUsers bankUsers) {
        this.authentication = authentication;
        this.bankUsers = bankUsers;
    }

    @Override
    public AuthenticationResponse<String> signInRequest(SignInUser user) throws AuthenticationException {
        BankUserAccount account;

        try {
            account = new BankUserAccount(bankUsers.findByEmail(user.email()));
        } catch (BankUserException e) {
            throw new AuthenticationException(e.getMessage());
        }

        account.setEmail(user.email());
        account.setPassword(user.password());

        return new AuthenticationResponse<>(authentication.generateLoginUserToken(account));
    }

    @Override
    public AuthenticationResponse<String> signUpRequest(SignUpUser user) throws AuthenticationException {
        BankUserAccount account = BankUserAccount.fromSignUp(user);

        try {
            account = bankUsers.createAccount(account);
        } catch (BankUserException e) {
            throw new AuthenticationException(e.getMessage());
        }

        return new AuthenticationResponse<>(authentication.generateNewUserToken(account));
    }
}
