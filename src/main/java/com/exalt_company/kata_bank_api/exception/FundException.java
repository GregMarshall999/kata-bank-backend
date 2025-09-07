package com.exalt_company.kata_bank_api.exception;

import org.springframework.http.HttpStatus;

public class FundException extends BankApiException {
    public FundException(String message, HttpStatus status) {
        super(message, status);
    }
}
