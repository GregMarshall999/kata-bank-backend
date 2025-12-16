package com.exalt_company.kata_bank.adapter.v1.spi;

import com.exalt_company.user_domain.domain.message.Message;
import com.exalt_company.user_domain.domain.message.MessageStatus;
import com.exalt_company.user_domain.shared.Page;
import com.exalt_company.user_domain.shared.exception.MessageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MessagesJpaAdapterTest {

    @InjectMocks
    private MessagesJpaAdapter messagesJpaAdapter;

    private UUID messageId;
    private UUID ownerId;

    @BeforeEach
    void setUp() {
        messageId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
    }

    @Test
    void should_return_null_when_getting_message_by_id() throws MessageException {
        //When
        Message<File> message = messagesJpaAdapter.getMessageById(messageId);

        //Then
        assertThat(message).isNull();
    }

    @Test
    void should_return_null_when_paging_user_messages() throws MessageException {
        //Given
        int page = 0;
        int size = 10;

        //When
        Page<Message<File>> result = messagesJpaAdapter.pageUserMessages(page, size, ownerId);

        //Then
        assertThat(result).isNull();
    }

    @Test
    void should_return_null_when_sending_message() throws MessageException {
        //Given
        Message<File> message = new Message<>();

        //When
        MessageStatus status = messagesJpaAdapter.sendMessageTo(message);

        //Then
        assertThat(status).isNull();
    }

    @Test
    void should_handle_different_message_ids() throws MessageException {
        //Given
        UUID messageId1 = UUID.randomUUID();
        UUID messageId2 = UUID.randomUUID();

        //When
        Message<File> message1 = messagesJpaAdapter.getMessageById(messageId1);
        Message<File> message2 = messagesJpaAdapter.getMessageById(messageId2);

        //Then
        assertThat(message1).isNull();
        assertThat(message2).isNull();
    }

    @Test
    void should_handle_different_paging_parameters() throws MessageException {
        //Given
        int page1 = 0;
        int size1 = 10;
        int page2 = 1;
        int size2 = 20;

        //When
        Page<Message<File>> result1 = messagesJpaAdapter.pageUserMessages(page1, size1, ownerId);
        Page<Message<File>> result2 = messagesJpaAdapter.pageUserMessages(page2, size2, ownerId);

        //Then
        assertThat(result1).isNull();
        assertThat(result2).isNull();
    }

    @Test
    void should_handle_null_message() throws MessageException {
        //When
        MessageStatus status = messagesJpaAdapter.sendMessageTo(null);

        //Then
        assertThat(status).isNull();
    }

    @Test
    void should_handle_zero_page_and_size() throws MessageException {
        //When
        Page<Message<File>> result = messagesJpaAdapter.pageUserMessages(0, 0, ownerId);

        //Then
        assertThat(result).isNull();
    }

    @Test
    void should_handle_negative_page() throws MessageException {
        //When
        Page<Message<File>> result = messagesJpaAdapter.pageUserMessages(-1, 10, ownerId);

        //Then
        assertThat(result).isNull();
    }
}

