package com.exalt_company.kata_bank.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

@Entity
public class BankFund extends BaseEntity {
    private double balance;

    @OneToOne(optional = false)
    private BankUser owner;

    public BankFund() {}

    public BankFund(BankUser owner) {
        this.owner = owner;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public BankUser getOwner() {
        return owner;
    }

    public void setOwner(BankUser owner) {
        this.owner = owner;
    }
}
