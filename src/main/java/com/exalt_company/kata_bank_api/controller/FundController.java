package com.exalt_company.kata_bank_api.controller;

import com.exalt_company.kata_bank_api.dto.FundDto;
import com.exalt_company.kata_bank_api.service.IFundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FundController extends BaseController<FundDto, IFundService> {
    @Autowired
    public FundController(IFundService service) {
        super(service);
    }
}
