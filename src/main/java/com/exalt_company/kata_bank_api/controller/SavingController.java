package com.exalt_company.kata_bank_api.controller;

import com.exalt_company.kata_bank_api.dto.SavingDto;
import com.exalt_company.kata_bank_api.enums.Banking;
import com.exalt_company.kata_bank_api.exception.FundException;
import com.exalt_company.kata_bank_api.exception.SavingException;
import com.exalt_company.kata_bank_api.service.ISavingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/saving")
public class SavingController extends BaseController<SavingDto, ISavingService> {
    @Autowired
    public SavingController(ISavingService service) {
        super(service);
    }

    @PostMapping("/open")
    public ResponseEntity<Banking> openSavingsAccount(
            @RequestBody SavingDto dto, @RequestHeader("Authorization") String token) throws SavingException {
        return service.openSavingsAccount(dto, token);
    }

    @PostMapping("/close")
    public ResponseEntity<Banking> closeSavingsAccount(
            @RequestBody SavingDto dto, @RequestHeader("Authorization") String token) throws SavingException {
        return service.closeSavingsAccount(dto, token);
    }

    @PostMapping("/deposit")
    public ResponseEntity<Banking> deposit(
            @RequestBody SavingDto dto, @RequestHeader(name = "Authorization") String token) throws FundException, SavingException {
        return service.deposit(dto, token);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Banking> withdraw(
            @RequestBody SavingDto dto, @RequestHeader(name = "Authorization") String token) throws SavingException {
        return service.withdraw(dto, token);
    }
}
