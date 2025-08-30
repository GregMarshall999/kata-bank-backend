package com.exalt_company.kata_bank_api.exception;

/**
 * This is used for any errors we might encounter in the Base functionality.
 */
public class BaseException extends Exception {
    public BaseException(String message) {
        super(message);
    }
}
