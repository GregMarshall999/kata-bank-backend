package com.exalt_company.kata_bank_api.enums;

/**
 * Mainly demonstrating local supports
 * Add languages and translations later on
 */
public enum AccountType {
    FUND("Funds", "Compte Courant"),
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
