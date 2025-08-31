package com.exalt_company.kata_bank_api.controller;

import com.exalt_company.kata_bank_api.dto.fund.FundDto;
import com.exalt_company.kata_bank_api.dto.fund.FundOpDto;
import com.exalt_company.kata_bank_api.dto.fund.OverdrawDto;
import com.exalt_company.kata_bank_api.enums.Banking;
import com.exalt_company.kata_bank_api.exception.FundException;
import com.exalt_company.kata_bank_api.service.IFundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fund")
@Tag(name = "Funds", description = "Fund management APIs for deposits and withdrawals")
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
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/deposit")
    public ResponseEntity<Banking> deposit(
            @RequestBody FundOpDto dto, @RequestHeader(name = "Authorization") String token) throws FundException {
        return service.deposit(dto, token);
    }

    @Operation(summary = "Withdraw funds", description = "Withdraws funds from a user's account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Withdrawal successful",
                    content = @Content(schema = @Schema(implementation = Banking.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Insufficient funds")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/withdraw")
    public ResponseEntity<Banking> withdraw(
            @RequestBody FundOpDto dto, @RequestHeader(name = "Authorization") String token) throws FundException {
        return service.withdraw(dto, token);
    }

    @PutMapping("/request-overdraw")
    public ResponseEntity<Banking> requestOverdrawCapabilities(
            @RequestBody OverdrawDto overdrawDto,
            @RequestHeader(name = "Authorization") String token) throws FundException {
        return service.requestOverdrawCapabilities(overdrawDto, token);
    }

    @PutMapping("/cancel-overdraw")
    public ResponseEntity<Banking> cancelOverdrawCapabilities(
            @RequestBody OverdrawDto overdrawDto,
            @RequestHeader(name = "Authorization") String token) throws FundException {
        return service.cancelOverdrawCapabilities(overdrawDto, token);
    }
}
