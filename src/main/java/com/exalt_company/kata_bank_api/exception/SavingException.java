package com.exalt_company.kata_bank_api.exception;

import org.springframework.http.HttpStatus;

public class SavingException extends BankApiException {
    public SavingException(String message, HttpStatus status) {
        super(message, status);
    }
}
