package com.exalt_company.kata_bank_api.controller;

import com.exalt_company.kata_bank_api.dto.statement.AccountStatementDto;
import com.exalt_company.kata_bank_api.exception.AuditException;
import com.exalt_company.kata_bank_api.service.IAuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit-account")
@Tag(name = "Account Auditing", description = "APIs for retrieving account audit trails and transaction statements")
public class AccountAuditController {
    private final IAuditService service;

    @Autowired
    public AccountAuditController(IAuditService service) {
        this.service = service;
    }

    @Operation(
        summary = "Request account statement", 
        description = "Retrieves a paginated statement of all operations performed on a specific account. " +
                     "This includes deposits, withdrawals, overdraw operations, and account modifications. " +
                     "The response provides detailed audit information with pagination support."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Account statement retrieved successfully",
            content = @Content(schema = @Schema(implementation = AccountStatementDto.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid request parameters or account type",
            content = @Content(schema = @Schema(implementation = com.exalt_company.kata_bank_api.exception.ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized - Invalid or missing authentication token"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Account not found or owner does not exist"
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error during audit retrieval"
        )
    })
    @GetMapping("/{accountType}/{ownerId}/{page}/{size}")
    public ResponseEntity<AccountStatementDto> requestStatement(
            @Parameter(description = "Type of account (FUND or SAVING)", example = "FUND")
            @PathVariable String accountType, 
            @Parameter(description = "Unique identifier of the account owner", example = "123")
            @PathVariable long ownerId,
            @Parameter(description = "Page number for pagination (0-based)", example = "0")
            @PathVariable int page, 
            @Parameter(description = "Number of operations per page", example = "10")
            @PathVariable int size) throws AuditException {
        return service.requestStatement(accountType, ownerId, page, size);
    }
}
