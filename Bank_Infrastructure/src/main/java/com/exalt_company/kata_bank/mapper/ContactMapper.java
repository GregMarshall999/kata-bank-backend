package com.exalt_company.kata_bank.mapper;

import com.exalt_company.contact_domain.api.resource.OperationRequest;
import com.exalt_company.contact_domain.api.resource.OperationState;
import com.exalt_company.contact_domain.domain.Contact;
import com.exalt_company.kata_bank.adapter.v1.resource.ContactRequest;
import com.exalt_company.kata_bank.adapter.v1.resource.ContactResponse;
import com.exalt_company.kata_bank.adapter.v1.resource.UserContact;

import java.util.List;

public interface ContactMapper {
    static OperationRequest toDomain(ContactRequest request) {
        return new OperationRequest(request.requesterId(), request.contactId(), request.contactName());
    }

    static ContactResponse fromDomain(OperationState state) {
        return switch (state) {
            case ADDED -> ContactResponse.ADDED;
            case EDITED -> ContactResponse.EDITED;
            case DELETED -> ContactResponse.DELETED;
            case FAILED -> ContactResponse.FAILED;
        };
    }

    static List<UserContact> fromDomain(List<Contact> contacts) {
        return contacts.stream()
                .map(contact -> new UserContact(contact.contactName(), contact.contactId()))
                .toList();
    }
}
