package com.exalt_company.user_domain.shared.exception;

/**
 * Exception thrown when an account communication operation fails.
 * This exception is used for errors related to message consultation, listing, or sending.
 */
public class AccountComunicationException extends Exception {
    public AccountComunicationException(String message) {
        super(message);
    }
}
