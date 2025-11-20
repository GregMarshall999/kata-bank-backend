package com.exalt_company.user_domain.spi;

import com.exalt_company.user_domain.domain.account.BankUserAccount;
import com.exalt_company.user_domain.shared.exception.AuthenticationException;

/**
 * SPI port for authentication requests
 * @param <T> custom token type
 */
public interface Authentication<T> {
    /**
     * Used for existing user authentication
     * @param userAccount user to authenticate
     * @return custom token
     * @throws AuthenticationException on authentication error
     */
    T generateLoginUserToken(BankUserAccount userAccount) throws AuthenticationException;

    /**
     * Used for new user authentication
     * @param userAccount user to authenticate
     * @return custom token
     * @throws AuthenticationException on authentication error
     */
    T generateNewUserToken(BankUserAccount userAccount) throws AuthenticationException;
}
