package com.exalt_company.kata_bank_api.dto;

public class FundDto extends BaseDto {
    private double balance;

    private long ownerId;

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }
}
