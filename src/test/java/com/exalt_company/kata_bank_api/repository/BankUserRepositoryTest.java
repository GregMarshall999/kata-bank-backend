package com.exalt_company.kata_bank_api.repository;

import com.exalt_company.kata_bank_api.entity.BankUser;
import com.exalt_company.kata_bank_api.entity.user_fields.Credentials;
import com.exalt_company.kata_bank_api.entity.user_fields.Identity;
import com.exalt_company.kata_bank_api.enums.BankRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class BankUserRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BankUserRepository bankUserRepository;

    private BankUser testUser;
    private Credentials credentials;
    private Identity identity;

    @BeforeEach
    void setUp() {
        credentials = new Credentials();
        credentials.setEmail("test@example.com");
        credentials.setPassword("encodedPassword");

        identity = new Identity();
        identity.setName("John");
        identity.setSurname("Doe");

        testUser = new BankUser();
        testUser.setCredentials(credentials);
        testUser.setIdentity(identity);
        testUser.setBankRole(BankRole.CLIENT);
    }

    @Test
    void testSaveBankUser() {
        BankUser savedUser = bankUserRepository.save(testUser);

        assertNotNull(savedUser);
        assertNotEquals(0L, savedUser.getId());
        assertEquals("test@example.com", savedUser.getCredentials().getEmail());
        assertEquals("John", savedUser.getIdentity().getName());
        assertEquals("Doe", savedUser.getIdentity().getSurname());
        assertEquals(BankRole.CLIENT, savedUser.getBankRole());
    }

    @Test
    void testFindByCredentialsEmail() {
        entityManager.persistAndFlush(testUser);

        Optional<BankUser> foundUser = bankUserRepository.findByCredentialsEmail("test@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("test@example.com", foundUser.get().getCredentials().getEmail());
    }

    @Test
    void testFindByCredentialsEmailNotFound() {
        Optional<BankUser> foundUser = bankUserRepository.findByCredentialsEmail("nonexistent@example.com");

        assertFalse(foundUser.isPresent());
    }

    @Test
    void testFindAll() {
        BankUser user1 = createTestUser("user1@example.com", "User1", "Doe1", BankRole.CLIENT);
        BankUser user2 = createTestUser("user2@example.com", "User2", "Doe2", BankRole.ADVISOR);
        BankUser user3 = createTestUser("user3@example.com", "User3", "Doe3", BankRole.ADMIN);

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
        entityManager.persistAndFlush(user3);

        List<BankUser> allUsers = bankUserRepository.findAll();

        assertNotNull(allUsers);
        assertTrue(allUsers.size() >= 3);
        assertTrue(allUsers.stream().anyMatch(user -> "user1@example.com".equals(user.getCredentials().getEmail())));
        assertTrue(allUsers.stream().anyMatch(user -> "user2@example.com".equals(user.getCredentials().getEmail())));
        assertTrue(allUsers.stream().anyMatch(user -> "user3@example.com".equals(user.getCredentials().getEmail())));
    }

    @Test
    void testFindAllEmpty() {
        List<BankUser> allUsers = bankUserRepository.findAll();

        assertNotNull(allUsers);
        assertTrue(allUsers.isEmpty());
    }

    @Test
    void testUpdateBankUser() {
        BankUser savedUser = entityManager.persistAndFlush(testUser);
        savedUser.getIdentity().setName("Jane");
        savedUser.getIdentity().setSurname("Smith");

        BankUser updatedUser = bankUserRepository.save(savedUser);

        assertEquals("Jane", updatedUser.getIdentity().getName());
        assertEquals("Smith", updatedUser.getIdentity().getSurname());
    }

    @Test
    void testDeleteBankUser() {
        BankUser savedUser = entityManager.persistAndFlush(testUser);
        Long userId = savedUser.getId();

        bankUserRepository.delete(savedUser);
        entityManager.flush();

        Optional<BankUser> deletedUser = bankUserRepository.findById(userId);
        assertFalse(deletedUser.isPresent());
    }

    @Test
    void testFindById() {
        BankUser savedUser = entityManager.persistAndFlush(testUser);
        Long userId = savedUser.getId();

        Optional<BankUser> foundUser = bankUserRepository.findById(userId);

        assertTrue(foundUser.isPresent());
        assertEquals(userId, foundUser.get().getId());
        assertEquals("test@example.com", foundUser.get().getCredentials().getEmail());
    }

    @Test
    void testFindByIdNotFound() {
        Optional<BankUser> foundUser = bankUserRepository.findById(999L);

        assertFalse(foundUser.isPresent());
    }

    @Test
    void testSaveMultipleUsersWithSameEmailShouldFail() {
        BankUser user1 = createTestUser("same@example.com", "User1", "Doe1", BankRole.CLIENT);
        BankUser user2 = createTestUser("same@example.com", "User2", "Doe2", BankRole.ADVISOR);

        BankUser savedUser1 = bankUserRepository.save(user1);
        assertNotNull(savedUser1);

        assertThrows(Exception.class, () -> bankUserRepository.save(user2));
    }

    @Test
    void testSaveMultipleUsersWithDifferentEmails() {
        BankUser user1 = createTestUser("user1@example.com", "User1", "Doe1", BankRole.CLIENT);
        BankUser user2 = createTestUser("user2@example.com", "User2", "Doe2", BankRole.ADVISOR);

        BankUser savedUser1 = bankUserRepository.save(user1);
        BankUser savedUser2 = bankUserRepository.save(user2);

        assertNotNull(savedUser1);
        assertNotNull(savedUser2);
        assertNotEquals(savedUser1.getId(), savedUser2.getId());
        assertEquals("user1@example.com", savedUser1.getCredentials().getEmail());
        assertEquals("user2@example.com", savedUser2.getCredentials().getEmail());
    }

    private BankUser createTestUser(String email, String name, String surname, BankRole role) {
        Credentials creds = new Credentials();
        creds.setEmail(email);
        creds.setPassword("password");

        Identity ident = new Identity();
        ident.setName(name);
        ident.setSurname(surname);

        BankUser user = new BankUser();
        user.setCredentials(creds);
        user.setIdentity(ident);
        user.setBankRole(role);

        return user;
    }
} 