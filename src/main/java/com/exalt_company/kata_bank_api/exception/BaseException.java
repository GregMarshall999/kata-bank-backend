package com.exalt_company.kata_bank_api.exception;

import org.springframework.http.HttpStatus;

/**
 * This is used for any errors we might encounter in the Base functionality.
 */
public class BaseException extends BankApiException {
    public BaseException(String message, HttpStatus status) {
        super(message, status);
    }
}
