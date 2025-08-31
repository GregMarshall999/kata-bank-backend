package com.exalt_company.kata_bank_api.dto.fund;

public class FundDto extends BaseFundDto {
    private double balance;

    private boolean canOverdraw;

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
