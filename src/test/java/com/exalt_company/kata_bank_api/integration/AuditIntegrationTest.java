package com.exalt_company.kata_bank_api.integration;

import com.exalt_company.kata_bank_api.dto.statement.AccountStatementDto;
import com.exalt_company.kata_bank_api.dto.statement.OperationDto;
import com.exalt_company.kata_bank_api.entity.AccountAudit;
import com.exalt_company.kata_bank_api.entity.BankUser;
import com.exalt_company.kata_bank_api.entity.Fund;
import com.exalt_company.kata_bank_api.entity.Saving;
import com.exalt_company.kata_bank_api.entity.user_fields.Credentials;
import com.exalt_company.kata_bank_api.entity.user_fields.Identity;
import com.exalt_company.kata_bank_api.enums.AccountType;
import com.exalt_company.kata_bank_api.enums.AuditOperation;
import com.exalt_company.kata_bank_api.enums.BankRole;
import com.exalt_company.kata_bank_api.exception.AuditException;
import com.exalt_company.kata_bank_api.repository.AccountAuditRepository;
import com.exalt_company.kata_bank_api.repository.BankUserRepository;
import com.exalt_company.kata_bank_api.repository.FundRepository;
import com.exalt_company.kata_bank_api.repository.SavingRepository;
import com.exalt_company.kata_bank_api.security.JwtService;
import com.exalt_company.kata_bank_api.service.IAuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class AuditIntegrationTest {
    @Autowired
    private IAuditService auditService;

    @Autowired
    private AccountAuditRepository auditRepository;

    @Autowired
    private BankUserRepository bankUserRepository;

    @Autowired
    private FundRepository fundRepository;

    @Autowired
    private SavingRepository savingRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;


    private MockMvc mockMvc;
    private BankUser testUser;
    private Fund testFund;
    private Saving testSaving;
    private String validToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        auditRepository.deleteAll();
        fundRepository.deleteAll();
        savingRepository.deleteAll();
        bankUserRepository.deleteAll();

        testUser = new BankUser();
        testUser.setBankRole(BankRole.CLIENT);
        
        Credentials credentials = new Credentials();
        credentials.setEmail("test@example.com");
        credentials.setPassword(passwordEncoder.encode("password123"));
        testUser.setCredentials(credentials);
        
        Identity identity = new Identity();
        identity.setName("John");
        identity.setSurname("Doe");
        testUser.setIdentity(identity);
        
        testUser = bankUserRepository.save(testUser);
        validToken = jwtService.generateToken(testUser, testUser.getId(), testUser.getBankRole());

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
    }

    @Test
    void testRecordAudit_CompleteFlow() {
        AuditOperation operation = AuditOperation.DEPOSIT;
        double amount = 100.0;
        double balanceBefore = 1000.0;
        double balanceAfter = 1100.0;

        auditService.recordAudit(operation, amount, balanceBefore, balanceAfter, testUser, testFund, null);

        List<AccountAudit> audits = auditRepository.findAll();
        assertEquals(1, audits.size());
        
        AccountAudit audit = audits.get(0);
        assertEquals(operation, audit.getOperation());
        assertEquals(amount, audit.getAmount());
        assertEquals(balanceBefore, audit.getBalanceBefore());
        assertEquals(balanceAfter, audit.getBalanceAfter());
        assertEquals(testUser, audit.getRequestingUser());
        assertEquals(testFund, audit.getUserFund());
        assertNull(audit.getUserSaving());
        assertNotNull(audit.getCreatedAt());
    }

    @Test
    void testRecordAudit_WithSaving() {
        AuditOperation operation = AuditOperation.WITHDRAW;
        double amount = 50.0;
        double balanceBefore = 500.0;
        double balanceAfter = 450.0;

        auditService.recordAudit(operation, amount, balanceBefore, balanceAfter, testUser, null, testSaving);

        List<AccountAudit> audits = auditRepository.findAll();
        assertEquals(1, audits.size());
        
        AccountAudit audit = audits.get(0);
        assertEquals(operation, audit.getOperation());
        assertEquals(amount, audit.getAmount());
        assertEquals(balanceBefore, audit.getBalanceBefore());
        assertEquals(balanceAfter, audit.getBalanceAfter());
        assertEquals(testUser, audit.getRequestingUser());
        assertNull(audit.getUserFund());
        assertEquals(testSaving, audit.getUserSaving());
    }

    @Test
    void testRequestStatement_FundAccount() throws AuditException {
        createAuditRecords(AuditOperation.DEPOSIT, 100.0, 1000.0, 1100.0, testUser, testFund, null);
        createAuditRecords(AuditOperation.WITHDRAW, 50.0, 1100.0, 1050.0, testUser, testFund, null);

        AccountStatementDto statement = auditService.requestStatement("FUND", testUser.getId(), 0, 10).getBody();

        assertNotNull(statement);
        assertEquals(AccountType.FUND, statement.getAccountType());
        assertEquals(1000.0, statement.getAccountBalance());
        assertEquals(2, statement.getOperations().size());
        assertEquals(0, statement.getOperationsPage());
        assertEquals(10, statement.getOperationsSize());
        assertEquals(1, statement.getTotalOperationsPage());
        
        List<AuditOperation> operations = statement.getOperations().stream()
            .map(OperationDto::getOperation)
            .toList();
        assertTrue(operations.contains(AuditOperation.DEPOSIT));
        assertTrue(operations.contains(AuditOperation.WITHDRAW));
    }

    @Test
    void testRequestStatement_SavingAccount() throws AuditException {
        createAuditRecords(AuditOperation.OPEN, 500.0, 0.0, 500.0, testUser, null, testSaving);
        testSaving.setBalance(testSaving.getBalance() + 200.0);
        createAuditRecords(AuditOperation.DEPOSIT, 200.0, 0.0, 200.0, testUser, null, testSaving);

        AccountStatementDto statement = auditService.requestStatement("SAVING", testUser.getId(), 0, 10).getBody();

        assertNotNull(statement);
        assertEquals(AccountType.SAVING, statement.getAccountType());
        assertEquals(700.0, statement.getAccountBalance());
        assertEquals(2, statement.getOperations().size());
        
        List<AuditOperation> operations = statement.getOperations().stream()
            .map(OperationDto::getOperation)
            .toList();
        assertTrue(operations.contains(AuditOperation.OPEN));
        assertTrue(operations.contains(AuditOperation.DEPOSIT));
    }

    @Test
    void testRequestStatement_Pagination() throws AuditException {
        for (int i = 0; i < 25; i++) {
            createAuditRecords(AuditOperation.DEPOSIT, 10.0, 1000.0 + i * 10, 1010.0 + i * 10, testUser, testFund, null);
        }

        AccountStatementDto firstPage = auditService.requestStatement("FUND", testUser.getId(), 0, 10).getBody();
        
        AccountStatementDto secondPage = auditService.requestStatement("FUND", testUser.getId(), 1, 10).getBody();

        assertNotNull(firstPage);
        assertEquals(0, firstPage.getOperationsPage());
        assertEquals(10, firstPage.getOperationsSize());
        assertEquals(3, firstPage.getTotalOperationsPage());
        assertEquals(10, firstPage.getOperations().size());
        
        assertNotNull(secondPage);
        assertEquals(1, secondPage.getOperationsPage());
        assertEquals(10, secondPage.getOperationsSize());
        assertEquals(3, secondPage.getTotalOperationsPage());
        assertEquals(10, secondPage.getOperations().size());
    }

    @Test
    void testRequestStatement_EmptyResult() throws AuditException {
        AccountStatementDto statement = auditService.requestStatement("FUND", testUser.getId(), 0, 10).getBody();

        assertNotNull(statement);
        assertEquals(AccountType.FUND, statement.getAccountType());
        assertEquals(1000.0, statement.getAccountBalance());
        assertEquals(0, statement.getOperations().size());
        assertEquals(0, statement.getOperationsPage());
        assertEquals(10, statement.getOperationsSize());
        assertEquals(0, statement.getTotalOperationsPage());
    }

    @Test
    void testRequestStatement_UserNotFound() {
        assertThrows(AuditException.class, () ->
            auditService.requestStatement("FUND", 999L, 0, 10));
    }

    @Test
    void testRequestStatement_InvalidAccountType() {
        assertThrows(AuditException.class, () ->
            auditService.requestStatement("INVALID", testUser.getId(), 0, 10));
    }

    @Test
    void testMultipleAuditOperations() {
        AuditOperation[] operations = {
            AuditOperation.OPEN, AuditOperation.DEPOSIT, AuditOperation.WITHDRAW,
            AuditOperation.OVERDRAW_REQUEST, AuditOperation.OVERDRAW_CANCEL, AuditOperation.CLOSE
        };

        for (int i = 0; i < operations.length; i++) {
            auditService.recordAudit(operations[i], 100.0 + i, 1000.0 + i, 1100.0 + i, testUser, testFund, null);
        }

        List<AccountAudit> audits = auditRepository.findAll();
        assertEquals(operations.length, audits.size());
        
        for (int i = 0; i < operations.length; i++) {
            assertEquals(operations[i], audits.get(i).getOperation());
            assertEquals(100.0 + i, audits.get(i).getAmount());
        }
    }

    @Test
    void testAuditDataIntegrity() {
        double amount = 123.45;
        double balanceBefore = 1000.00;
        double balanceAfter = 1123.45;

        auditService.recordAudit(AuditOperation.DEPOSIT, amount, balanceBefore, balanceAfter, testUser, testFund, null);

        AccountAudit audit = auditRepository.findAll().get(0);
        assertEquals(amount, audit.getAmount(), 0.001);
        assertEquals(balanceBefore, audit.getBalanceBefore(), 0.001);
        assertEquals(balanceAfter, audit.getBalanceAfter(), 0.001);
        
        assertNotNull(audit.getRequestingUser());
        assertEquals(testUser.getId(), audit.getRequestingUser().getId());
        assertNotNull(audit.getUserFund());
        assertEquals(testFund.getId(), audit.getUserFund().getId());
    }

    @Test
    void testAuditTimestamps() {
        LocalDateTime beforeCreation = LocalDateTime.now();
        
        auditService.recordAudit(AuditOperation.DEPOSIT, 100.0, 1000.0, 1100.0, testUser, testFund, null);
        
        LocalDateTime afterCreation = LocalDateTime.now();
        
        AccountAudit audit = auditRepository.findAll().get(0);
        assertNotNull(audit.getCreatedAt());
        assertTrue(audit.getCreatedAt().isAfter(beforeCreation) || audit.getCreatedAt().isEqual(beforeCreation));
        assertTrue(audit.getCreatedAt().isBefore(afterCreation) || audit.getCreatedAt().isEqual(afterCreation));
    }

    @Test
    void testRequestStatementViaWeb_FundAccount() throws Exception {
        createAuditRecords(AuditOperation.DEPOSIT, 100.0, 1000.0, 1100.0, testUser, testFund, null);
        createAuditRecords(AuditOperation.WITHDRAW, 50.0, 1100.0, 1050.0, testUser, testFund, null);

        mockMvc.perform(get("/api/audit-account/FUND/{ownerId}/{page}/{size}", testUser.getId(), 0, 10)
                        .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountType").value("FUND"))
                .andExpect(jsonPath("$.accountBalance").value(1000.0))
                .andExpect(jsonPath("$.operations").isArray())
                .andExpect(jsonPath("$.operations.length()").value(2))
                .andExpect(jsonPath("$.operationsPage").value(0))
                .andExpect(jsonPath("$.operationsSize").value(10))
                .andExpect(jsonPath("$.totalOperationsPage").value(1));
    }

    @Test
    void testRequestStatementViaWeb_SavingAccount() throws Exception {
        createAuditRecords(AuditOperation.OPEN, 500.0, 0.0, 500.0, testUser, null, testSaving);

        testSaving.setBalance(700);
        savingRepository.save(testSaving);

        createAuditRecords(AuditOperation.DEPOSIT, 200.0, 500.0, 700.0, testUser, null, testSaving);

        mockMvc.perform(get("/api/audit-account/SAVING/{ownerId}/{page}/{size}", testUser.getId(), 0, 10)
                        .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountType").value("SAVING"))
                .andExpect(jsonPath("$.accountBalance").value(700.0))
                .andExpect(jsonPath("$.operations").isArray())
                .andExpect(jsonPath("$.operations.length()").value(2))
                .andExpect(jsonPath("$.operationsPage").value(0))
                .andExpect(jsonPath("$.operationsSize").value(10))
                .andExpect(jsonPath("$.totalOperationsPage").value(1));
    }

    @Test
    void testRequestStatementViaWeb_InvalidAccountType() throws Exception {
        mockMvc.perform(get("/api/audit-account/INVALID/{ownerId}/{page}/{size}", testUser.getId(), 0, 10)
                        .header("Authorization", validToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Wrong account type"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testRequestStatementViaWeb_NonExistentUser() throws Exception {
        mockMvc.perform(get("/api/audit-account/FUND/{ownerId}/{page}/{size}", 999L, 0, 10)
                        .header("Authorization", validToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Can't find funds owner"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testRequestStatementViaWeb_EmptyResult() throws Exception {
        mockMvc.perform(get("/api/audit-account/FUND/{ownerId}/{page}/{size}", testUser.getId(), 0, 10)
                        .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountType").value("FUND"))
                .andExpect(jsonPath("$.accountBalance").value(1000.0))
                .andExpect(jsonPath("$.operations").isArray())
                .andExpect(jsonPath("$.operations.length()").value(0))
                .andExpect(jsonPath("$.operationsPage").value(0))
                .andExpect(jsonPath("$.operationsSize").value(10))
                .andExpect(jsonPath("$.totalOperationsPage").value(0));
    }

    @Test
    void testRequestStatementViaWeb_Pagination() throws Exception {
        for (int i = 0; i < 25; i++) {
            createAuditRecords(AuditOperation.DEPOSIT, 10.0, 1000.0 + i * 10, 1010.0 + i * 10, testUser, testFund, null);
        }

        mockMvc.perform(get("/api/audit-account/FUND/{ownerId}/{page}/{size}", testUser.getId(), 0, 10)
                        .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operationsPage").value(0))
                .andExpect(jsonPath("$.operationsSize").value(10))
                .andExpect(jsonPath("$.totalOperationsPage").value(3))
                .andExpect(jsonPath("$.operations.length()").value(10));

        mockMvc.perform(get("/api/audit-account/FUND/{ownerId}/{page}/{size}", testUser.getId(), 1, 10)
                        .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operationsPage").value(1))
                .andExpect(jsonPath("$.operationsSize").value(10))
                .andExpect(jsonPath("$.totalOperationsPage").value(3))
                .andExpect(jsonPath("$.operations.length()").value(10));

        mockMvc.perform(get("/api/audit-account/FUND/{ownerId}/{page}/{size}", testUser.getId(), 2, 10)
                        .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operationsPage").value(2))
                .andExpect(jsonPath("$.operationsSize").value(10))
                .andExpect(jsonPath("$.totalOperationsPage").value(3))
                .andExpect(jsonPath("$.operations.length()").value(5));
    }

    @Test
    void testRequestStatementViaWeb_NegativePageNumber() throws Exception {
        mockMvc.perform(get("/api/audit-account/FUND/{ownerId}/{page}/{size}", testUser.getId(), -1, 10)
                        .header("Authorization", validToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Page number must be non-negative"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testRequestStatementViaWeb_ZeroPageSize() throws Exception {
        mockMvc.perform(get("/api/audit-account/FUND/{ownerId}/{page}/{size}", testUser.getId(), 0, 0)
                        .header("Authorization", validToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Page size must be positive"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testRequestStatementViaWeb_NegativePageSize() throws Exception {
        mockMvc.perform(get("/api/audit-account/FUND/{ownerId}/{page}/{size}", testUser.getId(), 0, -1)
                        .header("Authorization", validToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Page size must be positive"))
                .andExpect(jsonPath("$.path").exists());
    }

    private void createAuditRecords(AuditOperation operation, double amount, double balanceBefore,
                                   double balanceAfter, BankUser user, Fund fund, Saving saving) {
        auditService.recordAudit(operation, amount, balanceBefore, balanceAfter, user, fund, saving);
    }
}
