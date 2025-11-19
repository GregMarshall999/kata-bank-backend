package com.exalt_company.fund_domain.api.resource;

import java.util.UUID;

public class Withdraw extends FundResource {
    public Withdraw(UUID fundOwnerId, UUID fundId, double amount) {
        super(fundOwnerId, fundId, amount);
    }
}
