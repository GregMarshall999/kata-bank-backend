package com.exalt_company.kata_bank.adapter.v1.resource;

import java.util.UUID;

public record ContactRequest(UUID requesterId, UUID contactId, String contactName) {}
