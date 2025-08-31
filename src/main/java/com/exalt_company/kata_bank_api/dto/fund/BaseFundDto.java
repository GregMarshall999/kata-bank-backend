package com.exalt_company.kata_bank_api.dto.fund;

import com.exalt_company.kata_bank_api.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;

public abstract class BaseFundDto extends BaseDto {
    
    @Schema(description = "ID of the fund owner (user)", 
            example = "1", 
            required = true)
    private long ownerId;

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }
}
