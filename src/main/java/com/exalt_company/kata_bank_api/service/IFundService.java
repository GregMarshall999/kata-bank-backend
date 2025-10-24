package com.exalt_company.kata_bank_api.service;

import com.exalt_company.kata_bank_api.dto.fund.FundDto;
import com.exalt_company.kata_bank_api.dto.fund.FundOpDto;
import com.exalt_company.kata_bank_api.dto.fund.OverdrawDto;
import com.exalt_company.kata_bank_api.enums.Banking;
import com.exalt_company.kata_bank_api.exception.FundException;
import org.springframework.http.ResponseEntity;

public interface IFundService extends IBaseService<FundDto> {
    ResponseEntity<Banking> deposit(FundOpDto dto) throws FundException;
    ResponseEntity<Banking> withdraw(FundOpDto dto) throws FundException;

    ResponseEntity<Banking> requestOverdrawCapabilities(OverdrawDto overdrawDto) throws FundException;
    ResponseEntity<Banking> cancelOverdrawCapabilities(OverdrawDto overdrawDto) throws FundException;

    ResponseEntity<FundDto> getByOwner(long userId);
}
