package com.exalt_company.kata_bank_api.service;

import com.exalt_company.kata_bank_api.dto.FundDto;
import com.exalt_company.kata_bank_api.enums.Banking;
import com.exalt_company.kata_bank_api.exception.FundException;
import org.springframework.http.ResponseEntity;

public interface IFundService extends IBaseService<FundDto> {
    ResponseEntity<Banking> deposit(FundDto dto) throws FundException;
    ResponseEntity<Banking> withdraw(FundDto dto) throws FundException;
}
