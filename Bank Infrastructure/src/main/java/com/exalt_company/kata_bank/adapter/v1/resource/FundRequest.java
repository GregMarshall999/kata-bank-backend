package com.exalt_company.kata_bank.adapter.v1.resource;

import java.util.UUID;

public record FundRequest(UUID fundOwnerId, UUID fundId, double amount) {}
