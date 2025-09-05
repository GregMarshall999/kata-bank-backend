package com.exalt_company.kata_bank_api.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Banking operation status responses")
public enum Banking {
    @Schema(description = "Operation authorized (used for overdraw requests)")
    AUTHORIZED,
    
    @Schema(description = "Operation completed successfully (used for overdraw cancellation)")
    COMPLETED,
    
    @Schema(description = "Funds deposited successfully")
    DEPOSITED,
    
    @Schema(description = "Operation refused")
    REFUSED,
    
    @Schema(description = "Funds withdrawn successfully")
    WITHDREW
}
