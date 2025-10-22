package com.exalt_company.user_domain.api.resource.authentication;

/**
 * This is an output resource for the API port
 * It is used to forward a custom token to the API caller
 * @param authenticationToken token to forward
 * @param <T> custom token type
 */
public record AuthenticationResponse<T>(T authenticationToken) {}