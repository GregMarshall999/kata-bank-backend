package com.exalt_company.kata_bank_api.dto.fund;

import com.exalt_company.kata_bank_api.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Base class for all fund-related Data Transfer Objects.
 * Provides common fields and functionality for fund operations.
 */
@Schema(description = "Base DTO for fund-related operations")
public abstract class BaseFundDto extends BaseDto {
    
    @Schema(description = "ID of the fund owner (user)", 
            example = "1")
    @NotNull(message = "Owner ID is required")
    @Positive(message = "Owner ID must be a positive number")
    private long ownerId;

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }
}
