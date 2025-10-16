package com.exalt_company.user_domain.domain.message;

import java.time.LocalDate;
import java.util.UUID;

public record CondensedMessage(UUID id, LocalDate date, String condensedContent, String recipient) {}
