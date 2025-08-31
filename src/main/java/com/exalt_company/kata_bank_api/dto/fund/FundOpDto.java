package com.exalt_company.kata_bank_api.dto.fund;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data transfer object for fund operations (deposit/withdraw)")
public class FundOpDto extends BaseFundDto {
    
    @Schema(description = "Amount to deposit or withdraw", 
            example = "100.0", 
            required = true)
    private double balance;

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
