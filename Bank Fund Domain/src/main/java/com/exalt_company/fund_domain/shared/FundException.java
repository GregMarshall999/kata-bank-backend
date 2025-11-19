package com.exalt_company.fund_domain.shared;

public class FundException extends Exception {
    private final FundStatus fundStatus;

    public FundException(String message, FundStatus status) {
        super(message);
        fundStatus = status;
    }

    public FundStatus getFundStatus() {
        return fundStatus;
    }
}
