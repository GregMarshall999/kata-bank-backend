package com.exalt_company.kata_bank_api.controller;

import com.exalt_company.kata_bank_api.dto.SavingDto;
import com.exalt_company.kata_bank_api.enums.Banking;
import com.exalt_company.kata_bank_api.exception.FundException;
import com.exalt_company.kata_bank_api.exception.SavingException;
import com.exalt_company.kata_bank_api.service.ISavingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/saving")
@Tag(name = "Savings", description = "Savings account management APIs for opening, closing, depositing, and withdrawing from savings accounts")
public class SavingController extends BaseController<SavingDto, ISavingService> {
    @Autowired
    public SavingController(ISavingService service) {
        super(service);
    }

    @Operation(summary = "Open savings account", 
               description = "Creates a new savings account for a user with a specified maximum balance limit. " +
                           "The account starts with a balance of 0 and can accept deposits up to the maximum balance limit. " +
                           "Users cannot open multiple savings accounts with the same ID. All operations are automatically audited and can be retrieved via the audit API.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Savings account created successfully",
                    content = @Content(schema = @Schema(implementation = Banking.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data, negative max balance, or account already exists",
                    content = @Content(schema = @Schema(implementation = com.exalt_company.kata_bank_api.exception.ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @PostMapping("/open")
    public ResponseEntity<Banking> openSavingsAccount(
            @Valid @RequestBody SavingDto dto) throws SavingException {
        return service.openSavingsAccount(dto);
    }

    @Operation(summary = "Close savings account", 
               description = "Closes an existing savings account. This operation is only allowed when the account balance is zero. " +
                           "If the account has a positive balance, all funds must be withdrawn before the account can be closed. " +
                           "Once closed, the account is permanently deleted from the system. All operations are automatically audited and can be retrieved via the audit API.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Savings account closed successfully",
                    content = @Content(schema = @Schema(implementation = Banking.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data, account not found, or non-zero balance",
                    content = @Content(schema = @Schema(implementation = com.exalt_company.kata_bank_api.exception.ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @PostMapping("/close")
    public ResponseEntity<Banking> closeSavingsAccount(
            @Valid @RequestBody SavingDto dto) throws SavingException {
        return service.closeSavingsAccount(dto);
    }

    @Operation(summary = "Deposit to savings account", 
               description = "Deposits money into an existing savings account. The deposit amount is added to the current balance. " +
                           "The total balance after deposit cannot exceed the maximum balance limit set for the account. " +
                           "Deposits with zero or negative amounts are not allowed. All operations are automatically audited and can be retrieved via the audit API.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deposit successful",
                    content = @Content(schema = @Schema(implementation = Banking.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data, account not found, negative amount, or maximum balance exceeded",
                    content = @Content(schema = @Schema(implementation = com.exalt_company.kata_bank_api.exception.ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @PostMapping("/deposit")
    public ResponseEntity<Banking> deposit(
            @Valid @RequestBody SavingDto dto) throws FundException, SavingException {
        return service.deposit(dto);
    }

    @Operation(summary = "Withdraw from savings account", 
               description = "Withdraws money from an existing savings account. The withdrawal amount is subtracted from the current balance. " +
                           "Withdrawals cannot result in a negative balance. The account balance must be sufficient to cover the withdrawal amount. " +
                           "Withdrawals with zero or negative amounts are not allowed. All operations are automatically audited and can be retrieved via the audit API.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Withdrawal successful",
                    content = @Content(schema = @Schema(implementation = Banking.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data, account not found, negative amount, or insufficient balance",
                    content = @Content(schema = @Schema(implementation = com.exalt_company.kata_bank_api.exception.ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @PostMapping("/withdraw")
    public ResponseEntity<Banking> withdraw(
            @Valid @RequestBody SavingDto dto) throws SavingException {
        return service.withdraw(dto);
    }
}
