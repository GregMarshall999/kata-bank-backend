package com.exalt_company.kata_bank.adapter.v1.resource;

import java.math.BigDecimal;
import java.util.UUID;

public record FundSendRequest(UUID senderId, UUID contactFundId, BigDecimal amount, String operationDescription) {}
