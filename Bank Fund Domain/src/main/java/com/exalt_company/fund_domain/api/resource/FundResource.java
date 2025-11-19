package com.exalt_company.fund_domain.api.resource;

import java.util.UUID;

public abstract class FundResource {
    private final UUID fundOwnerId;
    private final UUID fundId;
    private final double amount;

    protected FundResource(UUID fundOwnerId, UUID fundId, double amount) {
        this.fundOwnerId = fundOwnerId;
        this.fundId = fundId;
        this.amount = amount;
    }

    public UUID getFundOwnerId() {
        return fundOwnerId;
    }

    public UUID getFundId() {
        return fundId;
    }

    public double getAmount() {
        return amount;
    }
}
