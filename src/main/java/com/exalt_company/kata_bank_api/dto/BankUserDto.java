package com.exalt_company.kata_bank_api.dto;

import com.exalt_company.kata_bank_api.enums.BankRole;

/**
 * This represents the data interface of frontend apps calling our API.
 * I prefer to link entities with their ID for frontend sub requests for general use.
 * Later specific cases can have custom mapping of the required fields.
 */
public class BankUserDto extends BaseDto {
    private String name;
    private String surname;
    private String email;
    private BankRole bankRole;

    private long advisorId;

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

    public BankRole getBankRole() {
        return bankRole;
    }

    public void setBankRole(BankRole bankRole) {
        this.bankRole = bankRole;
    }

    public long getAdvisorId() {
        return advisorId;
    }

    public void setAdvisorId(long advisorId) {
        this.advisorId = advisorId;
    }
}
