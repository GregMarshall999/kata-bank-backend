package com.exalt_company.fund_domain.api.resource;

import java.util.UUID;

public class Deposit extends FundResource {
    public Deposit(UUID fundOwnerId, UUID fundId, double amount) {
        super(fundOwnerId, fundId, amount);
    }
}
