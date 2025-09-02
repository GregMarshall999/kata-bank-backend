package com.exalt_company.kata_bank_api.controller;

import com.exalt_company.kata_bank_api.dto.statement.AccountStatementDto;
import com.exalt_company.kata_bank_api.exception.AuditException;
import com.exalt_company.kata_bank_api.service.IAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit-account")
public class AccountAuditController {
    private final IAuditService service;

    @Autowired
    public AccountAuditController(IAuditService service) {
        this.service = service;
    }

    @GetMapping("/{accountType}/{ownerId}/{page}/{size}")
    public ResponseEntity<AccountStatementDto> requestStatement(
            @PathVariable String accountType, @PathVariable long ownerId,
            @PathVariable int page, @PathVariable int size) throws AuditException {
        return service.requestStatement(accountType, ownerId, page, size);
    }
}
