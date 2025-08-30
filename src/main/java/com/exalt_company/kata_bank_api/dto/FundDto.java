package com.exalt_company.kata_bank_api.dto;

public class FundDto extends BaseDto {
    private float balance;

    private long ownerId;

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }
}
