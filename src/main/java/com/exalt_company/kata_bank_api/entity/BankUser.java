package com.exalt_company.kata_bank_api.entity;

import com.exalt_company.kata_bank_api.entity.user_fields.Credentials;
import com.exalt_company.kata_bank_api.entity.user_fields.Identity;
import com.exalt_company.kata_bank_api.enums.BankRole;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;

/**
 * In an effort to showcase D.R.Y concepts, embedded fields are added.
 * An argument could be made for direct Advisor extension, since the only difference is the advisor field.
 * <p>
 * I personally prefer unidirectional relations to ease code complexity. (ManyToOne mostly)
 */
@Entity
public class BankUser extends BaseEntity {
    @Embedded
    private Identity identity;

    @Embedded
    private Credentials credentials;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BankRole bankRole;

    @ManyToOne
    private Advisor advisor;

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

    public Advisor getAdvisor() {
        return advisor;
    }

    public void setAdvisor(Advisor advisor) {
        this.advisor = advisor;
    }
}
