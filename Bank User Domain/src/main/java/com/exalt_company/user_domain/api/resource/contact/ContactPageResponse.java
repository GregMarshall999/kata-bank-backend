package com.exalt_company.user_domain.api.resource.contact;

import java.util.List;

public record ContactPageResponse(List<String> emails, int page, int totalPages, long totalElements) {}
