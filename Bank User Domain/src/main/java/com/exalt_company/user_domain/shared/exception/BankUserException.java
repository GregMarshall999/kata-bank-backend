package com.exalt_company.user_domain.shared.exception;

/**
 * Exception thrown when a bank user account operation fails.
 * This exception is used for errors related to user account creation, retrieval, update, or deletion.
 */
public class BankUserException extends Exception {
    public BankUserException(String message) {
        super(message);
    }
}
