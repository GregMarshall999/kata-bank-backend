package com.exalt_company.kata_bank_api.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Mainly demonstrating local supports
 * Add languages and translations later on
 */
@Schema(description = "Types of bank accounts supported by the system")
public enum AccountType {
    @Schema(description = "Fund account (current account) - supports deposits, withdrawals, and overdraw functionality")
    FUND("Funds", "Compte Courant"),
    
    @Schema(description = "Savings account - supports deposits and withdrawals with maximum balance limits")
    SAVING("Savings", "Compte Epargne");

    private final String en;
    private final String fr;

    AccountType(String en, String fr) {
        this.en = en;
        this.fr = fr;
    }

    public String getEn() {
        return en;
    }

    public String getFr() {
        return fr;
    }
}
