package com.exalt_company.kata_bank_api.service;

import com.exalt_company.kata_bank_api.dto.BankUserDto;

/**
 * To ensure scalability we use these interfaces to force implementations depending on our needs.
 */
public interface IBankUserService extends IBaseService<BankUserDto> {
}
