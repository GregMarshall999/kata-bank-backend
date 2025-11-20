package com.exalt_company.fund_domain.domain;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Represents a fund account in the banking system.
 * A fund is associated with an owner and maintains a balance.
 */
public class Fund {
    private UUID id;
    private BigDecimal balance;
    private UUID ownerId;

    public Fund() {
    }

    public Fund(UUID id, UUID ownerId) {
        this.id = id;
        this.ownerId = ownerId;
    }

    public Fund(UUID id, BigDecimal balance, UUID ownerId) {
        this(id, ownerId);
        this.balance = balance;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }
}
