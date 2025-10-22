package com.exalt_company.user_domain.api.resource.authentication;

/**
 * Simple API port resource to use email and password for login purposes
 * @param email
 * @param password
 */
public record SignInUser(String email, String password) {}
