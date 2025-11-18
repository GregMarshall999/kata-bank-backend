package com.exalt_company.user_domain.domain.account;

import com.exalt_company.user_domain.api.resource.authentication.SignUpUser;
import com.exalt_company.user_domain.shared.BankRole;

import java.util.UUID;

/**
 * Represents a bank user account in the domain model.
 * This class encapsulates all the information related to a bank user,
 * including their personal details and role within the system.
 */
public class BankUserAccount {
    private UUID id;

    private String name;
    private String surname;
    private String email;
    private String password;

    private BankRole role;

    /**
     * Constructs a new BankUserAccount with the specified personal details.
     * The user role is automatically set to CLIENT.
     *
     * @param name the first name of the user
     * @param surname the last name of the user
     * @param email the email address of the user
     * @param password the password for the user account
     */
    public BankUserAccount(String name, String surname, String email, String password) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;

        role = BankRole.CLIENT;
    }

    /**
     * Constructor used for the static copy method
     * @param id
     * @param name
     * @param surname
     * @param email
     * @param password
     * @param role
     */
    private BankUserAccount(UUID id, String name, String surname, String email, String password, BankRole role) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    /**
     * This is used for new instancing when found in collections.
     * @param copy found account to copy
     */
    public static BankUserAccount copy(BankUserAccount copy) {
        return new BankUserAccount(copy.id, copy.name, copy.surname, copy.email, copy.password, copy.role);
    }

    /**
     * Maps the name, surname and email of the API SignUpUser resource
     * @param user signup user
     * @return mapped BankUserAccount object
     */
    public static BankUserAccount fromSignUp(SignUpUser user) {
        return new BankUserAccount(user.name(), user.surname(), user.email(), user.password());
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
