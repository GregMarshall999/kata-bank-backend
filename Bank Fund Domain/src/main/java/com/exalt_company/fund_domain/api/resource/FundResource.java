package com.exalt_company.fund_domain.api.resource;

import java.math.BigDecimal;
import java.util.UUID;

public abstract class FundResource {
    private final UUID fundId;
    private final UUID fundOwnerId;
    private final BigDecimal amount;

    protected FundResource(UUID fundId, UUID fundOwnerId, BigDecimal amount) {
        this.fundId = fundId;
        this.fundOwnerId = fundOwnerId;
        this.amount = amount;
    }

    public UUID getFundOwnerId() {
        return fundOwnerId;
    }

    public UUID getFundId() {
        return fundId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
