package com.exalt_company.kata_bank_api.dto.statement;

import com.exalt_company.kata_bank_api.dto.BaseDto;
import com.exalt_company.kata_bank_api.enums.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Account statement containing balance and paginated operation history")
public class AccountStatementDto extends BaseDto {
    @Schema(description = "Type of the account (FUND or SAVING)", example = "FUND")
    private AccountType accountType;

    @Schema(description = "Current balance of the account", example = "1500.75")
    private double accountBalance;

    @Schema(description = "List of operations for the current page")
    private List<OperationDto> operations;

    @Schema(description = "Current page number (0-based)", example = "0")
    private int operationsPage;

    @Schema(description = "Number of operations per page", example = "10")
    private int operationsSize;

    @Schema(description = "Total number of pages available", example = "5")
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
