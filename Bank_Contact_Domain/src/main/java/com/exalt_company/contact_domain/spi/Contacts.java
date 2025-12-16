package com.exalt_company.contact_domain.spi;

import com.exalt_company.contact_domain.api.resource.OperationState;
import com.exalt_company.contact_domain.domain.Contact;
import com.exalt_company.contact_domain.shared.ContactException;

import java.util.List;
import java.util.UUID;

public interface Contacts {
    List<Contact> getUserContacts(UUID userId) throws ContactException;
    OperationState addContactTo(UUID contact, UUID to, String contactName) throws ContactException;
    OperationState editUserContact(UUID contact, UUID user, String contactName) throws ContactException;
    OperationState deleteContact(UUID contactId) throws ContactException;
}
