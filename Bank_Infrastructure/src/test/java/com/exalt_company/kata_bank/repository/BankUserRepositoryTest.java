package com.exalt_company.kata_bank.repository;

import com.exalt_company.kata_bank.entity.BankUser;
import com.exalt_company.user_domain.shared.BankRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BankUserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BankUserRepository bankUserRepository;

    private BankUser user;

    @BeforeEach
    void setUp() {
        user = new BankUser();
        user.setName("John");
        user.setSurname("Doe");
        user.setEmail("john.doe@test.com");
        user.setPassword("password123");
        user.setRole(BankRole.CLIENT);
        user = entityManager.persistAndFlush(user);
    }

    @Test
    void should_save_bank_user() {
        //Given
        BankUser newUser = new BankUser();
        newUser.setName("Jane");
        newUser.setSurname("Smith");
        newUser.setEmail("jane.smith@test.com");
        newUser.setPassword("password456");
        newUser.setRole(BankRole.ADMIN);

        //When
        BankUser saved = bankUserRepository.save(newUser);

        //Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Jane");
        assertThat(saved.getSurname()).isEqualTo("Smith");
        assertThat(saved.getEmail()).isEqualTo("jane.smith@test.com");
        assertThat(saved.getRole()).isEqualTo(BankRole.ADMIN);
    }

    @Test
    void should_find_user_by_id() {
        //Given - user created in setUp

        //When
        Optional<BankUser> found = bankUserRepository.findById(user.getId());

        //Then
        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(user);
        assertThat(found.get().getEmail()).isEqualTo("john.doe@test.com");
    }

    @Test
    void should_not_find_user_by_non_existent_id() {
        //When
        Optional<BankUser> found = bankUserRepository.findById(UUID.randomUUID());

        //Then
        assertThat(found).isNotPresent();
    }

    @Test
    void should_find_user_by_email() {
        //Given - user created in setUp

        //When
        Optional<BankUser> found = bankUserRepository.findByEmail("john.doe@test.com");

        //Then
        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(user);
        assertThat(found.get().getEmail()).isEqualTo("john.doe@test.com");
    }

    @Test
    void should_not_find_user_by_non_existent_email() {
        //When
        Optional<BankUser> found = bankUserRepository.findByEmail("nonexistent@test.com");

        //Then
        assertThat(found).isNotPresent();
    }

    @Test
    void should_find_user_by_email_case_sensitive() {
        //Given - user created in setUp with email "john.doe@test.com"

        //When
        Optional<BankUser> found = bankUserRepository.findByEmail("JOHN.DOE@TEST.COM");

        //Then
        assertThat(found).isNotPresent();
    }

    @Test
    void should_find_all_users() {
        //Given - user created in setUp
        BankUser user2 = new BankUser();
        user2.setName("Jane");
        user2.setSurname("Smith");
        user2.setEmail("jane.smith@test.com");
        user2.setPassword("password456");
        user2.setRole(BankRole.COUNSELOR);
        entityManager.persistAndFlush(user2);

        //When
        var allUsers = bankUserRepository.findAll();

        //Then
        assertThat(allUsers).hasSize(2);
    }

    @Test
    void should_delete_user() {
        //Given - user created in setUp

        //When
        bankUserRepository.delete(user);
        entityManager.flush();

        //Then
        Optional<BankUser> found = bankUserRepository.findById(user.getId());
        assertThat(found).isNotPresent();
    }

    @Test
    void should_update_user() {
        //Given - user created in setUp

        //When
        user.setName("UpdatedName");
        user.setSurname("UpdatedSurname");
        BankUser updated = bankUserRepository.save(user);
        entityManager.flush();

        //Then
        Optional<BankUser> found = bankUserRepository.findById(user.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("UpdatedName");
        assertThat(found.get().getSurname()).isEqualTo("UpdatedSurname");
    }

    @Test
    void should_find_user_with_different_roles() {
        //Given
        BankUser adminUser = new BankUser();
        adminUser.setName("Admin");
        adminUser.setSurname("User");
        adminUser.setEmail("admin@test.com");
        adminUser.setPassword("admin123");
        adminUser.setRole(BankRole.ADMIN);
        entityManager.persistAndFlush(adminUser);

        BankUser counselorUser = new BankUser();
        counselorUser.setName("Counselor");
        counselorUser.setSurname("User");
        counselorUser.setEmail("counselor@test.com");
        counselorUser.setPassword("counselor123");
        counselorUser.setRole(BankRole.COUNSELOR);
        entityManager.persistAndFlush(counselorUser);

        //When
        Optional<BankUser> foundAdmin = bankUserRepository.findByEmail("admin@test.com");
        Optional<BankUser> foundCounselor = bankUserRepository.findByEmail("counselor@test.com");

        //Then
        assertThat(foundAdmin).isPresent();
        assertThat(foundAdmin.get().getRole()).isEqualTo(BankRole.ADMIN);
        assertThat(foundCounselor).isPresent();
        assertThat(foundCounselor.get().getRole()).isEqualTo(BankRole.COUNSELOR);
    }
}

