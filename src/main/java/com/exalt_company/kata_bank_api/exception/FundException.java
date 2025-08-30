package com.exalt_company.kata_bank_api.exception;

import com.exalt_company.kata_bank_api.enums.Banking;

public class FundException extends Exception {
    private final Banking banking;

    public FundException(String message, Banking banking) {
        super(message);
        this.banking = banking;
    }

    public Banking getBanking() {
        return banking;
    }
}
