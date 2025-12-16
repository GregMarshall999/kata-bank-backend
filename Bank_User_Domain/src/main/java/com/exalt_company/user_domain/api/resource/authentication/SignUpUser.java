package com.exalt_company.user_domain.api.resource.authentication;

/**
 * Simple API resource used for new user generation
 * @param name
 * @param surname
 * @param email
 * @param password
 */
public record SignUpUser(String name, String surname, String email, String password) {}
