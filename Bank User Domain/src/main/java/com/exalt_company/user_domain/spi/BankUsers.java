package com.exalt_company.user_domain.spi;

import com.exalt_company.user_domain.domain.account.BankUserAccount;
import com.exalt_company.user_domain.exception.BankUserException;

/**
 * SPI port for Bank User Account requests
 */
public interface BankUsers {
    BankUserAccount createAccount(BankUserAccount userAccount, String password) throws BankUserException;
    BankUserAccount findByEmail(String email) throws BankUserException;
}
