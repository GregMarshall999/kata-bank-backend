package com.exalt_company.kata_bank_api.dto.fund;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Data transfer object for fund operations (deposit/withdraw)")
public class FundOpDto extends BaseFundDto {
    
    @Schema(description = "Amount to deposit or withdraw", 
            example = "100.0")
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private double balance;

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}