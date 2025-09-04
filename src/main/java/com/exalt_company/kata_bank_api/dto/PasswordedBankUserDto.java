package com.exalt_company.kata_bank_api.dto;

/**
 * This dto is used for administrative made users
 * The goal is to send a temporary generated password
 * Eventually an email service with a direct account confirmation can be put in place for better security
 */
public class PasswordedBankUserDto extends BankUserDto {
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
