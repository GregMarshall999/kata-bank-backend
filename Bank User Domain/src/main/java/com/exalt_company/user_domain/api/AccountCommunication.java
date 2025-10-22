package com.exalt_company.user_domain.api;

import com.exalt_company.user_domain.domain.message.CondensedMessage;
import com.exalt_company.user_domain.domain.message.Message;
import com.exalt_company.user_domain.domain.message.MessageStatus;
import com.exalt_company.user_domain.shared.Page;
import com.exalt_company.user_domain.shared.exception.AccountComunicationException;

import java.util.UUID;

public interface AccountCommunication<F> {
    Message<F> consultMessage(UUID messageId) throws AccountComunicationException;
    Page<CondensedMessage> listMessages(int page, int size, UUID userMessages) throws AccountComunicationException;
    MessageStatus sendMessageTo(Message<F> message) throws AccountComunicationException;
}
