package com.exalt_company.kata_bank_api.dto.fund;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Data Transfer Object for fund account information")
public class FundDto extends BaseFundDto {
    @Schema(description = "Current balance in the fund account", 
            example = "1500.75")
    @DecimalMin(value = "0.0", message = "Balance must be non-negative")
    private double balance;

    @Schema(description = "Whether the fund account allows overdrawing", 
            example = "true")
    @NotNull(message = "Can overdraw flag is required")
    private boolean canOverdraw;

    @Schema(description = "Maximum overdraw amount allowed for this fund account", 
            example = "500.0", 
            minimum = "0.0")
    @DecimalMin(value = "0.0", message = "Maximum overdraw must be non-negative")
    private double maxOverdraw;

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean isCanOverdraw() {
        return canOverdraw;
    }

    public void setCanOverdraw(boolean canOverdraw) {
        this.canOverdraw = canOverdraw;
    }

    public double getMaxOverdraw() {
        return maxOverdraw;
    }

    public void setMaxOverdraw(double maxOverdraw) {
        this.maxOverdraw = maxOverdraw;
    }
}
