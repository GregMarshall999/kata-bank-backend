package com.exalt_company.kata_bank.adapter.v1.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard error response structure for API exceptions.
 * Provides consistent error information across all endpoints.
 */
@Schema(description = "Error response containing error details")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        @Schema(description = "HTTP status code", example = "400")
        int status,
        
        @Schema(description = "Error message", example = "Invalid request parameters")
        String message,
        
        @Schema(description = "Timestamp when the error occurred")
        LocalDateTime timestamp,
        
        @Schema(description = "Detailed error information", nullable = true)
        String details,
        
        @Schema(description = "List of validation errors", nullable = true)
        List<String> validationErrors
) {
    public ErrorResponse(int status, String message) {
        this(status, message, LocalDateTime.now(), null, null);
    }
    
    public ErrorResponse(int status, String message, String details) {
        this(status, message, LocalDateTime.now(), details, null);
    }
    
    public ErrorResponse(int status, String message, List<String> validationErrors) {
        this(status, message, LocalDateTime.now(), null, validationErrors);
    }
}

