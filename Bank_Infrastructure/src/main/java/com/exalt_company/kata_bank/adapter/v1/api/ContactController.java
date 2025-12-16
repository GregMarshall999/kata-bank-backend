package com.exalt_company.kata_bank.adapter.v1.api;

import com.exalt_company.contact_domain.api.ContactOperation;
import com.exalt_company.contact_domain.shared.ContactException;
import com.exalt_company.kata_bank.adapter.v1.resource.ContactRequest;
import com.exalt_company.kata_bank.adapter.v1.resource.ContactResponse;
import com.exalt_company.kata_bank.adapter.v1.resource.UserContact;
import com.exalt_company.kata_bank.mapper.ContactMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/contact")
public class ContactController {
    private final ContactOperation operation;

    public ContactController(ContactOperation operation) {
        this.operation = operation;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<UserContact>> getUserContacts(@PathVariable UUID userId) throws ContactException {
        List<UserContact> contacts = ContactMapper.fromDomain(operation.getContacts(userId));

        return ResponseEntity.status(HttpStatus.OK).body(contacts);
    }

    @PostMapping
    public ResponseEntity<ContactResponse> addContact(@RequestBody ContactRequest request) throws ContactException {
        ContactResponse response = ContactMapper.fromDomain(operation.add(ContactMapper.toDomain(request)));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping
    public ResponseEntity<ContactResponse> editContact(@RequestBody ContactRequest request) throws ContactException {
        ContactResponse response = ContactMapper.fromDomain(operation.edit(ContactMapper.toDomain(request)));

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{contactId}")
    public ResponseEntity<ContactResponse> deleteContact(@PathVariable UUID contactId) throws ContactException {
        ContactResponse response = ContactMapper.fromDomain(operation.delete(contactId));

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
