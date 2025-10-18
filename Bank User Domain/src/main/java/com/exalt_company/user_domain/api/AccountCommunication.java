package com.exalt_company.user_domain.api;

import com.exalt_company.user_domain.domain.message.CondensedMessage;
import com.exalt_company.user_domain.domain.message.Message;
import com.exalt_company.user_domain.domain.message.MessageStatus;
import com.exalt_company.user_domain.shared.Page;

import java.util.UUID;

public interface AccountCommunication<F> {
    Message<F> consultMessage(UUID messageId);
    Page<CondensedMessage> listMessages(int page, int size, UUID userMessages);
    MessageStatus sendMessageTo(Message<F> message);
}
