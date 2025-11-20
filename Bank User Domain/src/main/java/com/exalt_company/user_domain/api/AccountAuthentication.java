package com.exalt_company.user_domain.api;

import com.exalt_company.user_domain.api.resource.authentication.AuthenticationResponse;
import com.exalt_company.user_domain.api.resource.authentication.SignInUser;
import com.exalt_company.user_domain.api.resource.authentication.SignUpUser;
import com.exalt_company.user_domain.shared.exception.AuthenticationException;

/**
 * For login and signup purposes
 * @param <T> Custom token type
 */
public interface AccountAuthentication<T> {
    /**
     * Used for user authentication
     * @param user login user
     * @return access token
     * @throws AuthenticationException on auth error
     */
    AuthenticationResponse<T> signInRequest(SignInUser user) throws AuthenticationException;

    /**
     * Used for user creation
     * @param user signup user
     * @return access token
     * @throws AuthenticationException on auth error
     */
    AuthenticationResponse<T> signUpRequest(SignUpUser user) throws AuthenticationException;
}
