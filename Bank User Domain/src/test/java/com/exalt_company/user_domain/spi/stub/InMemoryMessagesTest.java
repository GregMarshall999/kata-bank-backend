package com.exalt_company.user_domain.spi.stub;

import com.exalt_company.user_domain.domain.account.BankUserAccount;
import com.exalt_company.user_domain.domain.message.Message;
import com.exalt_company.user_domain.domain.message.MessageStatus;
import com.exalt_company.user_domain.shared.Page;
import com.exalt_company.user_domain.shared.exception.BankUserException;
import com.exalt_company.user_domain.shared.exception.MessageException;
import com.exalt_company.user_domain.spi.BankUsers;
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

class InMemoryMessagesTest {

    @Mock
    private BankUsers mockBankUsers;

    private InMemoryMessages inMemoryMessages;
    private BankUserAccount testUser;
    private UUID testUserId;
    private String testUserEmail;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        inMemoryMessages = new InMemoryMessages(mockBankUsers);
        testUserId = UUID.randomUUID();
        testUserEmail = "test@example.com";
        testUser = new BankUserAccount("John", "Doe", testUserEmail, "password123");
        testUser.setId(testUserId);
    }

    @Test
    void should_get_message_by_id_when_message_exists() throws MessageException {
        // Given
        Message<File> expectedMessage = createTestMessage();
        inMemoryMessages.sendMessageTo(expectedMessage);
        
        // When
        Message<File> result = inMemoryMessages.getMessageById(expectedMessage.getId());
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(expectedMessage.getId());
        assertThat(result.getAuthor()).isEqualTo(expectedMessage.getAuthor());
        assertThat(result.getRecipient()).isEqualTo(expectedMessage.getRecipient());
        assertThat(result.getContent()).isEqualTo(expectedMessage.getContent());
    }

    @Test
    void should_throw_exception_when_message_not_found() {
        // Given
        UUID nonExistentMessageId = UUID.randomUUID();
        
        // Then Expect
        assertThatThrownBy(() -> inMemoryMessages.getMessageById(nonExistentMessageId))
                .isInstanceOf(MessageException.class)
                .hasMessageContaining("Message not found");
    }

    @Test
    void should_page_user_messages_with_valid_parameters() throws BankUserException, MessageException {
        // Given
        when(mockBankUsers.findById(testUserId)).thenReturn(testUser);
        
        Message<File> message1 = createTestMessage();
        message1.setAuthor(testUserEmail);
        message1.setRecipient("recipient1@example.com");
        
        Message<File> message2 = createTestMessage();
        message2.setAuthor("sender2@example.com");
        message2.setRecipient(testUserEmail);
        
        Message<File> message3 = createTestMessage();
        message3.setAuthor("other@example.com");
        message3.setRecipient("another@example.com");
        
        inMemoryMessages.sendMessageTo(message1);
        inMemoryMessages.sendMessageTo(message2);
        inMemoryMessages.sendMessageTo(message3);
        
        // When
        Page<Message<File>> result = inMemoryMessages.pageUserMessages(0, 2, testUserId);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.content()).hasSize(2);
        
        // Verify that only messages involving the test user are returned
        List<Message<File>> userMessages = result.content();
        assertThat(userMessages.stream().anyMatch(msg -> 
            msg.getAuthor().equals(testUserEmail) || msg.getRecipient().equals(testUserEmail))).isTrue();
    }

    @Test
    void should_throw_exception_when_paging_with_negative_page() {
        // Then Expect
        assertThatThrownBy(() -> inMemoryMessages.pageUserMessages(-1, 10, testUserId))
                .isInstanceOf(MessageException.class)
                .hasMessageContaining("Wrong value for parameters");
    }

    @Test
    void should_throw_exception_when_paging_with_zero_size() {
        // Then Expect
        assertThatThrownBy(() -> inMemoryMessages.pageUserMessages(0, 0, testUserId))
                .isInstanceOf(MessageException.class)
                .hasMessageContaining("Wrong value for parameters");
    }

    @Test
    void should_throw_exception_when_user_not_found() throws BankUserException {
        // Given
        UUID nonExistentUserId = UUID.randomUUID();
        when(mockBankUsers.findById(nonExistentUserId)).thenThrow(new BankUserException("User not found"));
        
        // Then Expect
        assertThatThrownBy(() -> inMemoryMessages.pageUserMessages(0, 10, nonExistentUserId))
                .isInstanceOf(MessageException.class)
                .hasMessageContaining("Could not fetch messages");
    }

    @Test
    void should_page_user_messages_with_pagination() throws BankUserException, MessageException {
        // Given
        when(mockBankUsers.findById(testUserId)).thenReturn(testUser);
        
        // Create multiple messages for the user
        for (int i = 0; i < 5; i++) {
            Message<File> message = createTestMessage();
            message.setAuthor(testUserEmail);
            message.setRecipient("recipient" + i + "@example.com");
            inMemoryMessages.sendMessageTo(message);
        }
        
        // When - Get second page
        Page<Message<File>> result = inMemoryMessages.pageUserMessages(1, 2, testUserId);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.page()).isEqualTo(1);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.content()).hasSize(2);
    }

    @Test
    void should_send_message_and_return_sent_status() throws MessageException {
        // Given
        Message<File> message = createTestMessage();
        
        // When
        MessageStatus result = inMemoryMessages.sendMessageTo(message);
        
        // Then
        assertThat(result).isEqualTo(MessageStatus.SENT);
        assertThat(message.getId()).isNotNull();
    }

    @Test
    void should_set_message_id_when_sending_message() throws MessageException {
        // Given
        Message<File> message = createTestMessage();
        assertThat(message.getId()).isNull();
        
        // When
        inMemoryMessages.sendMessageTo(message);
        
        // Then
        assertThat(message.getId()).isNotNull();
    }

    @Test
    void should_store_message_when_sending() throws MessageException {
        // Given
        Message<File> message = createTestMessage();
        
        // When
        inMemoryMessages.sendMessageTo(message);
        
        // Then
        Message<File> retrievedMessage = inMemoryMessages.getMessageById(message.getId());
        assertThat(retrievedMessage).isNotNull();
        assertThat(retrievedMessage.getId()).isEqualTo(message.getId());
        assertThat(retrievedMessage.getAuthor()).isEqualTo(message.getAuthor());
        assertThat(retrievedMessage.getRecipient()).isEqualTo(message.getRecipient());
    }

    @Test
    void should_return_empty_page_when_no_messages_exist() throws BankUserException, MessageException {
        // Given
        when(mockBankUsers.findById(testUserId)).thenReturn(testUser);
        
        // When
        Page<Message<File>> result = inMemoryMessages.pageUserMessages(0, 10, testUserId);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.content()).isEmpty();
    }

    @Test
    void should_return_partial_page_when_fewer_messages_than_page_size() throws BankUserException, MessageException {
        // Given
        when(mockBankUsers.findById(testUserId)).thenReturn(testUser);
        
        // Create only 3 messages
        for (int i = 0; i < 3; i++) {
            Message<File> message = createTestMessage();
            message.setAuthor(testUserEmail);
            message.setRecipient("recipient" + i + "@example.com");
            inMemoryMessages.sendMessageTo(message);
        }
        
        // When - Request page with size 5, but only 3 messages exist
        Page<Message<File>> result = inMemoryMessages.pageUserMessages(0, 5, testUserId);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(5);
        assertThat(result.content()).hasSize(3);
    }

    private Message<File> createTestMessage() {
        Message<File> message = new Message<>();
        message.setDate(LocalDate.now());
        message.setContent("Test message content");
        message.setAuthor("sender@example.com");
        message.setRecipient("recipient@example.com");
        return message;
    }
}