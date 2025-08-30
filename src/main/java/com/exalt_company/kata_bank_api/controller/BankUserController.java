package com.exalt_company.kata_bank_api.controller;

import com.exalt_company.kata_bank_api.dto.BankUserDto;
import com.exalt_company.kata_bank_api.service.IBankUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Since we use generic CRUD operations, we have very little endpoints to add.
 * Autowired helps spring with dependency injection. It will find an implementation of the service interface and
 * instantiate it in the constructor.
 */
@RestController
@RequestMapping("/api/bank-user")
public class BankUserController extends BaseController<BankUserDto, IBankUserService> {
    @Autowired
    public BankUserController(IBankUserService service) {
        super(service);
    }
}
