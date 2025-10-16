package com.exalt_company.user_domain.api;

import com.exalt_company.user_domain.domain.account.AuthenticationResponse;
import com.exalt_company.user_domain.domain.account.SignInUser;

public interface AccountAuthentication<T> {
    AuthenticationResponse<T> signInRequest(SignInUser user);
}
