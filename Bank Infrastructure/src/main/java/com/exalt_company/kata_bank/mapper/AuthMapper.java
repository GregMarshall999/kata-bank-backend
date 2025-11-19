package com.exalt_company.kata_bank.mapper;

import com.exalt_company.kata_bank.adapter.v1.resource.AuthRequest;
import com.exalt_company.kata_bank.adapter.v1.resource.SignUpRequest;
import com.exalt_company.user_domain.api.resource.authentication.SignInUser;
import com.exalt_company.user_domain.api.resource.authentication.SignUpUser;

/**
 * Mapper utility class for converting between authentication-related objects.
 * Handles mapping between API request objects and domain authentication resources.
 */
public class AuthMapper {
    private AuthMapper() {}

    /**
     * Converts an AuthRequest to a domain SignInUser object.
     *
     * @param request the authentication request from the API layer
     * @return a domain SignInUser object
     */
    public static SignInUser toDomain(AuthRequest request) {
        return new SignInUser(request.email(), request.password());
    }

    /**
     * Converts a SignUpRequest to a domain SignUpUser object.
     *
     * @param request the sign-up request from the API layer
     * @return a domain SignUpUser object
     */
    public static SignUpUser toDomain(SignUpRequest request) {
        return new SignUpUser(request.name(), request.surname(), request.email(), request.password());
    }
}
