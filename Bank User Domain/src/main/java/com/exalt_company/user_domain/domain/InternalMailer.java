package com.exalt_company.user_domain.domain;

import com.exalt_company.user_domain.api.AccountCommunication;
import com.exalt_company.user_domain.ddd.UserDomainService;
import com.exalt_company.user_domain.domain.message.CondensedMessage;
import com.exalt_company.user_domain.domain.message.Message;
import com.exalt_company.user_domain.domain.message.MessageStatus;
import com.exalt_company.user_domain.shared.Page;
import com.exalt_company.user_domain.shared.exception.AccountComunicationException;
import com.exalt_company.user_domain.shared.exception.MessageException;
import com.exalt_company.user_domain.spi.Messages;

import java.io.File;
import java.util.List;
import java.util.UUID;

/**
 * Default Internal Mailer implementation
 * File type is used to represent an attached file reference
 */
@UserDomainService
public class InternalMailer implements AccountCommunication<File> {
    private final Messages<File> messages;

    public InternalMailer(Messages<File> messages) {
        this.messages = messages;
    }

    @Override
    public Message<File> consultMessage(UUID messageId) throws AccountComunicationException {
        try {
            return messages.getMessageById(messageId);
        } catch (MessageException e) {
            throw new AccountComunicationException("Could not get requested message: " + e.getMessage());
        }
    }

    @Override
    public Page<CondensedMessage> listMessages(int page, int size, UUID userMessages)
            throws AccountComunicationException {

        try {
            Page<Message<File>> messagePage = messages.pageUserMessages(page, size, userMessages);

            List<CondensedMessage> condensedMessages = messagePage.content().stream()
                    .map(message -> new CondensedMessage(
                            message.getId(),
                            message.getDate(),
                            message.getContent().substring(0, 10) + "...",
                            message.getRecipient()
                    ))
                    .toList();

            return new Page<>(condensedMessages, messagePage.page(), messagePage.size());
        } catch (MessageException e) {
            throw new AccountComunicationException("Failed to list messages: " + e.getMessage());
        }
    }

    @Override
    public MessageStatus sendMessageTo(Message<File> message) throws AccountComunicationException {
        try {
            return messages.sendMessageTo(message);
        } catch (MessageException e) {
            throw new AccountComunicationException("Could not send message: " + e.getMessage());
        }
    }
}
