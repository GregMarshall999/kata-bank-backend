package com.exalt_company.kata_bank.adapter.v1.api;

import com.exalt_company.fund_domain.api.FundAction;
import com.exalt_company.fund_domain.api.resource.Deposit;
import com.exalt_company.fund_domain.api.resource.Withdraw;
import com.exalt_company.fund_domain.shared.FundException;
import com.exalt_company.fund_domain.shared.FundStatus;
import com.exalt_company.kata_bank.adapter.v1.resource.FundRequest;
import com.exalt_company.kata_bank.mapper.FundMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;

@RestController
@RequestMapping("/api/v1/fund")
public class FundController {
    private final FundAction fundAction;

    public FundController(FundAction fundAction) {
        this.fundAction = fundAction;
    }

    @PostMapping("/deposit")
    public ResponseEntity<FundStatus> deposit(@RequestBody FundRequest request)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException,
            FundException {
        FundStatus depositStatus = fundAction.deposit(FundMapper.toDomain(request, Deposit.class));

        HttpStatus responseStatus = depositStatus.equals(FundStatus.CREATED) ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(responseStatus).body(depositStatus);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<FundStatus> withdraw(@RequestBody FundRequest request)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException,
            FundException {
        FundStatus withdrawalStatus = fundAction.withdraw(FundMapper.toDomain(request, Withdraw.class));

        return ResponseEntity.status(HttpStatus.OK).body(withdrawalStatus);
    }
}
