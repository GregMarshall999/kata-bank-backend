package com.exalt_company.kata_bank.repository;

import com.exalt_company.kata_bank.entity.MessageBankFile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BankFileRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BankFileRepository bankFileRepository;

    @Test
    void should_save_message_bank_file() {
        //Given
        MessageBankFile file = new MessageBankFile();
        file.setFileLocation("/path/to/file.txt");

        //When
        MessageBankFile saved = bankFileRepository.save(file);

        //Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFileLocation()).isEqualTo("/path/to/file.txt");
    }

    @Test
    void should_find_file_by_id() {
        //Given
        MessageBankFile file = new MessageBankFile();
        file.setFileLocation("/path/to/file.txt");
        MessageBankFile saved = entityManager.persistAndFlush(file);

        //When
        Optional<MessageBankFile> found = bankFileRepository.findById(saved.getId());

        //Then
        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(saved);
        assertThat(found.get().getFileLocation()).isEqualTo("/path/to/file.txt");
    }

    @Test
    void should_not_find_file_by_non_existent_id() {
        //When
        Optional<MessageBankFile> found = bankFileRepository.findById(UUID.randomUUID());

        //Then
        assertThat(found).isNotPresent();
    }

    @Test
    void should_find_all_files() {
        //Given
        MessageBankFile file1 = new MessageBankFile();
        file1.setFileLocation("/path/to/file1.txt");
        entityManager.persistAndFlush(file1);

        MessageBankFile file2 = new MessageBankFile();
        file2.setFileLocation("/path/to/file2.txt");
        entityManager.persistAndFlush(file2);

        //When
        var allFiles = bankFileRepository.findAll();

        //Then
        assertThat(allFiles).hasSize(2);
    }

    @Test
    void should_delete_file() {
        //Given
        MessageBankFile file = new MessageBankFile();
        file.setFileLocation("/path/to/file.txt");
        MessageBankFile saved = entityManager.persistAndFlush(file);

        //When
        bankFileRepository.delete(saved);
        entityManager.flush();

        //Then
        Optional<MessageBankFile> found = bankFileRepository.findById(saved.getId());
        assertThat(found).isNotPresent();
    }

    @Test
    void should_update_file() {
        //Given
        MessageBankFile file = new MessageBankFile();
        file.setFileLocation("/path/to/original.txt");
        MessageBankFile saved = entityManager.persistAndFlush(file);

        //When
        saved.setFileLocation("/path/to/updated.txt");
        MessageBankFile updated = bankFileRepository.save(saved);
        entityManager.flush();

        //Then
        Optional<MessageBankFile> found = bankFileRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getFileLocation()).isEqualTo("/path/to/updated.txt");
    }
}

