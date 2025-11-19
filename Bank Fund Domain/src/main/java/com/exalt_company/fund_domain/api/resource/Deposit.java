package com.exalt_company.fund_domain.api.resource;

import java.math.BigDecimal;
import java.util.UUID;

public class Deposit extends FundResource {
    public Deposit(UUID fundId, UUID fundOwnerId, BigDecimal amount) {
        super(fundId, fundOwnerId, amount);
    }
}
