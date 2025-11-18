package com.exalt_company.kata_bank.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Entity
public class RefreshToken extends BaseEntity {
    @NotNull
    private String token;

    @NotNull
    private Instant expiryDate;

    @ManyToOne(optional = false)
    private BankUser user;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    public BankUser getUser() {
        return user;
    }

    public void setUser(BankUser user) {
        this.user = user;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiryDate);
    }
}

