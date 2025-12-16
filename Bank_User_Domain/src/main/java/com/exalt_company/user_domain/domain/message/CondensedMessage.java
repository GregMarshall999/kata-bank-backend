package com.exalt_company.user_domain.domain.message;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Immutable summary view of a user message, containing only essential details
 * for lightweight listing and retrieval operations.
 *
 * @param id unique identifier of the message
 * @param date local date when the message was created or scheduled
 * @param condensedContent short, human-readable preview of the message content
 * @param recipient recipient identifier (e.g., email or username)
 */
public record CondensedMessage(UUID id, LocalDate date, String condensedContent, String recipient) {}
