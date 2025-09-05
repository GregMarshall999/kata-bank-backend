package com.exalt_company.kata_bank_api.repository;

import com.exalt_company.kata_bank_api.entity.BankUser;
import com.exalt_company.kata_bank_api.entity.Fund;
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
class FundRepositoryTest {
    @Autowired
    private FundRepository fundRepository;

    @Autowired
    private BankUserRepository bankUserRepository;

    private BankUser testUser;
    private Fund testFund;

    @BeforeEach
    void setUp() {
        fundRepository.deleteAll();
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

        testFund = new Fund();
        testFund.setBalance(1000.0);
        testFund.setOwner(testUser);
        testFund.setCanOverdraw(false);
        testFund.setMaxOverdraw(0.0);
    }

    @Test
    void testSaveFund() {
        Fund savedFund = fundRepository.save(testFund);

        assertNotNull(savedFund);
        assertNotEquals(0L, savedFund.getId());
        assertEquals(1000.0, savedFund.getBalance(), 0.001);
        assertEquals(testUser, savedFund.getOwner());
        assertEquals(false, savedFund.canOverdraw());
        assertEquals(0.0, savedFund.getMaxOverdraw(), 0.001);
    }

    @Test
    void testFindByOwner() {
        fundRepository.save(testFund);

        Optional<Fund> foundFund = fundRepository.findByOwner(testUser);

        assertTrue(foundFund.isPresent());
        assertEquals(testUser, foundFund.get().getOwner());
        assertEquals(1000.0, foundFund.get().getBalance(), 0.001);
    }

    @Test
    void testFindByOwnerNotFound() {
        final BankUser otherUser = createTestUser("other@example.com", "Other", "User", BankRole.CLIENT);

        Optional<Fund> foundFund = fundRepository.findByOwner(otherUser);

        assertFalse(foundFund.isPresent());
    }

    @Test
    void testFindAll() {
        Fund fund1 = createTestFund(testUser, 1000.0, false, 0.0);
        fundRepository.save(fund1);

        final BankUser otherUser = createTestUser("other@example.com", "Other", "User", BankRole.CLIENT);
        Fund fund2 = createTestFund(otherUser, 2000.0, true, 500.0);
        fundRepository.save(fund2);

        List<Fund> allFunds = fundRepository.findAll();

        assertNotNull(allFunds);
        assertEquals(2, allFunds.size());
        assertTrue(allFunds.stream().anyMatch(fund -> fund.getOwner().equals(testUser)));
        assertTrue(allFunds.stream().anyMatch(fund -> fund.getOwner().equals(otherUser)));
    }

    @Test
    void testFindAllEmpty() {
        List<Fund> allFunds = fundRepository.findAll();

        assertNotNull(allFunds);
        assertTrue(allFunds.isEmpty());
    }

    @Test
    void testUpdateFund() {
        Fund savedFund = fundRepository.save(testFund);
        savedFund.setBalance(1500.0);
        savedFund.setCanOverdraw(true);
        savedFund.setMaxOverdraw(200.0);

        Fund updatedFund = fundRepository.save(savedFund);

        assertEquals(1500.0, updatedFund.getBalance(), 0.001);
        assertEquals(true, updatedFund.canOverdraw());
        assertEquals(200.0, updatedFund.getMaxOverdraw(), 0.001);
    }

    @Test
    void testDeleteFund() {
        Fund savedFund = fundRepository.save(testFund);
        Long fundId = savedFund.getId();

        fundRepository.delete(savedFund);

        Optional<Fund> deletedFund = fundRepository.findById(fundId);
        assertFalse(deletedFund.isPresent());
    }

    @Test
    void testFindById() {
        Fund savedFund = fundRepository.save(testFund);
        Long fundId = savedFund.getId();

        Optional<Fund> foundFund = fundRepository.findById(fundId);

        assertTrue(foundFund.isPresent());
        assertEquals(fundId, foundFund.get().getId());
        assertEquals(testUser, foundFund.get().getOwner());
    }

    @Test
    void testFindByIdNotFound() {
        Optional<Fund> foundFund = fundRepository.findById(999L);

        assertFalse(foundFund.isPresent());
    }

    @Test
    void testFundWithOverdrawCapabilities() {
        Fund overdrawFund = new Fund();
        overdrawFund.setBalance(500.0);
        overdrawFund.setOwner(testUser);
        overdrawFund.setCanOverdraw(true);
        overdrawFund.setMaxOverdraw(1000.0);

        Fund savedFund = fundRepository.save(overdrawFund);

        assertNotNull(savedFund);
        assertEquals(true, savedFund.canOverdraw());
        assertEquals(1000.0, savedFund.getMaxOverdraw(), 0.001);
        assertEquals(500.0, savedFund.getBalance(), 0.001);
    }

    @Test
    void testFundDataIntegrity() {
        Fund savedFund = fundRepository.save(testFund);

        Fund foundFund = fundRepository.findById(savedFund.getId()).orElse(null);

        assertNotNull(foundFund);
        assertEquals(1000.0, foundFund.getBalance(), 0.001);
        assertEquals(false, foundFund.canOverdraw());
        assertEquals(0.0, foundFund.getMaxOverdraw(), 0.001);
        assertEquals(testUser.getId(), foundFund.getOwner().getId());
    }

    @Test
    void testFundTimestamps() {
        Fund savedFund = fundRepository.save(testFund);

        assertNotNull(savedFund.getCreatedAt());
        assertNotNull(savedFund.getUpdatedAt());
    }

    @Test
    void testFundWithNegativeBalance() {
        Fund negativeFund = new Fund();
        negativeFund.setBalance(-100.0);
        negativeFund.setOwner(testUser);
        negativeFund.setCanOverdraw(true);
        negativeFund.setMaxOverdraw(200.0);

        Fund savedFund = fundRepository.save(negativeFund);

        assertNotNull(savedFund);
        assertEquals(-100.0, savedFund.getBalance(), 0.001);
        assertEquals(true, savedFund.canOverdraw());
        assertEquals(200.0, savedFund.getMaxOverdraw(), 0.001);
    }

    @Test
    void testFundWithZeroBalance() {
        Fund zeroFund = new Fund();
        zeroFund.setBalance(0.0);
        zeroFund.setOwner(testUser);
        zeroFund.setCanOverdraw(false);
        zeroFund.setMaxOverdraw(0.0);

        Fund savedFund = fundRepository.save(zeroFund);

        assertNotNull(savedFund);
        assertEquals(0.0, savedFund.getBalance(), 0.001);
        assertEquals(false, savedFund.canOverdraw());
        assertEquals(0.0, savedFund.getMaxOverdraw(), 0.001);
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

    private Fund createTestFund(BankUser owner, double balance, boolean canOverdraw, double maxOverdraw) {
        Fund fund = new Fund();
        fund.setBalance(balance);
        fund.setOwner(owner);
        fund.setCanOverdraw(canOverdraw);
        fund.setMaxOverdraw(maxOverdraw);
        return fund;
    }
}
