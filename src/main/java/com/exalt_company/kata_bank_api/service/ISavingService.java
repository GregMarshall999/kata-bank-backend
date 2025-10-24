package com.exalt_company.kata_bank_api.service;

import com.exalt_company.kata_bank_api.dto.SavingDto;
import com.exalt_company.kata_bank_api.enums.Banking;
import com.exalt_company.kata_bank_api.exception.FundException;
import com.exalt_company.kata_bank_api.exception.SavingException;
import org.springframework.http.ResponseEntity;

public interface ISavingService extends IBaseService<SavingDto> {
    ResponseEntity<Banking> openSavingsAccount(SavingDto dto) throws SavingException;
    ResponseEntity<Banking> closeSavingsAccount(SavingDto dto) throws SavingException;
    ResponseEntity<Banking> deposit(SavingDto dto) throws FundException, SavingException;
    ResponseEntity<Banking> withdraw(SavingDto dto) throws SavingException;

    ResponseEntity<SavingDto> getByOwner(long userId);
}
