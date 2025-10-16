package com.exalt_company.user_domain.domain.account;

public record AuthenticationResponse<T>(T authenticationToken) {}