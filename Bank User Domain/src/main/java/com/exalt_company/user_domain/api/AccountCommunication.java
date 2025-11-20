package com.exalt_company.user_domain.api;

import com.exalt_company.user_domain.domain.message.CondensedMessage;
import com.exalt_company.user_domain.domain.message.Message;
import com.exalt_company.user_domain.domain.message.MessageStatus;
import com.exalt_company.user_domain.shared.Page;
import com.exalt_company.user_domain.shared.exception.AccountComunicationException;

import java.util.UUID;

/**
 * Contract for communicating with user accounts through messages.
 *
 * @param <F> the payload type contained in the {@link Message} body
 */
public interface AccountCommunication<F> {
    /**
     * Retrieves a full {@link Message} by its unique identifier.
     *
     * @param messageId the unique identifier of the message to consult
     * @return the full {@link Message} with its payload
     * @throws AccountComunicationException if the message cannot be found or retrieved
     */
    Message<F> consultMessage(UUID messageId) throws AccountComunicationException;

    /**
     * Lists a paginated collection of a user's messages in condensed form.
     *
     * @param page zero-based page index
     * @param size number of items per page
     * @param userMessages the identifier of the user whose messages to list
     * @return a {@link Page} of {@link CondensedMessage} entries
     * @throws AccountComunicationException if listing messages fails
     */
    Page<CondensedMessage> listMessages(int page, int size, UUID userMessages) throws AccountComunicationException;

    /**
     * Sends the provided {@link Message} to its intended recipient(s).
     *
     * @param message the message to send
     * @return the resulting {@link MessageStatus}
     * @throws AccountComunicationException if sending the message fails
     */
    MessageStatus sendMessageTo(Message<F> message) throws AccountComunicationException;
}
