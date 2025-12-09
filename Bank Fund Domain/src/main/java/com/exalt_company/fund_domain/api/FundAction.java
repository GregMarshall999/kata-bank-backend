package com.exalt_company.fund_domain.api;

import com.exalt_company.fund_domain.api.resource.Deposit;
import com.exalt_company.fund_domain.api.resource.FundResponse;
import com.exalt_company.fund_domain.api.resource.Withdraw;
import com.exalt_company.fund_domain.shared.FundException;
import com.exalt_company.fund_domain.shared.FundStatus;

import java.util.UUID;

/**
 * Interface for performing fund operations such as deposits and withdrawals.
 */
public interface FundAction {
    /**
     * Deposits funds into an account.
     *
     * @param deposit the deposit request containing fund ID, owner ID, and amount
     * @return the status of the deposit operation
     * @throws FundException if the deposit operation fails
     */
    FundStatus deposit(Deposit deposit) throws FundException;

    /**
     * Withdraws funds from an account.
     *
     * @param withdraw the withdrawal request containing fund ID, owner ID, and amount
     * @return the status of the withdrawal operation
     * @throws FundException if the withdrawal operation fails (e.g., insufficient funds)
     */
    FundStatus withdraw(Withdraw withdraw) throws FundException;

    FundResponse getByOwnerId(UUID ownerId) throws FundException;

    void createUserFund(UUID ownerId) throws FundException;
}
