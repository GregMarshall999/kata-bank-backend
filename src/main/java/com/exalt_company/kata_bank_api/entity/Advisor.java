package com.exalt_company.kata_bank_api.entity;

import com.exalt_company.kata_bank_api.entity.user_fields.Credentials;
import com.exalt_company.kata_bank_api.entity.user_fields.Identity;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;

@Entity
public class Advisor extends BaseEntity {
    @Embedded
    private Identity identity;

    @Embedded
    private Credentials credentials;

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
}
