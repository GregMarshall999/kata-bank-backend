package com.exalt_company.user_domain.domain;

import com.exalt_company.user_domain.domain.message.CondensedMessage;
import com.exalt_company.user_domain.domain.message.Message;
import com.exalt_company.user_domain.domain.message.MessageStatus;
import com.exalt_company.user_domain.shared.Page;
import com.exalt_company.user_domain.shared.exception.AccountComunicationException;
import com.exalt_company.user_domain.shared.exception.MessageException;
import com.exalt_company.user_domain.spi.Messages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class InternalMailerTest {

    @Mock
    private Messages<File> mockMessages;

    private InternalMailer internalMailer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        internalMailer = new InternalMailer(mockMessages);
    }

    // ==================== consultMessage Tests ====================

    @Test
    void should_consult_message_successfully_when_message_exists() throws MessageException, AccountComunicationException {
        // Given
        UUID messageId = UUID.randomUUID();
        Message<File> expectedMessage = createTestMessage();
        expectedMessage.setId(messageId);
        
        when(mockMessages.getMessageById(messageId)).thenReturn(expectedMessage);

        // When
        Message<File> result = internalMailer.consultMessage(messageId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(messageId);
        assertThat(result.getAuthor()).isEqualTo(expectedMessage.getAuthor());
        assertThat(result.getRecipient()).isEqualTo(expectedMessage.getRecipient());
        assertThat(result.getContent()).isEqualTo(expectedMessage.getContent());
    }

    @Test
    void should_throw_account_communication_exception_when_message_not_found() throws MessageException {
        // Given
        UUID messageId = UUID.randomUUID();
        when(mockMessages.getMessageById(messageId))
                .thenThrow(new MessageException("Message not found"));

        // When & Then
        assertThatThrownBy(() -> internalMailer.consultMessage(messageId))
                .isInstanceOf(AccountComunicationException.class)
                .hasMessageContaining("Could not get requested message")
                .hasMessageContaining("Message not found");
    }

    @Test
    void should_throw_account_communication_exception_when_message_access_fails() throws MessageException {
        // Given
        UUID messageId = UUID.randomUUID();
        when(mockMessages.getMessageById(messageId))
                .thenThrow(new MessageException("Access denied"));

        // When & Then
        assertThatThrownBy(() -> internalMailer.consultMessage(messageId))
                .isInstanceOf(AccountComunicationException.class)
                .hasMessageContaining("Could not get requested message")
                .hasMessageContaining("Access denied");
    }

    // ==================== listMessages Tests ====================

    @Test
    void should_list_messages_successfully_with_condensed_content() throws MessageException, AccountComunicationException {
        // Given
        UUID userMessages = UUID.randomUUID();
        int page = 0;
        int size = 10;
        
        Message<File> message1 = createTestMessage();
        message1.setId(UUID.randomUUID());
        message1.setContent("This is a very long message content that should be truncated");
        message1.setRecipient("recipient1@example.com");
        
        Message<File> message2 = createTestMessage();
        message2.setId(UUID.randomUUID());
        message2.setContent("Short message");
        message2.setRecipient("recipient2@example.com");
        
        List<Message<File>> messages = List.of(message1, message2);
        Page<Message<File>> messagePage = new Page<>(messages, page, size);
        
        when(mockMessages.pageUserMessages(page, size, userMessages)).thenReturn(messagePage);

        // When
        Page<CondensedMessage> result = internalMailer.listMessages(page, size, userMessages);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.page()).isEqualTo(page);
        assertThat(result.size()).isEqualTo(size);
        assertThat(result.content()).hasSize(2);
        
        // Verify condensed content is truncated to 10 characters + "..."
        CondensedMessage condensed1 = result.content().get(0);
        assertThat(condensed1.id()).isEqualTo(message1.getId());
        assertThat(condensed1.condensedContent()).isEqualTo("This is a " + "...");
        assertThat(condensed1.recipient()).isEqualTo("recipient1@example.com");
        
        CondensedMessage condensed2 = result.content().get(1);
        assertThat(condensed2.id()).isEqualTo(message2.getId());
        assertThat(condensed2.condensedContent()).isEqualTo("Short mess" + "...");
        assertThat(condensed2.recipient()).isEqualTo("recipient2@example.com");
    }

    @Test
    void should_list_messages_successfully_with_empty_page() throws MessageException, AccountComunicationException {
        // Given
        UUID userMessages = UUID.randomUUID();
        int page = 0;
        int size = 10;
        
        Page<Message<File>> emptyPage = new Page<>(List.of(), page, size);
        when(mockMessages.pageUserMessages(page, size, userMessages)).thenReturn(emptyPage);

        // When
        Page<CondensedMessage> result = internalMailer.listMessages(page, size, userMessages);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.page()).isEqualTo(page);
        assertThat(result.size()).isEqualTo(size);
        assertThat(result.content()).isEmpty();
    }

    @Test
    void should_handle_content_shorter_than_10_characters() throws MessageException, AccountComunicationException {
        // Given
        UUID userMessages = UUID.randomUUID();
        Message<File> message = createTestMessage();
        message.setId(UUID.randomUUID());
        message.setContent("Short");
        message.setRecipient("recipient@example.com");
        
        Page<Message<File>> messagePage = new Page<>(List.of(message), 0, 10);
        when(mockMessages.pageUserMessages(0, 10, userMessages)).thenReturn(messagePage);

        // When
        Page<CondensedMessage> result = internalMailer.listMessages(0, 10, userMessages);

        // Then
        assertThat(result.content()).hasSize(1);
        CondensedMessage condensed = result.content().get(0);
        assertThat(condensed.condensedContent()).isEqualTo("Short" + "...");
    }

    @Test
    void should_throw_account_communication_exception_when_pagination_fails() throws MessageException {
        // Given
        UUID userMessages = UUID.randomUUID();
        when(mockMessages.pageUserMessages(0, 10, userMessages))
                .thenThrow(new MessageException("Pagination failed"));

        // When & Then
        assertThatThrownBy(() -> internalMailer.listMessages(0, 10, userMessages))
                .isInstanceOf(AccountComunicationException.class)
                .hasMessageContaining("Failed to list messages")
                .hasMessageContaining("Pagination failed");
    }

    @Test
    void should_throw_account_communication_exception_when_user_access_fails() throws MessageException {
        // Given
        UUID userMessages = UUID.randomUUID();
        when(mockMessages.pageUserMessages(0, 10, userMessages))
                .thenThrow(new MessageException("User not found"));

        // When & Then
        assertThatThrownBy(() -> internalMailer.listMessages(0, 10, userMessages))
                .isInstanceOf(AccountComunicationException.class)
                .hasMessageContaining("Failed to list messages")
                .hasMessageContaining("User not found");
    }

    // ==================== sendMessageTo Tests ====================

    @Test
    void should_send_message_successfully() throws MessageException, AccountComunicationException {
        // Given
        Message<File> message = createTestMessage();
        when(mockMessages.sendMessageTo(message)).thenReturn(MessageStatus.SENT);

        // When
        MessageStatus result = internalMailer.sendMessageTo(message);

        // Then
        assertThat(result).isEqualTo(MessageStatus.SENT);
    }

    @Test
    void should_return_failed_status_when_message_sending_fails() throws MessageException, AccountComunicationException {
        // Given
        Message<File> message = createTestMessage();
        when(mockMessages.sendMessageTo(message)).thenReturn(MessageStatus.FAILED);

        // When
        MessageStatus result = internalMailer.sendMessageTo(message);

        // Then
        assertThat(result).isEqualTo(MessageStatus.FAILED);
    }

    @Test
    void should_throw_account_communication_exception_when_send_operation_fails() throws MessageException {
        // Given
        Message<File> message = createTestMessage();
        when(mockMessages.sendMessageTo(message))
                .thenThrow(new MessageException("Send operation failed"));

        // When & Then
        assertThatThrownBy(() -> internalMailer.sendMessageTo(message))
                .isInstanceOf(AccountComunicationException.class)
                .hasMessageContaining("Could not send message")
                .hasMessageContaining("Send operation failed");
    }

    @Test
    void should_throw_account_communication_exception_when_message_validation_fails() throws MessageException {
        // Given
        Message<File> message = createTestMessage();
        when(mockMessages.sendMessageTo(message))
                .thenThrow(new MessageException("Invalid message format"));

        // When & Then
        assertThatThrownBy(() -> internalMailer.sendMessageTo(message))
                .isInstanceOf(AccountComunicationException.class)
                .hasMessageContaining("Could not send message")
                .hasMessageContaining("Invalid message format");
    }

    // ==================== Integration Tests ====================

    @Test
    void should_handle_complete_message_lifecycle() throws MessageException, AccountComunicationException {
        // Given
        Message<File> message = createTestMessage();
        UUID messageId = UUID.randomUUID();
        message.setId(messageId);
        
        when(mockMessages.sendMessageTo(message)).thenReturn(MessageStatus.SENT);
        when(mockMessages.getMessageById(messageId)).thenReturn(message);

        // When - Send message
        MessageStatus sendResult = internalMailer.sendMessageTo(message);
        
        // Then - Verify send was successful
        assertThat(sendResult).isEqualTo(MessageStatus.SENT);
        
        // When - Consult the message
        Message<File> consultedMessage = internalMailer.consultMessage(messageId);
        
        // Then - Verify message can be retrieved
        assertThat(consultedMessage).isNotNull();
        assertThat(consultedMessage.getId()).isEqualTo(messageId);
        assertThat(consultedMessage.getContent()).isEqualTo(message.getContent());
    }

    @Test
    void should_handle_message_with_attachments() throws MessageException, AccountComunicationException {
        // Given
        Message<File> message = createTestMessage();
        File attachment = new File("test.txt");
        message.setAttachedFiles(List.of(attachment));
        
        when(mockMessages.sendMessageTo(message)).thenReturn(MessageStatus.SENT);

        // When
        MessageStatus result = internalMailer.sendMessageTo(message);

        // Then
        assertThat(result).isEqualTo(MessageStatus.SENT);
    }

    // ==================== Helper Methods ====================

    private Message<File> createTestMessage() {
        Message<File> message = new Message<>();
        message.setId(UUID.randomUUID());
        message.setDate(LocalDate.now());
        message.setContent("Test message content");
        message.setAuthor("sender@example.com");
        message.setRecipient("recipient@example.com");
        return message;
    }
}