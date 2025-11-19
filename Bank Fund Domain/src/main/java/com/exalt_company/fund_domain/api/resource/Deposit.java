package com.exalt_company.fund_domain.api.resource;

import java.util.UUID;

public class Deposit extends FundResource {
    public Deposit(UUID fundId, UUID fundOwnerId, double amount) {
        super(fundId, fundOwnerId, amount);
    }
}
