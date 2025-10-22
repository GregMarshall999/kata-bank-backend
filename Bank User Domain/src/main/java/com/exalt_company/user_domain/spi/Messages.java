package com.exalt_company.user_domain.spi;

import com.exalt_company.user_domain.domain.message.Message;
import com.exalt_company.user_domain.domain.message.MessageStatus;
import com.exalt_company.user_domain.shared.Page;
import com.exalt_company.user_domain.shared.exception.MessageException;

import java.util.UUID;

public interface Messages<F> {
    Message<F> getMessageById(UUID messageId) throws MessageException;
    Page<Message<F>> pageUserMessages(int page, int size, UUID messagesOwnerId) throws MessageException;
    MessageStatus sendMessageTo(Message<F> message) throws MessageException;
}
