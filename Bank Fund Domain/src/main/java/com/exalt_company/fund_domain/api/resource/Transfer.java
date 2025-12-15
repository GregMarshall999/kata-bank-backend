package com.exalt_company.fund_domain.api.resource;

import java.math.BigDecimal;
import java.util.UUID;

public class Transfer extends FundResource {
    public Transfer(UUID fundId, UUID fundOwnerId, BigDecimal amount) {
        super(fundId, fundOwnerId, amount);
    }
}
