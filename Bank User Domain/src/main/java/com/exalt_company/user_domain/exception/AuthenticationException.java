package com.exalt_company.user_domain.exception;

/**
 * Used for Authentication errors
 */
public class AuthenticationException extends Exception {
    public AuthenticationException(String message) {
        super(message);
    }
}
