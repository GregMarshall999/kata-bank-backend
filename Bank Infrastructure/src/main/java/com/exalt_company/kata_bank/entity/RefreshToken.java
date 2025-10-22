package com.exalt_company.kata_bank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
public class RefreshToken extends BaseEntity {
    @NotNull
    @Column(unique = true)
    private String token;
    
    @NotNull
    private LocalDate expiryDate;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private BankUser user;
    
    @NotNull
    private boolean revoked = false;

    public RefreshToken() {}
    
    public RefreshToken(String token, LocalDate expiryDate, BankUser user) {
        this.token = token;
        this.expiryDate = expiryDate;
        this.user = user;
    }

    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public LocalDate getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public BankUser getUser() {
        return user;
    }
    
    public void setUser(BankUser user) {
        this.user = user;
    }
    
    public boolean isRevoked() {
        return revoked;
    }
    
    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }
    
    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }
}
