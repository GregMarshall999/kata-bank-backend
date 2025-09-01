package com.exalt_company.kata_bank_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data Transfer Object for savings account operations")
public class SavingDto extends BaseDto {
    
    @Schema(description = "Current balance in the savings account", 
            example = "1000.50", 
            minimum = "0")
    private double balance;
    
    @Schema(description = "Maximum allowed balance for the savings account. Deposits cannot exceed this limit.", 
            example = "10000.00", 
            minimum = "0")
    private double maxBalance;
    
    @Schema(description = "ID of the user who owns this savings account", 
            example = "1")
    private long ownerId;

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getMaxBalance() {
        return maxBalance;
    }

    public void setMaxBalance(double maxBalance) {
        this.maxBalance = maxBalance;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }
}
