package com.exalt_company.kata_bank_api.dto.fund;

public class FundOpDto extends BaseFundDto {
    private double balance;

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
