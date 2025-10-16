package com.exalt_company.user_domain.api;

import com.exalt_company.user_domain.domain.account.AuthenticationResponse;
import com.exalt_company.user_domain.domain.account.BankUserAccount;
import com.exalt_company.user_domain.domain.account.AdminResponse;
import com.exalt_company.user_domain.domain.account.SignUpUser;

public interface AccountCreation<T> {
    AdminResponse createCustomAccount(BankUserAccount user);
    AuthenticationResponse<T> signUpRequest(SignUpUser user);
}
