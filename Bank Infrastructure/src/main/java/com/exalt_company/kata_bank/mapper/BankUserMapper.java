package com.exalt_company.kata_bank.mapper;

import com.exalt_company.kata_bank.entity.BankUser;
import com.exalt_company.user_domain.domain.account.BankUserAccount;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BankUserMapper {
    BankUser fromDomain(BankUserAccount account);
    BankUserAccount toDomain(BankUser user);
    List<BankUserAccount> toDomain(List<BankUser> users);
}
