package com.exalt_company.kata_bank_api.service;

import com.exalt_company.kata_bank_api.dto.statement.AccountStatementDto;
import com.exalt_company.kata_bank_api.entity.BankUser;
import com.exalt_company.kata_bank_api.entity.Fund;
import com.exalt_company.kata_bank_api.entity.Saving;
import com.exalt_company.kata_bank_api.enums.AuditOperation;
import com.exalt_company.kata_bank_api.exception.AuditException;
import org.springframework.http.ResponseEntity;

/**
 * This interface is used to bind auditing implementation.
 * The goal is to track the operations on the banking app.
 */
public interface IAuditService {
    void recordAudit(
            AuditOperation operation, double amount, double balanceBefore, double balanceAfter, BankUser requestingUser,
            Fund userFund, Saving userSaving
    );

    ResponseEntity<AccountStatementDto> requestStatement(String accountType, long ownerId, int page, int size) throws AuditException;
}
