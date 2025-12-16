package com.exalt_company.contact_domain.domain;

import com.exalt_company.contact_domain.api.ContactOperation;
import com.exalt_company.contact_domain.api.resource.OperationRequest;
import com.exalt_company.contact_domain.api.resource.OperationState;
import com.exalt_company.contact_domain.ddd.ContactDomainService;
import com.exalt_company.contact_domain.shared.ContactException;
import com.exalt_company.contact_domain.spi.Contacts;

import java.util.List;
import java.util.UUID;

@ContactDomainService
public class ContactOperator implements ContactOperation {
    private final Contacts contacts;

    @Override
    public List<Contact> getContacts(UUID userId) throws ContactException {
        return contacts.getUserContacts(userId);
    }

    public ContactOperator(Contacts contacts) {
        this.contacts = contacts;
    }

    @Override
    public OperationState add(OperationRequest request) throws ContactException {
        return contacts.addContactTo(request.contact(), request.requester(), request.name());
    }

    @Override
    public OperationState edit(OperationRequest request) throws ContactException {
        return contacts.editUserContact(request.contact(), request.requester(), request.name());
    }

    @Override
    public OperationState delete(UUID contactId) throws ContactException {
        return contacts.deleteContact(contactId);
    }
}
