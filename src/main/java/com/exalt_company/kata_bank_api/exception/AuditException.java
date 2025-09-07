package com.exalt_company.kata_bank_api.exception;

import org.springframework.http.HttpStatus;

public class AuditException extends BankApiException {
    public AuditException(String message, HttpStatus status) {
        super(message, status);
    }
}
