package com.exalt_company.kata_bank.mapper;

import com.exalt_company.kata_bank.entity.BankUser;
import com.exalt_company.user_domain.domain.account.BankUserAccount;

import java.util.List;

/**
 * Mapper utility class for converting between BankUserAccount domain objects
 * and BankUser JPA entities.
 */
public class BankUserMapper {
    private BankUserMapper() {}

    /**
     * Converts a domain BankUserAccount to a JPA BankUser entity.
     *
     * @param account the domain account object
     * @return a JPA BankUser entity
     */
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

    /**
     * Converts a JPA BankUser entity to a domain BankUserAccount object.
     *
     * @param user the JPA entity to convert
     * @return a domain BankUserAccount object
     */
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

    /**
     * Converts a list of JPA BankUser entities to a list of domain BankUserAccount objects.
     *
     * @param users the list of JPA entities to convert
     * @return a list of domain BankUserAccount objects
     */
    public static List<BankUserAccount> toDomain(List<BankUser> users) {
        return users.stream().map(BankUserMapper::toDomain).toList();
    }
}
