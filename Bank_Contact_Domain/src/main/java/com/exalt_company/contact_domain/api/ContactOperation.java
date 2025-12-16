package com.exalt_company.contact_domain.api;

import com.exalt_company.contact_domain.api.resource.OperationRequest;
import com.exalt_company.contact_domain.api.resource.OperationState;
import com.exalt_company.contact_domain.domain.Contact;
import com.exalt_company.contact_domain.shared.ContactException;

import java.util.List;
import java.util.UUID;

public interface ContactOperation {
    List<Contact> getContacts(UUID userId) throws ContactException;
    OperationState add(OperationRequest request) throws ContactException;
    OperationState edit(OperationRequest request) throws ContactException;
    OperationState delete(UUID contactId) throws ContactException;
}