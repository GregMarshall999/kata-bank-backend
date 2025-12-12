package com.exalt_company.kata_bank.entity;

import com.exalt_company.user_domain.shared.BankRole;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * JPA entity representing a bank user account.
 * Implements Spring Security's UserDetails interface for authentication and authorization.
 * Each user has a role (ADMIN, CLIENT, or COUNSELOR) that determines their permissions.
 */
@Entity
public class BankUser extends BaseEntity implements UserDetails {
    @NotNull
    private String name;

    @NotNull
    private String surname;

    @Email
    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    private BankRole role;

    @OneToMany
    private List<BankContact> contacts;

    public BankUser() {}

    public BankUser(UUID id, String name, String surname, String email, String password, BankRole role) {
        super(id);

        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public BankRole getRole() {
        return role;
    }

    public void setRole(BankRole role) {
        this.role = role;
    }

    public List<BankContact> getContacts() {
        return contacts;
    }

    public void setContacts(List<BankContact> contacts) {
        this.contacts = contacts;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
