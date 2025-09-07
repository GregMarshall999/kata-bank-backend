package com.exalt_company.kata_bank_api.integration;

import com.exalt_company.kata_bank_api.entity.AccountAudit;
import com.exalt_company.kata_bank_api.entity.BankUser;
import com.exalt_company.kata_bank_api.entity.Fund;
import com.exalt_company.kata_bank_api.entity.Saving;
import com.exalt_company.kata_bank_api.entity.user_fields.Credentials;
import com.exalt_company.kata_bank_api.entity.user_fields.Identity;
import com.exalt_company.kata_bank_api.enums.AuditOperation;
import com.exalt_company.kata_bank_api.enums.BankRole;
import com.exalt_company.kata_bank_api.repository.AccountAuditRepository;
import com.exalt_company.kata_bank_api.repository.BankUserRepository;
import com.exalt_company.kata_bank_api.repository.FundRepository;
import com.exalt_company.kata_bank_api.repository.SavingRepository;
import com.exalt_company.kata_bank_api.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class AccountAuditIntegrationTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private BankUserRepository bankUserRepository;

    @Autowired
    private FundRepository fundRepository;

    @Autowired
    private SavingRepository savingRepository;

    @Autowired
    private AccountAuditRepository accountAuditRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private MockMvc mockMvc;
    private BankUser adminUser;
    private BankUser clientUser;
    private BankUser otherClientUser;
    private String adminToken;
    private String clientToken;
    private Fund testFund;
    private Saving testSaving;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        
        accountAuditRepository.deleteAll();
        fundRepository.deleteAll();
        savingRepository.deleteAll();
        bankUserRepository.deleteAll();

        adminUser = createTestUser("Admin", "User", "admin@example.com", "admin123", BankRole.ADMIN);
        adminUser = bankUserRepository.save(adminUser);
        adminToken = "Bearer " + jwtService.generateToken(adminUser, adminUser.getId(), adminUser.getBankRole());

        clientUser = createTestUser("Client", "User", "client@example.com", "client123", BankRole.CLIENT);
        clientUser = bankUserRepository.save(clientUser);
        clientToken = "Bearer " + jwtService.generateToken(clientUser, clientUser.getId(), clientUser.getBankRole());

        otherClientUser = createTestUser("Other", "Client", "other@example.com", "other123", BankRole.CLIENT);
        otherClientUser = bankUserRepository.save(otherClientUser);

        testFund = new Fund();
        testFund.setBalance(1000.0);
        testFund.setOwner(clientUser);
        testFund = fundRepository.save(testFund);

        testSaving = new Saving();
        testSaving.setBalance(500.0);
        testSaving.setOwner(clientUser);
        testSaving = savingRepository.save(testSaving);

        createTestAuditRecords();
    }

    @Test
    void testGetFundAccountStatementAsAdmin() throws Exception {
        mockMvc.perform(get("/api/audit-account/statement/FUND/{ownerId}/{page}/{size}", 
                        clientUser.getId(), 0, 10)
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountType").value("FUND"))
                .andExpect(jsonPath("$.accountBalance").value(1000.0))
                .andExpect(jsonPath("$.operations").isArray())
                .andExpect(jsonPath("$.operationsPage").value(0))
                .andExpect(jsonPath("$.operationsSize").value(10))
                .andExpect(jsonPath("$.totalOperationsPage").exists());
    }

    @Test
    void testGetSavingAccountStatementAsAdmin() throws Exception {
        mockMvc.perform(get("/api/audit-account/statement/SAVING/{ownerId}/{page}/{size}", 
                        clientUser.getId(), 0, 10)
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountType").value("SAVING"))
                .andExpect(jsonPath("$.accountBalance").value(500.0))
                .andExpect(jsonPath("$.operations").isArray())
                .andExpect(jsonPath("$.operationsPage").value(0))
                .andExpect(jsonPath("$.operationsSize").value(10))
                .andExpect(jsonPath("$.totalOperationsPage").exists());
    }

    @Test
    void testGetFundAccountStatementAsClient() throws Exception {
        mockMvc.perform(get("/api/audit-account/statement/FUND/{ownerId}/{page}/{size}", 
                        clientUser.getId(), 0, 10)
                        .header("Authorization", clientToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountType").value("FUND"))
                .andExpect(jsonPath("$.accountBalance").value(1000.0))
                .andExpect(jsonPath("$.operations").isArray())
                .andExpect(jsonPath("$.operationsPage").value(0))
                .andExpect(jsonPath("$.operationsSize").value(10))
                .andExpect(jsonPath("$.totalOperationsPage").exists());
    }

    @Test
    void testGetSavingAccountStatementAsClient() throws Exception {
        mockMvc.perform(get("/api/audit-account/statement/SAVING/{ownerId}/{page}/{size}", 
                        clientUser.getId(), 0, 10)
                        .header("Authorization", clientToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountType").value("SAVING"))
                .andExpect(jsonPath("$.accountBalance").value(500.0))
                .andExpect(jsonPath("$.operations").isArray())
                .andExpect(jsonPath("$.operationsPage").value(0))
                .andExpect(jsonPath("$.operationsSize").value(10))
                .andExpect(jsonPath("$.totalOperationsPage").exists());
    }

    @Test
    void testGetAccountStatementWithPagination() throws Exception {
        mockMvc.perform(get("/api/audit-account/statement/FUND/{ownerId}/{page}/{size}", 
                        clientUser.getId(), 0, 5)
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountType").value("FUND"))
                .andExpect(jsonPath("$.operationsPage").value(0))
                .andExpect(jsonPath("$.operationsSize").value(5))
                .andExpect(jsonPath("$.totalOperationsPage").exists());
    }

    @Test
    void testGetAccountStatementWithLargePageSize() throws Exception {
        mockMvc.perform(get("/api/audit-account/statement/FUND/{ownerId}/{page}/{size}", 
                        clientUser.getId(), 0, 100)
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountType").value("FUND"))
                .andExpect(jsonPath("$.operationsPage").value(0))
                .andExpect(jsonPath("$.operationsSize").value(100))
                .andExpect(jsonPath("$.totalOperationsPage").exists());
    }

    @Test
    void testGetAccountStatementWithoutAuthorization() throws Exception {
        mockMvc.perform(get("/api/audit-account/statement/FUND/{ownerId}/{page}/{size}", 
                        clientUser.getId(), 0, 10))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetOtherUserAccountStatementAsClient_ShouldFail() throws Exception {
        mockMvc.perform(get("/api/audit-account/statement/FUND/{ownerId}/{page}/{size}", 
                        otherClientUser.getId(), 0, 10)
                        .header("Authorization", clientToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testGetOtherUserAccountStatementAsAdmin_ShouldFail() throws Exception {
        mockMvc.perform(get("/api/audit-account/statement/FUND/{ownerId}/{page}/{size}", 
                        otherClientUser.getId(), 0, 10)
                        .header("Authorization", adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testGetAccountStatementWithInvalidAccountType() throws Exception {
        mockMvc.perform(get("/api/audit-account/statement/INVALID/{ownerId}/{page}/{size}", 
                        clientUser.getId(), 0, 10)
                        .header("Authorization", adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testGetAccountStatementWithNonExistentUser() throws Exception {
        mockMvc.perform(get("/api/audit-account/statement/FUND/{ownerId}/{page}/{size}", 
                        999L, 0, 10)
                        .header("Authorization", adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testGetAccountStatementWithNegativeOwnerId() throws Exception {
        mockMvc.perform(get("/api/audit-account/statement/FUND/{ownerId}/{page}/{size}", 
                        -1L, 0, 10)
                        .header("Authorization", adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testGetAccountStatementWithZeroOwnerId() throws Exception {
        mockMvc.perform(get("/api/audit-account/statement/FUND/{ownerId}/{page}/{size}", 
                        0L, 0, 10)
                        .header("Authorization", adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testGetAccountStatementWithNegativePage() throws Exception {
        mockMvc.perform(get("/api/audit-account/statement/FUND/{ownerId}/{page}/{size}", 
                        clientUser.getId(), -1, 10)
                        .header("Authorization", adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testGetAccountStatementWithNegativeSize() throws Exception {
        mockMvc.perform(get("/api/audit-account/statement/FUND/{ownerId}/{page}/{size}", 
                        clientUser.getId(), 0, -1)
                        .header("Authorization", adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testGetAccountStatementWithZeroSize() throws Exception {
        mockMvc.perform(get("/api/audit-account/statement/FUND/{ownerId}/{page}/{size}", 
                        clientUser.getId(), 0, 0)
                        .header("Authorization", adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testGetAccountStatementWithLargePageNumber() throws Exception {
        mockMvc.perform(get("/api/audit-account/statement/FUND/{ownerId}/{page}/{size}", 
                        clientUser.getId(), 999, 10)
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountType").value("FUND"))
                .andExpect(jsonPath("$.operations").isArray())
                .andExpect(jsonPath("$.operations.length()").value(0));
    }

    @Test
    void testGetAccountStatementWithLowerCaseAccountType() throws Exception {
        mockMvc.perform(get("/api/audit-account/statement/fund/{ownerId}/{page}/{size}", 
                        clientUser.getId(), 0, 10)
                        .header("Authorization", adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testGetAccountStatementWithMixedCaseAccountType() throws Exception {
        mockMvc.perform(get("/api/audit-account/statement/FuNd/{ownerId}/{page}/{size}", 
                        clientUser.getId(), 0, 10)
                        .header("Authorization", adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testGetAccountStatementForUserWithNoFund() throws Exception {
        mockMvc.perform(get("/api/audit-account/statement/FUND/{ownerId}/{page}/{size}", 
                        otherClientUser.getId(), 0, 10)
                        .header("Authorization", adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testGetAccountStatementForUserWithNoSaving() throws Exception {
        mockMvc.perform(get("/api/audit-account/statement/SAVING/{ownerId}/{page}/{size}", 
                        otherClientUser.getId(), 0, 10)
                        .header("Authorization", adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testAccountStatementContainsOperationDetails() throws Exception {
        mockMvc.perform(get("/api/audit-account/statement/FUND/{ownerId}/{page}/{size}", 
                        clientUser.getId(), 0, 10)
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.operations").isArray())
                .andExpect(jsonPath("$.operations[0].operation").exists())
                .andExpect(jsonPath("$.operations[0].operationAuthor").exists())
                .andExpect(jsonPath("$.operations[0].operationAmount").exists())
                .andExpect(jsonPath("$.operations[0].operationDate").exists());
    }

    @Test
    void testAccountStatementOperationsAreOrderedByDate() throws Exception {
        mockMvc.perform(get("/api/audit-account/statement/FUND/{ownerId}/{page}/{size}", 
                        clientUser.getId(), 0, 10)
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.operations").isArray());
    }

    private BankUser createTestUser(String name, String surname, String email, String password, BankRole role) {
        Identity identity = new Identity();
        identity.setName(name);
        identity.setSurname(surname);

        Credentials credentials = new Credentials();
        credentials.setEmail(email);
        credentials.setPassword(passwordEncoder.encode(password));

        BankUser user = new BankUser();
        user.setCredentials(credentials);
        user.setIdentity(identity);
        user.setBankRole(role);

        return user;
    }

    private void createTestAuditRecords() {
        AccountAudit fundDepositAudit = new AccountAudit();
        fundDepositAudit.setOperation(AuditOperation.DEPOSIT);
        fundDepositAudit.setAmount(1000.0);
        fundDepositAudit.setBalanceBefore(0.0);
        fundDepositAudit.setBalanceAfter(1000.0);
        fundDepositAudit.setRequestingUser(clientUser);
        fundDepositAudit.setUserFund(testFund);
        accountAuditRepository.save(fundDepositAudit);

        AccountAudit savingDepositAudit = new AccountAudit();
        savingDepositAudit.setOperation(AuditOperation.DEPOSIT);
        savingDepositAudit.setAmount(500.0);
        savingDepositAudit.setBalanceBefore(0.0);
        savingDepositAudit.setBalanceAfter(500.0);
        savingDepositAudit.setRequestingUser(clientUser);
        savingDepositAudit.setUserSaving(testSaving);
        accountAuditRepository.save(savingDepositAudit);

        for (int i = 0; i < 5; i++) {
            AccountAudit additionalAudit = new AccountAudit();
            additionalAudit.setOperation(AuditOperation.WITHDRAW);
            additionalAudit.setAmount(50.0);
            additionalAudit.setBalanceBefore(1000.0 - (i * 50.0));
            additionalAudit.setBalanceAfter(950.0 - (i * 50.0));
            additionalAudit.setRequestingUser(clientUser);
            additionalAudit.setUserFund(testFund);
            accountAuditRepository.save(additionalAudit);
        }
    }
}
