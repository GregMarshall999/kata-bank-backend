package com.exalt_company.kata_bank.mapper;

import com.exalt_company.kata_bank.adapter.v1.resource.AuthRequest;
import com.exalt_company.kata_bank.adapter.v1.resource.SignUpRequest;
import com.exalt_company.user_domain.api.resource.authentication.SignInUser;
import com.exalt_company.user_domain.api.resource.authentication.SignUpUser;

public class AuthMapper {
    private AuthMapper() {}

    public static SignInUser toDomain(AuthRequest request) {
        return new SignInUser(request.email(), request.password());
    }

    public static SignUpUser toDomain(SignUpRequest request) {
        return new SignUpUser(request.name(), request.surname(), request.email(), request.password());
    }
}
