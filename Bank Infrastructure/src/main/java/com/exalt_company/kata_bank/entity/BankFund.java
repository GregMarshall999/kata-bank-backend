package com.exalt_company.kata_bank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

import java.math.BigDecimal;

/**
 * JPA entity representing a bank fund account.
 * Each fund is associated with a single bank user (owner) and maintains a balance.
 */
@Entity
public class BankFund extends BaseEntity {
    @Column(precision = 19, scale = 2)
    private BigDecimal balance;

    @OneToOne(optional = false)
    private BankUser owner;

    public BankFund() {}

    public BankFund(BankUser owner) {
        this.owner = owner;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BankUser getOwner() {
        return owner;
    }

    public void setOwner(BankUser owner) {
        this.owner = owner;
    }
}
