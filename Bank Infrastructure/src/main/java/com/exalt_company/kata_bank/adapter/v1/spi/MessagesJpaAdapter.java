package com.exalt_company.kata_bank.adapter.v1.spi;

import com.exalt_company.user_domain.domain.message.Message;
import com.exalt_company.user_domain.domain.message.MessageStatus;
import com.exalt_company.user_domain.shared.Page;
import com.exalt_company.user_domain.shared.exception.MessageException;
import com.exalt_company.user_domain.spi.Messages;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.UUID;

@Component
public class MessagesJpaAdapter implements Messages<File> {
    @Override
    public Message<File> getMessageById(UUID messageId) throws MessageException {
        return null;
    }

    @Override
    public Page<Message<File>> pageUserMessages(int page, int size, UUID messagesOwnerId) throws MessageException {
        return null;
    }

    @Override
    public MessageStatus sendMessageTo(Message<File> message) throws MessageException {
        return null;
    }
}
