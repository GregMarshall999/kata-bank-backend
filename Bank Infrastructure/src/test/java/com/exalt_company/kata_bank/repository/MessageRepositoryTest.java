package com.exalt_company.kata_bank.repository;

import com.exalt_company.kata_bank.entity.BankUser;
import com.exalt_company.kata_bank.entity.Message;
import com.exalt_company.user_domain.shared.BankRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MessageRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private BankUserRepository bankUserRepository;

    private BankUser author;
    private BankUser recipient;

    @BeforeEach
    void setUp() {
        author = new BankUser();
        author.setName("John");
        author.setSurname("Doe");
        author.setEmail("john.doe@test.com");
        author.setPassword("password123");
        author.setRole(BankRole.CLIENT);
        author = entityManager.persistAndFlush(author);

        recipient = new BankUser();
        recipient.setName("Jane");
        recipient.setSurname("Smith");
        recipient.setEmail("jane.smith@test.com");
        recipient.setPassword("password456");
        recipient.setRole(BankRole.CLIENT);
        recipient = entityManager.persistAndFlush(recipient);
    }

    @Test
    void should_save_message() {
        //Given
        Message message = new Message();
        message.setDate(LocalDate.now());
        message.setContent("Test message content");
        message.setAuthor(author);
        message.setRecipientEmail(recipient);

        //When
        Message saved = messageRepository.save(message);

        //Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getContent()).isEqualTo("Test message content");
        assertThat(saved.getAuthor()).isEqualTo(author);
        assertThat(saved.getRecipientEmail()).isEqualTo(recipient);
    }

    @Test
    void should_find_message_by_id() {
        //Given
        Message message = new Message();
        message.setDate(LocalDate.now());
        message.setContent("Test message content");
        message.setAuthor(author);
        message.setRecipientEmail(recipient);
        Message saved = entityManager.persistAndFlush(message);

        //When
        Optional<Message> found = messageRepository.findById(saved.getId());

        //Then
        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(saved);
        assertThat(found.get().getContent()).isEqualTo("Test message content");
    }

    @Test
    void should_not_find_message_by_non_existent_id() {
        //When
        Optional<Message> found = messageRepository.findById(UUID.randomUUID());

        //Then
        assertThat(found).isNotPresent();
    }

    @Test
    void should_find_all_messages() {
        //Given
        Message message1 = new Message();
        message1.setDate(LocalDate.now());
        message1.setContent("Message 1");
        message1.setAuthor(author);
        message1.setRecipientEmail(recipient);
        entityManager.persistAndFlush(message1);

        Message message2 = new Message();
        message2.setDate(LocalDate.now());
        message2.setContent("Message 2");
        message2.setAuthor(recipient);
        message2.setRecipientEmail(author);
        entityManager.persistAndFlush(message2);

        //When
        var allMessages = messageRepository.findAll();

        //Then
        assertThat(allMessages).hasSize(2);
    }

    @Test
    void should_delete_message() {
        //Given
        Message message = new Message();
        message.setDate(LocalDate.now());
        message.setContent("Test message content");
        message.setAuthor(author);
        message.setRecipientEmail(recipient);
        Message saved = entityManager.persistAndFlush(message);

        //When
        messageRepository.delete(saved);
        entityManager.flush();

        //Then
        Optional<Message> found = messageRepository.findById(saved.getId());
        assertThat(found).isNotPresent();
    }

    @Test
    void should_update_message() {
        //Given
        Message message = new Message();
        message.setDate(LocalDate.now());
        message.setContent("Original content");
        message.setAuthor(author);
        message.setRecipientEmail(recipient);
        Message saved = entityManager.persistAndFlush(message);

        //When
        saved.setContent("Updated content");
        Message updated = messageRepository.save(saved);
        entityManager.flush();

        //Then
        Optional<Message> found = messageRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getContent()).isEqualTo("Updated content");
    }
}

