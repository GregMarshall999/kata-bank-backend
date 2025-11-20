package com.exalt_company.user_domain.spi;

import com.exalt_company.user_domain.domain.message.Message;
import com.exalt_company.user_domain.domain.message.MessageStatus;
import com.exalt_company.user_domain.shared.Page;
import com.exalt_company.user_domain.shared.exception.MessageException;

import java.util.UUID;

/**
 * Service Provider Interface for message retrieval and delivery within the user domain.
 *
 * @param <F> the type of the message payload/fragment carried by {@link Message}
 */
public interface Messages<F> {
    /**
     * Retrieve a message by its unique identifier.
     *
     * @param messageId the unique identifier of the message
     * @return the resolved {@link Message}
     * @throws MessageException if the message cannot be found or access fails
     */
    Message<F> getMessageById(UUID messageId) throws MessageException;

    /**
     * Retrieve a paginated list of messages owned by a specific user/account.
     *
     * @param page zero-based page index
     * @param size the maximum number of items per page
     * @param messagesOwnerId the identifier of the owner of the messages
     * @return a {@link Page} of {@link Message} instances
     * @throws MessageException if pagination or access fails
     */
    Page<Message<F>> pageUserMessages(int page, int size, UUID messagesOwnerId) throws MessageException;

    /**
     * Send the provided message to its intended recipient(s).
     *
     * @param message the message to send
     * @return the resulting {@link MessageStatus} after attempting delivery
     * @throws MessageException if the send operation fails
     */
    MessageStatus sendMessageTo(Message<F> message) throws MessageException;
}
