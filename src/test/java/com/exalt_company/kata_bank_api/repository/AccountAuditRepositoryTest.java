package com.exalt_company.kata_bank_api.repository;

import com.exalt_company.kata_bank_api.entity.AccountAudit;
import com.exalt_company.kata_bank_api.entity.BankUser;
import com.exalt_company.kata_bank_api.entity.Fund;
import com.exalt_company.kata_bank_api.entity.Saving;
import com.exalt_company.kata_bank_api.entity.user_fields.Credentials;
import com.exalt_company.kata_bank_api.entity.user_fields.Identity;
import com.exalt_company.kata_bank_api.enums.AuditOperation;
import com.exalt_company.kata_bank_api.enums.BankRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class AccountAuditRepositoryTest {
    @Autowired
    private AccountAuditRepository auditRepository;

    @Autowired
    private BankUserRepository bankUserRepository;

    @Autowired
    private FundRepository fundRepository;

    @Autowired
    private SavingRepository savingRepository;

    private BankUser testUser;
    private Fund testFund;
    private Saving testSaving;
    private AccountAudit testAudit;

    @BeforeEach
    void setUp() {
        auditRepository.deleteAll();
        fundRepository.deleteAll();
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

        testFund = new Fund();
        testFund.setBalance(1000.0);
        testFund.setOwner(testUser);
        testFund.setCanOverdraw(false);
        testFund.setMaxOverdraw(0.0);
        testFund = fundRepository.save(testFund);

        testSaving = new Saving();
        testSaving.setBalance(500.0);
        testSaving.setOwner(testUser);
        testSaving.setMaxBalance(10000.0);
        testSaving = savingRepository.save(testSaving);

        testAudit = new AccountAudit();
        testAudit.setOperation(AuditOperation.DEPOSIT);
        testAudit.setAmount(100.0);
        testAudit.setBalanceBefore(900.0);
        testAudit.setBalanceAfter(1000.0);
        testAudit.setRequestingUser(testUser);
        testAudit.setUserFund(testFund);
        testAudit.setUserSaving(null);
    }

    @Test
    void testSaveAndFindById() {
        AccountAudit savedAudit = auditRepository.save(testAudit);
        AccountAudit foundAudit = auditRepository.findById(savedAudit.getId()).orElse(null);

        assertNotNull(foundAudit);
        assertEquals(savedAudit.getId(), foundAudit.getId());
        assertEquals(AuditOperation.DEPOSIT, foundAudit.getOperation());
        assertEquals(100.0, foundAudit.getAmount());
        assertEquals(testUser, foundAudit.getRequestingUser());
        assertEquals(testFund, foundAudit.getUserFund());
        assertNull(foundAudit.getUserSaving());
    }

    @Test
    void testFindAll() {
        auditRepository.save(testAudit);
        
        AccountAudit secondAudit = new AccountAudit();
        secondAudit.setOperation(AuditOperation.WITHDRAW);
        secondAudit.setAmount(50.0);
        secondAudit.setBalanceBefore(1000.0);
        secondAudit.setBalanceAfter(950.0);
        secondAudit.setRequestingUser(testUser);
        secondAudit.setUserFund(testFund);
        secondAudit.setUserSaving(null);
        auditRepository.save(secondAudit);

        List<AccountAudit> allAudits = auditRepository.findAll();

        assertEquals(2, allAudits.size());
        assertTrue(allAudits.stream().anyMatch(audit -> audit.getOperation() == AuditOperation.DEPOSIT));
        assertTrue(allAudits.stream().anyMatch(audit -> audit.getOperation() == AuditOperation.WITHDRAW));
    }

    @Test
    void testDelete() {
        AccountAudit savedAudit = auditRepository.save(testAudit);
        assertEquals(1, auditRepository.count());

        auditRepository.delete(savedAudit);

        assertEquals(0, auditRepository.count());
        assertFalse(auditRepository.findById(savedAudit.getId()).isPresent());
    }

    @Test
    void testFindByUserFundOwnerCurrentMonth() {
        auditRepository.save(testAudit);
        
        BankUser otherUser = new BankUser();
        otherUser.setBankRole(BankRole.CLIENT);
        Identity identity = new Identity();
        identity.setName("user");
        identity.setSurname("other");
        Credentials otherCredentials = new Credentials();
        otherCredentials.setEmail("other@example.com");
        otherCredentials.setPassword("password123");
        otherUser.setIdentity(identity);
        otherUser.setCredentials(otherCredentials);
        otherUser = bankUserRepository.save(otherUser);
        
        Fund otherFund = new Fund();
        otherFund.setBalance(2000.0);
        otherFund.setOwner(otherUser);
        otherFund = fundRepository.save(otherFund);
        
        AccountAudit otherAudit = new AccountAudit();
        otherAudit.setOperation(AuditOperation.WITHDRAW);
        otherAudit.setAmount(200.0);
        otherAudit.setBalanceBefore(2000.0);
        otherAudit.setBalanceAfter(1800.0);
        otherAudit.setRequestingUser(otherUser);
        otherAudit.setUserFund(otherFund);
        otherAudit.setUserSaving(null);
        auditRepository.save(otherAudit);

        Pageable pageable = PageRequest.of(0, 10);
        Page<AccountAudit> fundAudits = auditRepository.findByUserFundOwnerCurrentMonth(testUser, pageable);

        assertEquals(1, fundAudits.getTotalElements());
        assertEquals(AuditOperation.DEPOSIT, fundAudits.getContent().get(0).getOperation());
        assertEquals(testUser, fundAudits.getContent().get(0).getRequestingUser());
    }

    @Test
    void testFindByUserSavingOwnerCurrentMonth() {
        AccountAudit savingAudit = new AccountAudit();
        savingAudit.setOperation(AuditOperation.OPEN);
        savingAudit.setAmount(0.0);
        savingAudit.setBalanceBefore(0.0);
        savingAudit.setBalanceAfter(0.0);
        savingAudit.setRequestingUser(testUser);
        savingAudit.setUserFund(null);
        savingAudit.setUserSaving(testSaving);
        auditRepository.save(savingAudit);
        
        BankUser otherUser = new BankUser();
        otherUser.setBankRole(BankRole.CLIENT);
        Identity identity = new Identity();
        identity.setName("user");
        identity.setSurname("other");
        Credentials otherCredentials = new Credentials();
        otherCredentials.setEmail("other@example.com");
        otherCredentials.setPassword("password123");
        otherUser.setIdentity(identity);
        otherUser.setCredentials(otherCredentials);
        otherUser = bankUserRepository.save(otherUser);
        
        Saving otherSaving = new Saving();
        otherSaving.setBalance(1000.0);
        otherSaving.setOwner(otherUser);
        otherSaving.setMaxBalance(5000.0);
        otherSaving = savingRepository.save(otherSaving);
        
        AccountAudit otherAudit = new AccountAudit();
        otherAudit.setOperation(AuditOperation.DEPOSIT);
        otherAudit.setAmount(500.0);
        otherAudit.setBalanceBefore(1000.0);
        otherAudit.setBalanceAfter(1500.0);
        otherAudit.setRequestingUser(otherUser);
        otherAudit.setUserFund(null);
        otherAudit.setUserSaving(otherSaving);
        auditRepository.save(otherAudit);

        Pageable pageable = PageRequest.of(0, 10);
        Page<AccountAudit> savingAudits = auditRepository.findByUserSavingOwnerCurrentMonth(testUser, pageable);

        assertEquals(1, savingAudits.getTotalElements());
        assertEquals(AuditOperation.OPEN, savingAudits.getContent().get(0).getOperation());
        assertEquals(testUser, savingAudits.getContent().get(0).getRequestingUser());
    }

    @Test
    void testPagination() {
        for (int i = 0; i < 25; i++) {
            AccountAudit audit = new AccountAudit();
            audit.setOperation(AuditOperation.DEPOSIT);
            audit.setAmount(10.0 + i);
            audit.setBalanceBefore(1000.0 + i);
            audit.setBalanceAfter(1010.0 + i);
            audit.setRequestingUser(testUser);
            audit.setUserFund(testFund);
            audit.setUserSaving(null);
            auditRepository.save(audit);
        }

        Pageable firstPage = PageRequest.of(0, 10);
        Page<AccountAudit> firstPageResult = auditRepository.findByUserFundOwnerCurrentMonth(testUser, firstPage);

        Pageable secondPage = PageRequest.of(1, 10);
        Page<AccountAudit> secondPageResult = auditRepository.findByUserFundOwnerCurrentMonth(testUser, secondPage);

        assertEquals(25, firstPageResult.getTotalElements());
        assertEquals(10, firstPageResult.getContent().size());
        assertEquals(0, firstPageResult.getNumber());
        assertEquals(10, firstPageResult.getSize());
        assertEquals(3, firstPageResult.getTotalPages());

        assertEquals(25, secondPageResult.getTotalElements());
        assertEquals(10, secondPageResult.getContent().size());
        assertEquals(1, secondPageResult.getNumber());
        assertEquals(10, secondPageResult.getSize());
        assertEquals(3, secondPageResult.getTotalPages());
    }

    @Test
    void testCurrentMonthFiltering() {
        auditRepository.save(testAudit);
        
        AccountAudit oldAudit = new AccountAudit();
        oldAudit.setOperation(AuditOperation.WITHDRAW);
        oldAudit.setAmount(50.0);
        oldAudit.setBalanceBefore(1000.0);
        oldAudit.setBalanceAfter(950.0);
        oldAudit.setRequestingUser(testUser);
        oldAudit.setUserFund(testFund);
        oldAudit.setUserSaving(null);

        auditRepository.save(oldAudit);

        Pageable pageable = PageRequest.of(0, 10);
        Page<AccountAudit> currentMonthAudits = auditRepository.findByUserFundOwnerCurrentMonth(testUser, pageable);

        assertTrue(currentMonthAudits.getTotalElements() >= 1);
    }

    @Test
    void testAuditDataIntegrity() {
        AccountAudit savedAudit = auditRepository.save(testAudit);

        AccountAudit foundAudit = auditRepository.findById(savedAudit.getId()).orElse(null);

        assertNotNull(foundAudit);
        assertEquals(AuditOperation.DEPOSIT, foundAudit.getOperation());
        assertEquals(100.0, foundAudit.getAmount(), 0.001);
        assertEquals(900.0, foundAudit.getBalanceBefore(), 0.001);
        assertEquals(1000.0, foundAudit.getBalanceAfter(), 0.001);
        assertEquals(testUser.getId(), foundAudit.getRequestingUser().getId());
        assertEquals(testFund.getId(), foundAudit.getUserFund().getId());
        assertNull(foundAudit.getUserSaving());
    }

    @Test
    void testAuditTimestamps() {
        LocalDateTime beforeSave = LocalDateTime.now();
        
        AccountAudit savedAudit = auditRepository.save(testAudit);
        
        LocalDateTime afterSave = LocalDateTime.now();

        assertNotNull(savedAudit.getCreatedAt());
        assertTrue(savedAudit.getCreatedAt().isAfter(beforeSave) || savedAudit.getCreatedAt().isEqual(beforeSave));
        assertTrue(savedAudit.getCreatedAt().isBefore(afterSave) || savedAudit.getCreatedAt().isEqual(afterSave));
    }

    @Test
    void testMultipleOperationsForSameUser() {
        AuditOperation[] operations = {
            AuditOperation.OPEN, AuditOperation.DEPOSIT, AuditOperation.WITHDRAW,
            AuditOperation.OVERDRAW_REQUEST, AuditOperation.OVERDRAW_CANCEL, AuditOperation.CLOSE
        };

        for (int i = 0; i < operations.length; i++) {
            AccountAudit audit = new AccountAudit();
            audit.setOperation(operations[i]);
            audit.setAmount(100.0 + i);
            audit.setBalanceBefore(1000.0 + i);
            audit.setBalanceAfter(1100.0 + i);
            audit.setRequestingUser(testUser);
            audit.setUserFund(testFund);
            audit.setUserSaving(null);
            auditRepository.save(audit);
        }

        Pageable pageable = PageRequest.of(0, 20);
        Page<AccountAudit> userAudits = auditRepository.findByUserFundOwnerCurrentMonth(testUser, pageable);

        assertEquals(operations.length, userAudits.getTotalElements());
        
        List<AuditOperation> foundOperations = userAudits.getContent().stream()
            .map(AccountAudit::getOperation)
            .toList();
        
        for (AuditOperation operation : operations) {
            assertTrue(foundOperations.contains(operation));
        }
    }

    @Test
    void testEmptyResult() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AccountAudit> emptyResult = auditRepository.findByUserFundOwnerCurrentMonth(testUser, pageable);

        assertEquals(0, emptyResult.getTotalElements());
        assertEquals(0, emptyResult.getContent().size());
        assertEquals(0, emptyResult.getTotalPages());
    }

    @Test
    void testFindAllByUserSaving() {
        AccountAudit savingAudit1 = new AccountAudit();
        savingAudit1.setOperation(AuditOperation.OPEN);
        savingAudit1.setAmount(0.0);
        savingAudit1.setBalanceBefore(0.0);
        savingAudit1.setBalanceAfter(0.0);
        savingAudit1.setRequestingUser(testUser);
        savingAudit1.setUserFund(null);
        savingAudit1.setUserSaving(testSaving);
        auditRepository.save(savingAudit1);

        AccountAudit savingAudit2 = new AccountAudit();
        savingAudit2.setOperation(AuditOperation.DEPOSIT);
        savingAudit2.setAmount(200.0);
        savingAudit2.setBalanceBefore(500.0);
        savingAudit2.setBalanceAfter(700.0);
        savingAudit2.setRequestingUser(testUser);
        savingAudit2.setUserFund(null);
        savingAudit2.setUserSaving(testSaving);
        auditRepository.save(savingAudit2);

        Saving otherSaving = new Saving();
        otherSaving.setBalance(1000.0);
        otherSaving.setOwner(testUser);
        otherSaving.setMaxBalance(5000.0);
        otherSaving = savingRepository.save(otherSaving);

        AccountAudit otherSavingAudit = new AccountAudit();
        otherSavingAudit.setOperation(AuditOperation.DEPOSIT);
        otherSavingAudit.setAmount(100.0);
        otherSavingAudit.setBalanceBefore(1000.0);
        otherSavingAudit.setBalanceAfter(1100.0);
        otherSavingAudit.setRequestingUser(testUser);
        otherSavingAudit.setUserFund(null);
        otherSavingAudit.setUserSaving(otherSaving);
        auditRepository.save(otherSavingAudit);

        List<AccountAudit> testSavingAudits = auditRepository.findAllByUserSaving(testSaving);

        assertEquals(2, testSavingAudits.size());
        assertTrue(testSavingAudits.stream().allMatch(audit -> audit.getUserSaving().equals(testSaving)));
        assertTrue(testSavingAudits.stream().anyMatch(audit -> audit.getOperation() == AuditOperation.OPEN));
        assertTrue(testSavingAudits.stream().anyMatch(audit -> audit.getOperation() == AuditOperation.DEPOSIT));
    }

    @Test
    void testFindAllByUserSavingEmpty() {
        List<AccountAudit> emptyResult = auditRepository.findAllByUserSaving(testSaving);
        assertEquals(0, emptyResult.size());
    }
}
