package com.exalt_company.kata_bank_api.controller;

import com.exalt_company.kata_bank_api.dto.BankUserDto;
import com.exalt_company.kata_bank_api.service.IBankUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bank-user")
public class BankUserController extends BaseController<BankUserDto, IBankUserService> {
    @Autowired
    public BankUserController(IBankUserService service) {
        super(service);
    }
}
