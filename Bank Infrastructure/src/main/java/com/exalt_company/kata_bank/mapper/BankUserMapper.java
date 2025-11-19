package com.exalt_company.kata_bank.mapper;

import com.exalt_company.kata_bank.entity.BankUser;
import com.exalt_company.user_domain.domain.account.BankUserAccount;

import java.util.List;

public class BankUserMapper {
    private BankUserMapper() {}

    public static BankUser fromDomain(BankUserAccount account) {
        return new BankUser(
                account.getId(),
                account.getName(),
                account.getSurname(),
                account.getEmail(),
                account.getPassword(),
                account.getRole()
        );
    }

    public static BankUserAccount toDomain(BankUser user) {
        return new BankUserAccount(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getPassword(),
                user.getRole()
        );
    }

    public static List<BankUserAccount> toDomain(List<BankUser> users) {
        return users.stream().map(BankUserMapper::toDomain).toList();
    }
}
