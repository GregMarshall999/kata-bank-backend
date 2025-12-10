package com.exalt_company.user_domain.spi.stub;

import com.exalt_company.user_domain.domain.message.Message;
import com.exalt_company.user_domain.domain.message.MessageStatus;
import com.exalt_company.user_domain.shared.Page;
import com.exalt_company.user_domain.shared.exception.BankUserException;
import com.exalt_company.user_domain.shared.exception.MessageException;
import com.exalt_company.user_domain.spi.BankUsers;
import com.exalt_company.user_domain.spi.Messages;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InMemoryMessages implements Messages<File> {
    private final Map<UUID, Message<File>> messages = new HashMap<>();

    private final BankUsers bankUsers;

    public InMemoryMessages(BankUsers bankUsers) {
        this.bankUsers = bankUsers;
    }

    @Override
    public Message<File> getMessageById(UUID messageId) throws MessageException {
        if(!messages.containsKey(messageId)) throw new MessageException("Message not found");

        return messages.get(messageId);
    }

    @Override
    public Page<Message<File>> pageUserMessages(int page, int size, UUID messagesOwnerId) throws MessageException {
        if(page < 0 || size < 1) throw new MessageException("Wrong value for parameters");

        int firstIndex = page * size;
        int lastIndex = firstIndex + size;

        try {
            String userEmail = bankUsers.findById(messagesOwnerId).getEmail();


            List<Message<File>> userMessages = messages.values().stream()
                    .filter(message ->
                            message.getAuthor().equals(userEmail) || message.getRecipient().equals(userEmail))
                    .toList();

            List<Message<File>> content = new ArrayList<>();
            for (int i = firstIndex; i < lastIndex; i++) {
                if(i >= userMessages.size()) break;

                content.add(userMessages.get(i));
            }

            return new Page<>(content, page, userMessages.size() / size, userMessages.size());
        } catch (BankUserException e) {
            throw new MessageException("Could not fetch messages: " + e.getMessage());
        }
    }

    @Override
    public MessageStatus sendMessageTo(Message<File> message) throws MessageException {
        UUID id = UUID.randomUUID();

        //To simulate possible mail box errors
        if(messages.containsKey(id)) throw new MessageException("MailBox error");

        message.setId(id);
        messages.put(id, message);

        return MessageStatus.SENT;
    }
}
