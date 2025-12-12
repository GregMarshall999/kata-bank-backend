package com.exalt_company.contact_domain.api.resource;

import java.util.UUID;

public record OperationRequest(UUID requester, UUID contact, String name) {}
