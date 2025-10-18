package com.exalt_company.user_domain.spi;

import com.exalt_company.user_domain.domain.account.BankUserAccount;
import com.exalt_company.user_domain.shared.exception.AuthenticationException;

/**
 * SPI port for authentication requests
 * @param <T> custom token type
 */
public interface Authentication<T> {
    T generateLoginUserToken(BankUserAccount userAccount, String password) throws AuthenticationException;
    T generateNewUserToken(BankUserAccount userAccount, String password) throws AuthenticationException;
}
