package com.exalt_company.user_domain.domain;

import com.exalt_company.user_domain.api.AccountAuthentication;
import com.exalt_company.user_domain.api.resource.AuthenticationResponse;
import com.exalt_company.user_domain.api.resource.SignInUser;
import com.exalt_company.user_domain.api.resource.SignUpUser;
import com.exalt_company.user_domain.domain.account.BankUserAccount;
import com.exalt_company.user_domain.exception.AuthenticationException;
import com.exalt_company.user_domain.exception.BankUserException;
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

    /**
     * For a user logging in
     * Finds the user by email then generates an access token
     * If not found or incorrect password, the error is forwarded for proper handling
     * @param user login user
     * @return access token on success
     * @throws AuthenticationException if not found or bad password
     */
    @Override
    public AuthenticationResponse<String> signInRequest(SignInUser user) throws AuthenticationException {
        BankUserAccount account;

        try {
            account = bankUsers.findByEmail(user.email());
        } catch (BankUserException e) {
            throw new AuthenticationException(e.getMessage());
        }

        return new AuthenticationResponse<>(authentication.generateLoginUserToken(account, user.password()));
    }

    /**
     * For a new user account
     * Requests the generation of a new bank user account then sends an access token when successful
     * User and token generation errors are caught and passed on for later proper handling
     * @param user the new user
     * @return an access token on success
     * @throws AuthenticationException if any error interrupts the signup flow
     */
    @Override
    public AuthenticationResponse<String> signUpRequest(SignUpUser user) throws AuthenticationException {
        BankUserAccount account = BankUserAccount.fromSignUp(user);

        try {
            account = bankUsers.createAccount(account, user.password());
        } catch (BankUserException e) {
            throw new AuthenticationException(e.getMessage());
        }

        return new AuthenticationResponse<>(authentication.generateNewUserToken(account, user.password()));
    }
}
