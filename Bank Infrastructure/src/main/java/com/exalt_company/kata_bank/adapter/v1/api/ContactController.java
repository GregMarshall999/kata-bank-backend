package com.exalt_company.kata_bank.adapter.v1.api;

import com.exalt_company.user_domain.api.resource.contact.ContactPageRequest;
import com.exalt_company.user_domain.api.resource.contact.ContactPageResponse;
import com.exalt_company.user_domain.api.AccountRequest;
import com.exalt_company.user_domain.shared.exception.BankUserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/contact")
public class ContactController {
    private final AccountRequest accountRequest;

    public ContactController(AccountRequest accountRequest) {
        this.accountRequest = accountRequest;
    }

    @PostMapping("/seach")
    public ResponseEntity<ContactPageResponse> searchContactsByEmailLike(@RequestBody ContactPageRequest request)
            throws BankUserException {
        ContactPageResponse response = accountRequest.searchContactPageByEmail(request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
