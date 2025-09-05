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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This is the model layer.
 * We represent the database structure through our entities.
 * The markers will help spring with the background bootstrapping.
 * <p>
 * In an effort to showcase D.R.Y concepts, embedded fields are added.
 * An argument could be made for direct Advisor extension, since the only difference is the advisor field.
 * <p>
 * I personally prefer unidirectional relations to ease code complexity. (ManyToOne mostly)
 * <p>
 * We are using this entity for JWT authentication, so we have UserDetails interface to implement.
 */
@Entity
public class BankUser extends BaseEntity implements UserDetails {
    @Embedded
    private Identity identity;

    @Embedded
    private Credentials credentials;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BankRole bankRole;

    @ManyToOne
    private BankUser advisor;

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

    public BankUser getAdvisor() {
        return advisor;
    }

    public void setAdvisor(BankUser advisor) {
        this.advisor = advisor;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(bankRole == null) return new ArrayList<>();

        return List.of(new SimpleGrantedAuthority(bankRole.name()));
    }

    @Override
    public String getPassword() {
        if(credentials != null) return credentials.getPassword();

        return null;
    }

    @Override
    public String getUsername() {
        if(credentials != null) return credentials.getEmail();

        return null;
    }
}
