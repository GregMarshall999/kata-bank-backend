package com.exalt_company.fund_domain.api.resource;

import java.util.UUID;

public class Withdraw extends FundResource {
    public Withdraw(UUID fundId, UUID fundOwnerId, double amount) {
        super(fundId, fundOwnerId, amount);
    }
}
