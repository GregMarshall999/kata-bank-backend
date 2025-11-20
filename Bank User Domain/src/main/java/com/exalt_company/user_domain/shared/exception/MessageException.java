package com.exalt_company.user_domain.shared.exception;

/**
 * Exception thrown when a message operation fails.
 * This exception is used for errors related to message retrieval, sending, or pagination.
 */
public class MessageException extends Exception {
    public MessageException(String message) {
        super(message);
    }
}
