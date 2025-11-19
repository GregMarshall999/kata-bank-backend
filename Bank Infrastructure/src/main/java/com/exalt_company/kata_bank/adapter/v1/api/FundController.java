package com.exalt_company.kata_bank.adapter.v1.api;

import com.exalt_company.fund_domain.api.FundAction;
import com.exalt_company.fund_domain.api.resource.Deposit;
import com.exalt_company.fund_domain.api.resource.Withdraw;
import com.exalt_company.fund_domain.shared.FundException;
import com.exalt_company.fund_domain.shared.FundStatus;
import com.exalt_company.kata_bank.adapter.v1.resource.FundRequest;
import com.exalt_company.kata_bank.config.SwaggerConfig;
import com.exalt_company.kata_bank.mapper.FundMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;

@RestController
@RequestMapping("/api/v1/fund")
@Tag(name = "Fund Operations", description = "Endpoints for depositing and withdrawing account funds")
@SecurityRequirement(name = SwaggerConfig.BEARER_SCHEME)
public class FundController {
    private final FundAction fundAction;

    public FundController(FundAction fundAction) {
        this.fundAction = fundAction;
    }

    @PostMapping("/deposit")
    @Operation(
            summary = "Deposit funds",
            description = "Adds funds to the specified bank account."
    )
    @ApiResponse(responseCode = "201", description = "Deposit created")
    @ApiResponse(responseCode = "200", description = "Deposit processed")
    @ApiResponse(responseCode = "400", description = "Invalid request",
            content = @Content(schema = @Schema(implementation = FundRequest.class)))
    public ResponseEntity<FundStatus> deposit(@RequestBody FundRequest request)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException,
            FundException {
        FundStatus depositStatus = fundAction.deposit(FundMapper.toDomain(request, Deposit.class));

        HttpStatus responseStatus = depositStatus.equals(FundStatus.CREATED) ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(responseStatus).body(depositStatus);
    }

    @PostMapping("/withdraw")
    @Operation(
            summary = "Withdraw funds",
            description = "Removes funds from the specified bank account."
    )
    @ApiResponse(responseCode = "200", description = "Withdrawal processed")
    @ApiResponse(responseCode = "400", description = "Invalid request",
            content = @Content(schema = @Schema(implementation = FundRequest.class)))
    @ApiResponse(responseCode = "409", description = "Insufficient funds")
    public ResponseEntity<FundStatus> withdraw(@RequestBody FundRequest request)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException,
            FundException {
        FundStatus withdrawalStatus = fundAction.withdraw(FundMapper.toDomain(request, Withdraw.class));

        return ResponseEntity.status(HttpStatus.OK).body(withdrawalStatus);
    }
}
