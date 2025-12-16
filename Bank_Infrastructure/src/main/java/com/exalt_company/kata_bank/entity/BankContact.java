package com.exalt_company.kata_bank.entity;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
public class BankContact extends BaseEntity {
    @NotNull
    private UUID contactId;

    private String customName;

    public BankContact() {
    }

    public BankContact(UUID contactId, String customName) {
        this.contactId = contactId;
        this.customName = customName;
    }

    public UUID getContactId() {
        return contactId;
    }

    public void setContactId(UUID contactId) {
        this.contactId = contactId;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }
}
