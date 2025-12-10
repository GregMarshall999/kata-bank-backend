package com.exalt_company.user_domain.domain;

import com.exalt_company.user_domain.api.AccountRequest;
import com.exalt_company.user_domain.api.resource.contact.ContactPageRequest;
import com.exalt_company.user_domain.api.resource.contact.ContactPageResponse;
import com.exalt_company.user_domain.ddd.UserDomainService;
import com.exalt_company.user_domain.domain.account.BankUserAccount;
import com.exalt_company.user_domain.shared.Page;
import com.exalt_company.user_domain.shared.exception.BankUserException;
import com.exalt_company.user_domain.spi.BankUsers;

@UserDomainService
public class ContactFinder implements AccountRequest {
    private final BankUsers bankUsers;

    public ContactFinder(BankUsers bankUsers) {
        this.bankUsers = bankUsers;
    }

    @Override
    public ContactPageResponse searchContactPageByEmail(ContactPageRequest request) throws BankUserException {
        Page<BankUserAccount> pagedBankUsers =
                bankUsers.searchAccountsByEmail(request.email(), request.page(), request.size());

        return new ContactPageResponse(
                pagedBankUsers.content().stream().map(BankUserAccount::getEmail).toList(),
                pagedBankUsers.page(),
                pagedBankUsers.totalPages(),
                pagedBankUsers.totalElements()
        );
    }
}
