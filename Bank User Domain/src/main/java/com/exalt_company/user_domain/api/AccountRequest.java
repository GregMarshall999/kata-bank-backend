package com.exalt_company.user_domain.api;

import com.exalt_company.user_domain.api.resource.contact.ContactPageRequest;
import com.exalt_company.user_domain.api.resource.contact.ContactPageResponse;
import com.exalt_company.user_domain.shared.exception.BankUserException;

public interface AccountRequest {
    ContactPageResponse searchContactPageByEmail(ContactPageRequest request) throws BankUserException;
}
