package com.exalt_company.kata_bank.entity;

import com.exalt_company.user_domain.shared.BankRole;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

@Entity
public class BankUser extends BaseEntity {
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
    @Enumerated
    private BankRole role;

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

    public String getPassword() {
        return password;
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
}
