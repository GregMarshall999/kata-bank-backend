package com.exalt_company.kata_bank_api.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Types of operations that can be performed on bank accounts")
public enum AuditOperation {
    @Schema(description = "Close an account")
    CLOSE,
    
    @Schema(description = "Deposit money into an account")
    DEPOSIT,
    
    @Schema(description = "Open a new account")
    OPEN,
    
    @Schema(description = "Cancel overdraw capabilities on an account")
    OVERDRAW_CANCEL,
    
    @Schema(description = "Request overdraw capabilities on an account")
    OVERDRAW_REQUEST,
    
    @Schema(description = "Withdraw money from an account")
    WITHDRAW
}
