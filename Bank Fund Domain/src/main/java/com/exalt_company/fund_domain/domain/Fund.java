package com.exalt_company.fund_domain.domain;

import java.util.UUID;

public class Fund {
    private UUID id;
    private double balance;
    private UUID ownerId;

    public Fund() {
    }

    public Fund(UUID id, UUID ownerId) {
        this.id = id;
        this.ownerId = ownerId;
    }

    public Fund(UUID id, double balance, UUID ownerId) {
        this(id, ownerId);
        this.balance = balance;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }
}
