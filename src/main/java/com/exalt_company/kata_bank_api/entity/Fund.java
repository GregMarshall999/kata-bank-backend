package com.exalt_company.kata_bank_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class Fund extends BaseEntity {
    @Column(nullable = false)
    private double balance;

    private boolean canOverdraw;

    private double maxOverdraw; //Warning! This value is positive!

    @ManyToOne(optional = false)
    private BankUser owner;

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean canOverdraw() {
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

    public BankUser getOwner() {
        return owner;
    }

    public void setOwner(BankUser owner) {
        this.owner = owner;
    }
}
