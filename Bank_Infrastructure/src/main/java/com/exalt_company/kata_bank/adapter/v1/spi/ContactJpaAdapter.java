package com.exalt_company.kata_bank.adapter.v1.spi;

import com.exalt_company.contact_domain.api.resource.OperationState;
import com.exalt_company.contact_domain.domain.Contact;
import com.exalt_company.contact_domain.shared.ContactException;
import com.exalt_company.contact_domain.spi.Contacts;
import com.exalt_company.kata_bank.entity.BankContact;
import com.exalt_company.kata_bank.entity.BankUser;
import com.exalt_company.kata_bank.repository.BankContactRepository;
import com.exalt_company.kata_bank.repository.BankUserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ContactJpaAdapter implements Contacts {
    private final BankContactRepository contactRepository;
    private final BankUserRepository userRepository;

    public ContactJpaAdapter(BankContactRepository contactRepository, BankUserRepository userRepository) {
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Contact> getUserContacts(UUID userId) throws ContactException {
        BankUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ContactException("No user to fetch contacts from!", OperationState.FAILED));

        return user.getContacts().stream().map(bankContact -> {
            Optional<BankContact> contact = contactRepository.findById(bankContact.getId());
            return contact.map(value -> new Contact(value.getContactId(), value.getCustomName()))
                    .orElse(null);
        }).toList();
    }

    @Override
    public OperationState addContactTo(UUID contact, UUID to, String contactName) throws ContactException {
        BankUser user = userRepository.findById(to)
                .orElseThrow(() -> new ContactException("No user to add contacts to!", OperationState.FAILED));

        Optional<BankContact> existingContact = contactRepository.findById(contact);
        if(existingContact.isPresent())
            throw new ContactException("Attempted to add a preexisting contact!", OperationState.FAILED);

        BankContact savedContact = contactRepository.save(new BankContact(contact, contactName));

        if(!user.getContacts().contains(savedContact)) {
            user.getContacts().add(savedContact);
            userRepository.save(user);
        }

        return OperationState.ADDED;
    }

    @Override
    public OperationState editUserContact(UUID contact, UUID user, String contactName) throws ContactException {
        BankUser bankUser = userRepository.findById(user)
                .orElseThrow(() -> new ContactException("No user for contact editing!", OperationState.FAILED));

        BankContact existingContact = contactRepository.findById(contact)
                .orElseThrow(() -> new ContactException("Could not edit non existing contact!", OperationState.FAILED));

        bankUser.getContacts().remove(existingContact);

        existingContact.setContactId(contact);
        existingContact.setCustomName(contactName);

        BankContact savedContact = contactRepository.save(existingContact);

        bankUser.getContacts().add(savedContact);
        userRepository.save(bankUser);

        return OperationState.EDITED;
    }

    @Override
    public OperationState deleteContact(UUID contactId) throws ContactException {
        BankContact contact = contactRepository.findByContactId(contactId)
                .orElseThrow(() -> new ContactException("No contact to remove!", OperationState.FAILED));

        BankUser user = userRepository.findByContactId(contact.getId())
                .orElseThrow(() -> new ContactException("No user with this contact!", OperationState.FAILED));

        user.getContacts().remove(contact);
        userRepository.save(user);

        contactRepository.delete(contact);

        return OperationState.DELETED;
    }
}
