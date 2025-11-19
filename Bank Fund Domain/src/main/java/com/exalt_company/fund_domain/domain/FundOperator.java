package com.exalt_company.fund_domain.domain;

import com.exalt_company.fund_domain.api.FundAction;
import com.exalt_company.fund_domain.api.resource.Deposit;
import com.exalt_company.fund_domain.api.resource.FundResource;
import com.exalt_company.fund_domain.api.resource.Withdraw;
import com.exalt_company.fund_domain.ddd.FundDomainService;
import com.exalt_company.fund_domain.shared.FundException;
import com.exalt_company.fund_domain.shared.FundStatus;
import com.exalt_company.fund_domain.spi.Funds;

@FundDomainService
public class FundOperator implements FundAction {
    private final Funds funds;

    public FundOperator(Funds funds) {
        this.funds = funds;
    }

    @Override
    public FundStatus deposit(Deposit deposit) throws FundException {
        validateResource(deposit, "deposits");

        Fund ownerFund;
        try {
            ownerFund = funds.getByOwnerId(deposit.getFundOwnerId());
        } catch (FundException e) {
            ownerFund = funds.createFund(deposit.getFundOwnerId());
        }

        double balance = ownerFund.getBalance();
        ownerFund.setBalance(balance + deposit.getAmount());

        return funds.updateFund(deposit.getFundId(), ownerFund);
    }

    @Override
    public FundStatus withdraw(Withdraw withdraw) throws FundException {
        validateResource(withdraw, "withdraws");

        Fund ownerFund = funds.getByOwnerId(withdraw.getFundOwnerId());

        double balance = ownerFund.getBalance();
        balance = balance - withdraw.getAmount();

        if(balance < 0)
            throw new FundException("Balance overdrawn!", FundStatus.REFUSED);

        ownerFund.setBalance(balance);

        return funds.updateFund(withdraw.getFundId(), ownerFund);
    }

    private <R extends FundResource> void validateResource(R resource, String resourceType) throws FundException {
        if(resource.getFundId() == null)
            throw new FundException("Funds are required for " + resourceType + "!", FundStatus.UNSUPPORTED_OPERATION);
        if(resource.getFundOwnerId() == null)
            throw new FundException(
                    "The funds owner is required for " + resourceType + "!" , FundStatus.UNSUPPORTED_OPERATION
            );
        if(resource.getAmount() <= 0)
            throw new FundException(
                    resourceType.toUpperCase().charAt(0) +
                            resourceType.substring(1) +
                            " amounts must be positive!",
                    FundStatus.UNSUPPORTED_OPERATION
            );
    }
}
