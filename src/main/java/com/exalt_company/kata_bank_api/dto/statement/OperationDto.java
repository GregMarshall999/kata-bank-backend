package com.exalt_company.kata_bank_api.dto.statement;

import com.exalt_company.kata_bank_api.enums.AuditOperation;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Individual operation record in an account statement")
public class OperationDto {
    @Schema(description = "Type of operation performed", example = "DEPOSIT", allowableValues = {"CLOSE", "DEPOSIT", "OPEN", "OVERDRAW_CANCEL", "OVERDRAW_REQUEST", "WITHDRAW"})
    private AuditOperation operation;

    @Schema(description = "Username or identifier of the person who performed the operation", example = "john.doe")
    private String operationAuthor;

    private double operationAmount;

    private LocalDateTime operationDate;

    public AuditOperation getOperation() {
        return operation;
    }

    public void setOperation(AuditOperation operation) {
        this.operation = operation;
    }

    public String getOperationAuthor() {
        return operationAuthor;
    }

    public void setOperationAuthor(String operationAuthor) {
        this.operationAuthor = operationAuthor;
    }

    public double getOperationAmount() {
        return operationAmount;
    }

    public void setOperationAmount(double operationAmount) {
        this.operationAmount = operationAmount;
    }

    public LocalDateTime getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(LocalDateTime operationDate) {
        this.operationDate = operationDate;
    }
}
