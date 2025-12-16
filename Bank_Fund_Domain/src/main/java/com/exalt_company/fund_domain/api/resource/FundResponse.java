package com.exalt_company.fund_domain.api.resource;

import java.math.BigDecimal;
import java.util.UUID;

public record FundResponse(UUID id, BigDecimal balance) {
}
