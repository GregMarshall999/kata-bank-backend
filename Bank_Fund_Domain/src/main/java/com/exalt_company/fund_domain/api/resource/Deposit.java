package com.exalt_company.fund_domain.api.resource;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Represents a deposit operation request.
 * A deposit adds funds to a bank account.
 */
public class Deposit extends FundResource {
    public Deposit(UUID fundId, UUID fundOwnerId, BigDecimal amount) {
        super(fundId, fundOwnerId, amount);
    }
}
