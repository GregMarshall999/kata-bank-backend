package com.exalt_company.kata_bank_api.dto.statement;

import com.exalt_company.kata_bank_api.dto.BaseDto;
import com.exalt_company.kata_bank_api.enums.AuditOperation;

public class OperationDto extends BaseDto {
    private AuditOperation operation;

    //TODO: private double amount;

    private String operationAuthor;

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
}
