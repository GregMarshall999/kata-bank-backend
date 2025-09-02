package com.exalt_company.kata_bank_api.dto.statement;

import com.exalt_company.kata_bank_api.dto.BaseDto;
import com.exalt_company.kata_bank_api.enums.AccountType;

import java.util.List;

public class AccountStatementDto extends BaseDto {
    private AccountType accountType;

    private double accountBalance;

    private List<OperationDto> operations;

    private int operationsPage;

    private int operationsSize;

    private int totalOperationsPage;

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public List<OperationDto> getOperations() {
        return operations;
    }

    public void setOperations(List<OperationDto> operations) {
        this.operations = operations;
    }

    public int getOperationsPage() {
        return operationsPage;
    }

    public void setOperationsPage(int operationsPage) {
        this.operationsPage = operationsPage;
    }

    public int getOperationsSize() {
        return operationsSize;
    }

    public void setOperationsSize(int operationsSize) {
        this.operationsSize = operationsSize;
    }

    public int getTotalOperationsPage() {
        return totalOperationsPage;
    }

    public void setTotalOperationsPage(int totalOperationsPage) {
        this.totalOperationsPage = totalOperationsPage;
    }
}
