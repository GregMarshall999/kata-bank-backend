package com.exalt_company.user_domain.domain.account;

import com.exalt_company.user_domain.api.resource.SignUpUser;
import com.exalt_company.user_domain.domain.shared.BankRole;

import java.util.UUID;

public class BankUserAccount {
    private UUID id;

    private String name;
    private String surname;
    private String email;

    private BankRole role;

    public BankUserAccount(String name, String surname, String email) {
        this.name = name;
        this.surname = surname;
        this.email = email;

        role = BankRole.CLIENT;
    }

    /**
     * Maps the name, surname and email of the API SignUpUser resource
     * @param user signup user
     * @return mapped BankUserAccount object
     */
    public static BankUserAccount fromSignUp(SignUpUser user) {
        return new BankUserAccount(user.name(), user.surname(), user.email());
    }

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
