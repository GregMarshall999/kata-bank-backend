package com.exalt_company.kata_bank.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * JPA entity representing a message exchanged between bank users.
 * Messages can have recipients, copies, hidden copies, and attached files.
 */
@Entity
public class Message extends BaseEntity {
    @NotNull
    private LocalDate date;

    private String content;

    @ManyToOne(optional = false)
    private BankUser author;

    @ManyToOne(optional = false)
    private BankUser recipientEmail;

    @ManyToMany
    private List<BankUser> copiesTo;

    @ManyToMany
    private List<BankUser> hiddenCopies;

    @OneToMany
    private List<MessageBankFile> attachedFilesLocation;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public BankUser getAuthor() {
        return author;
    }

    public void setAuthor(BankUser author) {
        this.author = author;
    }

    public BankUser getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(BankUser recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public List<BankUser> getCopiesTo() {
        return copiesTo;
    }

    public void setCopiesTo(List<BankUser> copiesTo) {
        this.copiesTo = copiesTo;
    }

    public List<BankUser> getHiddenCopies() {
        return hiddenCopies;
    }

    public void setHiddenCopies(List<BankUser> hiddenCopies) {
        this.hiddenCopies = hiddenCopies;
    }

    public List<MessageBankFile> getAttachedFilesLocation() {
        return attachedFilesLocation;
    }

    public void setAttachedFilesLocation(List<MessageBankFile> attachedFilesLocation) {
        this.attachedFilesLocation = attachedFilesLocation;
    }
}
