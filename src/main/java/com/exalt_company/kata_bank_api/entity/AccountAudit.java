package com.exalt_company.kata_bank_api.entity;

import com.exalt_company.kata_bank_api.enums.AuditOperation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToOne;

@Entity
public class AccountAudit extends BaseEntity {
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuditOperation operation;

    private double amount;

    private double balanceBefore;

    private double balanceAfter;

    @OneToOne
    private BankUser requestingUser;

    @OneToOne
    private Fund userFund;

    @OneToOne
    private Saving userSaving;

    public AuditOperation getOperation() {
        return operation;
    }

    public void setOperation(AuditOperation operation) {
        this.operation = operation;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getBalanceBefore() {
        return balanceBefore;
    }

    public void setBalanceBefore(double balanceBefore) {
        this.balanceBefore = balanceBefore;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(double balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public BankUser getRequestingUser() {
        return requestingUser;
    }

    public void setRequestingUser(BankUser requestingUser) {
        this.requestingUser = requestingUser;
    }

    public Fund getUserFund() {
        return userFund;
    }

    public void setUserFund(Fund userFund) {
        this.userFund = userFund;
    }

    public Saving getUserSaving() {
        return userSaving;
    }

    public void setUserSaving(Saving userSaving) {
        this.userSaving = userSaving;
    }
}
