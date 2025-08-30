package com.exalt_company.kata_bank_api.controller;

import com.exalt_company.kata_bank_api.dto.FundDto;
import com.exalt_company.kata_bank_api.enums.Banking;
import com.exalt_company.kata_bank_api.exception.FundException;
import com.exalt_company.kata_bank_api.service.IFundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FundController extends BaseController<FundDto, IFundService> {
    @Autowired
    public FundController(IFundService service) {
        super(service);
    }

    @PostMapping("/deposit")
    public ResponseEntity<Banking> deposit(@RequestBody FundDto dto) throws FundException {
        return service.deposit(dto);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Banking> withdraw(@RequestBody FundDto dto) throws FundException {
        return service.withdraw(dto);
    }
}
