package com.exalt_company.fund_domain.api.resource;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Represents a withdrawal operation request.
 * A withdrawal removes funds from a bank account.
 */
public class Withdraw extends FundResource {
    public Withdraw(UUID fundId, UUID fundOwnerId, BigDecimal amount) {
        super(fundId, fundOwnerId, amount);
    }
}
