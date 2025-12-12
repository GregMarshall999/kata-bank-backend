package com.exalt_company.fund_domain.domain;

import com.exalt_company.fund_domain.api.FundAction;
import com.exalt_company.fund_domain.api.resource.Deposit;
import com.exalt_company.fund_domain.api.resource.FundResource;
import com.exalt_company.fund_domain.api.resource.FundResponse;
import com.exalt_company.fund_domain.api.resource.Withdraw;
import com.exalt_company.fund_domain.ddd.FundDomainService;
import com.exalt_company.fund_domain.shared.FundException;
import com.exalt_company.fund_domain.shared.FundStatus;
import com.exalt_company.fund_domain.spi.Funds;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Domain service implementation for fund operations.
 * Handles business logic for deposits and withdrawals, including validation
 * and fund creation when necessary.
 */
@FundDomainService
public class FundOperator implements FundAction {
    private final Funds funds;

    /**
     * Constructs a new FundOperator with the specified Funds repository.
     *
     * @param funds the repository for fund persistence operations
     */
    public FundOperator(Funds funds) {
        this.funds = funds;
    }

    @Override
    public FundStatus deposit(Deposit deposit) throws FundException {
        validateResource(deposit, "deposits");

        Fund ownerFund;
        boolean wasCreated = false;
        try {
            ownerFund = funds.getByOwnerId(deposit.getFundOwnerId());
        } catch (FundException e) {
            ownerFund = funds.createFund(deposit.getFundOwnerId());
            wasCreated = true;
        }

        BigDecimal balance = ownerFund.getBalance();
        if (balance == null) {
            balance = BigDecimal.ZERO;
        }
        ownerFund.setBalance(balance.add(deposit.getAmount()));
        FundStatus status = funds.updateFund(wasCreated ? ownerFund.getId() : deposit.getFundId(), ownerFund);

        return wasCreated ? FundStatus.CREATED : status;
    }

    @Override
    public FundStatus withdraw(Withdraw withdraw) throws FundException {
        validateResource(withdraw, "withdraws");

        Fund ownerFund = funds.getByOwnerId(withdraw.getFundOwnerId());

        if(!ownerFund.getId().equals(withdraw.getFundId()))
            throw new FundException("Owner fund mismatch!", FundStatus.REFUSED);

        BigDecimal balance = ownerFund.getBalance();
        if (balance == null) {
            balance = BigDecimal.ZERO;
        }
        balance = balance.subtract(withdraw.getAmount());

        if(balance.compareTo(BigDecimal.ZERO) < 0)
            throw new FundException("Balance overdrawn!", FundStatus.REFUSED);

        ownerFund.setBalance(balance);

        return funds.updateFund(withdraw.getFundId(), ownerFund);
    }

    @Override
    public FundResponse getByOwnerId(UUID ownerId) throws FundException {
        Fund ownerFund = funds.getByOwnerId(ownerId);

        return new FundResponse(ownerFund.getId(), ownerFund.getBalance());
    }

    @Override
    public void createUserFund(UUID ownerId) throws FundException {
        funds.createFund(ownerId);
    }

    private <R extends FundResource> void validateResource(R resource, String resourceType) throws FundException {
        if(!(resource instanceof Deposit) && resource.getFundId() == null)
            throw new FundException("Funds are required for " + resourceType + "!", FundStatus.UNSUPPORTED_OPERATION);
        if(resource.getFundOwnerId() == null)
            throw new FundException(
                    "The funds owner is required for " + resourceType + "!" , FundStatus.UNSUPPORTED_OPERATION
            );
        if(resource.getAmount() == null || resource.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new FundException(
                    resourceType.toUpperCase().charAt(0) +
                            resourceType.substring(1) +
                            " amounts must be positive!",
                    FundStatus.UNSUPPORTED_OPERATION
            );
    }
}
