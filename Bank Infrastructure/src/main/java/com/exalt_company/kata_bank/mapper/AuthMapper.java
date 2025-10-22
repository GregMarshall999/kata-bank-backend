package com.exalt_company.kata_bank.mapper;

import com.exalt_company.kata_bank.adapter.v1.resource.AuthRequest;
import com.exalt_company.kata_bank.adapter.v1.resource.SignUpRequest;
import com.exalt_company.user_domain.api.resource.authentication.SignInUser;
import com.exalt_company.user_domain.api.resource.authentication.SignUpUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    SignInUser toDomain(AuthRequest request);
    SignUpUser toDomain(SignUpRequest request);
}
