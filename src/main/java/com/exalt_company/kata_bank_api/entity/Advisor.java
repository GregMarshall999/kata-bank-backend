package com.exalt_company.kata_bank_api.entity;

import com.exalt_company.kata_bank_api.entity.user_fields.Credentials;
import com.exalt_company.kata_bank_api.entity.user_fields.Identity;
import com.exalt_company.kata_bank_api.enums.BankRole;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/**
 * This is the model layer.
 * We represent the database structure through our entities.
 * The markers will help spring with the background bootstrapping.
 */
@Entity
public class Advisor extends BaseEntity {
    @Embedded
    private Identity identity;

    @Embedded
    private Credentials credentials;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BankRole bankRole;

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public BankRole getBankRole() {
        return bankRole;
    }

    public void setBankRole(BankRole bankRole) {
        this.bankRole = bankRole;
    }
}
