package com.exalt_company.kata_bank_api.dto;

import com.exalt_company.kata_bank_api.enums.BankRole;

public class AdvisorDto extends BaseDto {
    private String name;
    private String surname;
    private String email;
    private BankRole bankRole;

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
}
