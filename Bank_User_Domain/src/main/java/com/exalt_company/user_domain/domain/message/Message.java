package com.exalt_company.user_domain.domain.message;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Represents a user-facing message exchanged between bank users.
 * <p>
 * A message holds metadata such as author, recipient, copy lists, and
 * optionally attached files. The generic type parameter allows callers to
 * define the concrete type used for attachments (e.g., a file descriptor or
 * a storage key).
 * </p>
 *
 * @param <F> the type used to represent an attached file reference
 */
public class Message<F> {
    private UUID id;
    private LocalDate date;
    private String content;
    private String author;
    private String recipient;
    private List<String> copies;
    private List<String> hiddenCopies;
    private List<F> attachedFiles;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public List<String> getCopies() {
        return copies;
    }

    public void setCopies(List<String> copies) {
        this.copies = copies;
    }

    public List<String> getHiddenCopies() {
        return hiddenCopies;
    }

    public void setHiddenCopies(List<String> hiddenCopies) {
        this.hiddenCopies = hiddenCopies;
    }

    public List<F> getAttachedFiles() {
        return attachedFiles;
    }

    public void setAttachedFiles(List<F> attachedFiles) {
        this.attachedFiles = attachedFiles;
    }
}