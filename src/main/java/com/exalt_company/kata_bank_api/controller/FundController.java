package com.exalt_company.kata_bank_api.controller;

import com.exalt_company.kata_bank_api.dto.fund.FundDto;
import com.exalt_company.kata_bank_api.dto.fund.FundOpDto;
import com.exalt_company.kata_bank_api.dto.fund.OverdrawDto;
import com.exalt_company.kata_bank_api.enums.Banking;
import com.exalt_company.kata_bank_api.exception.ErrorResponse;
import com.exalt_company.kata_bank_api.exception.FundException;
import com.exalt_company.kata_bank_api.service.IFundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fund")
@Tag(name = "Funds", description = "Fund management APIs for deposits, withdrawals, and overdraw functionality")
public class FundController extends BaseController<FundDto, IFundService> {
    @Autowired
    public FundController(IFundService service) {
        super(service);
    }

    @Operation(summary = "Deposit funds", description = "Deposits funds into a user's account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deposit successful",
                    content = @Content(schema = @Schema(implementation = Banking.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/deposit")
    public ResponseEntity<Banking> deposit(
            @RequestBody FundOpDto dto) throws FundException {
        return service.deposit(dto);
    }

    @Operation(summary = "Withdraw funds", description = "Withdraws funds from a user's account. Supports overdraw functionality when enabled.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Withdrawal successful",
                    content = @Content(schema = @Schema(implementation = Banking.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data or insufficient funds/overdraw limit exceeded",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/withdraw")
    public ResponseEntity<Banking> withdraw(
            @RequestBody FundOpDto dto) throws FundException {
        return service.withdraw(dto);
    }

    @Operation(summary = "Request overdraw capabilities", 
               description = "Requests authorization for overdraw functionality on a fund. This enables the ability to withdraw more money than the current balance, up to the specified maximum overdraw limit. " +
                           "Once authorized, users can withdraw up to the maxOverdraw amount even if their balance becomes negative.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Overdraw capabilities authorized successfully",
                    content = @Content(schema = @Schema(implementation = Banking.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data, fund not found, or unauthorized access",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/request-overdraw")
    public ResponseEntity<Banking> requestOverdrawCapabilities(
            @RequestBody OverdrawDto overdrawDto) throws FundException {
        return service.requestOverdrawCapabilities(overdrawDto);
    }

    @Operation(summary = "Cancel overdraw capabilities", 
               description = "Cancels the overdraw functionality on a fund. This operation is only allowed when the account balance is not negative. " +
                           "If the account has a negative balance, the overdraw capabilities cannot be cancelled until the balance is restored to zero or positive.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Overdraw capabilities cancelled successfully",
                    content = @Content(schema = @Schema(implementation = Banking.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data, fund not found, negative balance, or unauthorized access",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/cancel-overdraw")
    public ResponseEntity<Banking> cancelOverdrawCapabilities(
            @RequestBody OverdrawDto overdrawDto) throws FundException {
        return service.cancelOverdrawCapabilities(overdrawDto);
    }
}
