package com.exalt_company.fund_domain.api.resource;

import java.math.BigDecimal;
import java.util.UUID;

public class Withdraw extends FundResource {
    public Withdraw(UUID fundId, UUID fundOwnerId, BigDecimal amount) {
        super(fundId, fundOwnerId, amount);
    }
}
