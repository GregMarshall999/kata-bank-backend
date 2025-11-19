package com.exalt_company.kata_bank.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

/**
 * JPA entity representing a refresh token.
 * Refresh tokens are used to obtain new access tokens without requiring
 * the user to re-authenticate. Each token is associated with a user and has an expiration date.
 */
@Entity
public class RefreshToken extends BaseEntity {
    @NotNull
    private String token;

    //TODO: test with switch to LocalDate
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

