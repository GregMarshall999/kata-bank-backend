package com.exalt_company.kata_bank_api.repository;

import com.exalt_company.kata_bank_api.entity.BankUser;
import com.exalt_company.kata_bank_api.entity.Saving;
import com.exalt_company.kata_bank_api.entity.user_fields.Credentials;
import com.exalt_company.kata_bank_api.entity.user_fields.Identity;
import com.exalt_company.kata_bank_api.enums.BankRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class SavingRepositoryTest {
    @Autowired
    private SavingRepository savingRepository;

    @Autowired
    private BankUserRepository bankUserRepository;

    private BankUser testUser;
    private Saving testSaving;

    @BeforeEach
    void setUp() {
        savingRepository.deleteAll();
        bankUserRepository.deleteAll();

        testUser = new BankUser();
        testUser.setBankRole(BankRole.CLIENT);
        
        Credentials credentials = new Credentials();
        credentials.setEmail("test@example.com");
        credentials.setPassword("password123");
        testUser.setCredentials(credentials);
        
        Identity identity = new Identity();
        identity.setName("John");
        identity.setSurname("Doe");
        testUser.setIdentity(identity);
        
        testUser = bankUserRepository.save(testUser);

        testSaving = new Saving();
        testSaving.setBalance(500.0);
        testSaving.setOwner(testUser);
        testSaving.setMaxBalance(10000.0);
    }

    @Test
    void testSaveSaving() {
        Saving savedSaving = savingRepository.save(testSaving);

        assertNotNull(savedSaving);
        assertNotEquals(0L, savedSaving.getId());
        assertEquals(500.0, savedSaving.getBalance(), 0.001);
        assertEquals(testUser, savedSaving.getOwner());
        assertEquals(10000.0, savedSaving.getMaxBalance(), 0.001);
    }

    @Test
    void testFindByOwner() {
        savingRepository.save(testSaving);

        Optional<Saving> foundSaving = savingRepository.findByOwner(testUser);

        assertTrue(foundSaving.isPresent());
        assertEquals(testUser, foundSaving.get().getOwner());
        assertEquals(500.0, foundSaving.get().getBalance(), 0.001);
        assertEquals(10000.0, foundSaving.get().getMaxBalance(), 0.001);
    }

    @Test
    void testFindByOwnerNotFound() {
        final BankUser otherUser = createTestUser("other@example.com", "Other", "User", BankRole.CLIENT);

        Optional<Saving> foundSaving = savingRepository.findByOwner(otherUser);

        assertFalse(foundSaving.isPresent());
    }

    @Test
    void testFindAll() {
        Saving saving1 = createTestSaving(testUser, 500.0, 10000.0);
        savingRepository.save(saving1);

        final BankUser otherUser = createTestUser("other@example.com", "Other", "User", BankRole.CLIENT);
        Saving saving2 = createTestSaving(otherUser, 2000.0, 5000.0);
        savingRepository.save(saving2);

        List<Saving> allSavings = savingRepository.findAll();

        assertNotNull(allSavings);
        assertEquals(2, allSavings.size());
        assertTrue(allSavings.stream().anyMatch(saving -> saving.getOwner().equals(testUser)));
        assertTrue(allSavings.stream().anyMatch(saving -> saving.getOwner().equals(otherUser)));
    }

    @Test
    void testFindAllEmpty() {
        List<Saving> allSavings = savingRepository.findAll();

        assertNotNull(allSavings);
        assertTrue(allSavings.isEmpty());
    }

    @Test
    void testUpdateSaving() {
        Saving savedSaving = savingRepository.save(testSaving);
        savedSaving.setBalance(1500.0);
        savedSaving.setMaxBalance(15000.0);

        Saving updatedSaving = savingRepository.save(savedSaving);

        assertEquals(1500.0, updatedSaving.getBalance(), 0.001);
        assertEquals(15000.0, updatedSaving.getMaxBalance(), 0.001);
    }

    @Test
    void testDeleteSaving() {
        Saving savedSaving = savingRepository.save(testSaving);
        Long savingId = savedSaving.getId();

        savingRepository.delete(savedSaving);

        Optional<Saving> deletedSaving = savingRepository.findById(savingId);
        assertFalse(deletedSaving.isPresent());
    }

    @Test
    void testFindById() {
        Saving savedSaving = savingRepository.save(testSaving);
        Long savingId = savedSaving.getId();

        Optional<Saving> foundSaving = savingRepository.findById(savingId);

        assertTrue(foundSaving.isPresent());
        assertEquals(savingId, foundSaving.get().getId());
        assertEquals(testUser, foundSaving.get().getOwner());
    }

    @Test
    void testFindByIdNotFound() {
        Optional<Saving> foundSaving = savingRepository.findById(999L);

        assertFalse(foundSaving.isPresent());
    }

    @Test
    void testSavingWithHighBalance() {
        Saving highBalanceSaving = new Saving();
        highBalanceSaving.setBalance(50000.0);
        highBalanceSaving.setOwner(testUser);
        highBalanceSaving.setMaxBalance(100000.0);

        Saving savedSaving = savingRepository.save(highBalanceSaving);

        assertNotNull(savedSaving);
        assertEquals(50000.0, savedSaving.getBalance(), 0.001);
        assertEquals(100000.0, savedSaving.getMaxBalance(), 0.001);
    }

    @Test
    void testSavingDataIntegrity() {
        Saving savedSaving = savingRepository.save(testSaving);

        Saving foundSaving = savingRepository.findById(savedSaving.getId()).orElse(null);

        assertNotNull(foundSaving);
        assertEquals(500.0, foundSaving.getBalance(), 0.001);
        assertEquals(10000.0, foundSaving.getMaxBalance(), 0.001);
        assertEquals(testUser.getId(), foundSaving.getOwner().getId());
    }

    @Test
    void testSavingTimestamps() {
        Saving savedSaving = savingRepository.save(testSaving);

        assertNotNull(savedSaving.getCreatedAt());
        assertNotNull(savedSaving.getUpdatedAt());
    }

    @Test
    void testSavingWithZeroBalance() {
        Saving zeroSaving = new Saving();
        zeroSaving.setBalance(0.0);
        zeroSaving.setOwner(testUser);
        zeroSaving.setMaxBalance(5000.0);

        Saving savedSaving = savingRepository.save(zeroSaving);

        assertNotNull(savedSaving);
        assertEquals(0.0, savedSaving.getBalance(), 0.001);
        assertEquals(5000.0, savedSaving.getMaxBalance(), 0.001);
    }

    @Test
    void testSavingWithZeroMaxBalance() {
        Saving zeroMaxSaving = new Saving();
        zeroMaxSaving.setBalance(100.0);
        zeroMaxSaving.setOwner(testUser);
        zeroMaxSaving.setMaxBalance(0.0);

        Saving savedSaving = savingRepository.save(zeroMaxSaving);

        assertNotNull(savedSaving);
        assertEquals(100.0, savedSaving.getBalance(), 0.001);
        assertEquals(0.0, savedSaving.getMaxBalance(), 0.001);
    }

    @Test
    void testSavingWithNegativeBalance() {
        Saving negativeSaving = new Saving();
        negativeSaving.setBalance(-50.0);
        negativeSaving.setOwner(testUser);
        negativeSaving.setMaxBalance(1000.0);

        Saving savedSaving = savingRepository.save(negativeSaving);

        assertNotNull(savedSaving);
        assertEquals(-50.0, savedSaving.getBalance(), 0.001);
        assertEquals(1000.0, savedSaving.getMaxBalance(), 0.001);
    }

    @Test
    void testSavingWithNegativeMaxBalance() {
        Saving negativeMaxSaving = new Saving();
        negativeMaxSaving.setBalance(100.0);
        negativeMaxSaving.setOwner(testUser);
        negativeMaxSaving.setMaxBalance(-100.0);

        Saving savedSaving = savingRepository.save(negativeMaxSaving);

        assertNotNull(savedSaving);
        assertEquals(100.0, savedSaving.getBalance(), 0.001);
        assertEquals(-100.0, savedSaving.getMaxBalance(), 0.001);
    }

    @Test
    void testSavingWithVeryLargeValues() {
        Saving largeSaving = new Saving();
        largeSaving.setBalance(Double.MAX_VALUE);
        largeSaving.setOwner(testUser);
        largeSaving.setMaxBalance(Double.MAX_VALUE);

        Saving savedSaving = savingRepository.save(largeSaving);

        assertNotNull(savedSaving);
        assertEquals(Double.MAX_VALUE, savedSaving.getBalance(), 0.001);
        assertEquals(Double.MAX_VALUE, savedSaving.getMaxBalance(), 0.001);
    }

    private BankUser createTestUser(String email, String name, String surname, BankRole role) {
        BankUser user = new BankUser();
        user.setBankRole(role);
        
        Credentials credentials = new Credentials();
        credentials.setEmail(email);
        credentials.setPassword("password123");
        user.setCredentials(credentials);
        
        Identity identity = new Identity();
        identity.setName(name);
        identity.setSurname(surname);
        user.setIdentity(identity);
        
        return bankUserRepository.save(user);
    }

    private Saving createTestSaving(BankUser owner, double balance, double maxBalance) {
        Saving saving = new Saving();
        saving.setBalance(balance);
        saving.setOwner(owner);
        saving.setMaxBalance(maxBalance);
        return saving;
    }
}
