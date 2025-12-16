package com.exalt_company.user_domain.shared.exception;

/**
 * Exception thrown when an authentication operation fails.
 * This exception is used for errors related to user sign-in, sign-up, or token generation.
 */
public class AuthenticationException extends Exception {
    public AuthenticationException(String message) {
        super(message);
    }
}
