package com.exalt_company.kata_bank_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * This dto is used for administrative made users
 * The goal is to send a temporary generated password
 * Eventually an email service with a direct account confirmation can be put in place for better security
 */
@Schema(description = "Data Transfer Object for bank user with password information (used for administrative user creation)")
public class PasswordedBankUserDto extends BankUserDto {
    @Schema(description = "Temporary password for the newly created user", 
            example = "TempPass123!")
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
