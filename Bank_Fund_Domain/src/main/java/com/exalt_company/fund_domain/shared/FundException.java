package com.exalt_company.fund_domain.shared;

/**
 * Exception thrown when a fund operation fails.
 * Contains both an error message and the status of the failed operation.
 */
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
