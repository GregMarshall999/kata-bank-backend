package com.exalt_company.user_domain.domain.account;

import java.util.UUID;

public class BankUserAccount {
    private UUID id = UUID.randomUUID();

    private String name;
    private String surname;
    private String email;

    private BankRole role;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public BankRole getRole() {
        return role;
    }

    public void setRole(BankRole role) {
        this.role = role;
    }
}
