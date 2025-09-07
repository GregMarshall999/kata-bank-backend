package com.exalt_company.kata_bank_api.exception;

import org.springframework.http.HttpStatus;

/**
 * This will be used to regroup all the exceptions in this project
 * The goal is to contain the HttpStatus that will be given on every error
 */
public class BankApiException extends Exception {
    private final HttpStatus status;

    public BankApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
