package com.exalt_company.kata_bank_api.integration;

import com.exalt_company.kata_bank_api.dto.fund.FundDto;
import com.exalt_company.kata_bank_api.dto.fund.FundOpDto;
import com.exalt_company.kata_bank_api.dto.fund.OverdrawDto;
import com.exalt_company.kata_bank_api.dto.SavingDto;
import com.exalt_company.kata_bank_api.entity.BankUser;
import com.exalt_company.kata_bank_api.entity.Fund;
import com.exalt_company.kata_bank_api.entity.Saving;
import com.exalt_company.kata_bank_api.entity.user_fields.Credentials;
import com.exalt_company.kata_bank_api.entity.user_fields.Identity;
import com.exalt_company.kata_bank_api.enums.BankRole;
import com.exalt_company.kata_bank_api.repository.BankUserRepository;
import com.exalt_company.kata_bank_api.repository.FundRepository;
import com.exalt_company.kata_bank_api.repository.SavingRepository;
import com.exalt_company.kata_bank_api.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security integration tests to verify role-based access control.
 * Tests that admins have full access to all endpoints while clients are restricted
 * to only their designated endpoints as defined in SecurityConfig.
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class SecurityIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private BankUserRepository bankUserRepository;

    @Autowired
    private FundRepository fundRepository;

    @Autowired
    private SavingRepository savingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private BankUser adminUser;
    private BankUser clientUser;
    private String adminToken;
    private String clientToken;
    private Fund testFund;
    private Saving testSaving;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        bankUserRepository.deleteAll();
        fundRepository.deleteAll();
        savingRepository.deleteAll();

        adminUser = createTestUser("Admin", "User", "admin@example.com", "admin123", BankRole.ADMIN);
        adminUser = bankUserRepository.save(adminUser);
        adminToken = "Bearer " + jwtService.generateToken(adminUser, adminUser.getId(), adminUser.getBankRole());

        clientUser = createTestUser("Client", "User", "client@example.com", "client123", BankRole.CLIENT);
        clientUser = bankUserRepository.save(clientUser);
        clientToken = "Bearer " + jwtService.generateToken(clientUser, clientUser.getId(), clientUser.getBankRole());

        testFund = new Fund();
        testFund.setBalance(1000.0);
        testFund.setOwner(clientUser);
        testFund.setCanOverdraw(false);
        testFund.setMaxOverdraw(0.0);
        testFund = fundRepository.save(testFund);

        testSaving = new Saving();
        testSaving.setBalance(500.0);
        testSaving.setMaxBalance(10000.0);
        testSaving.setOwner(clientUser);
        testSaving = savingRepository.save(testSaving);
    }

    @Test
    void testAdminCanAccessAllBankUserEndpoints() throws Exception {
        mockMvc.perform(get("/api/bank-user")
                        .header("Authorization", adminToken))
                .andExpect(status().isFound());

        mockMvc.perform(get("/api/bank-user/{id}", clientUser.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isFound());

        mockMvc.perform(get("/api/bank-user/{page}/{size}", 0, 10)
                        .header("Authorization", adminToken))
                .andExpect(status().isFound());
    }

    @Test
    void testAdminCanAccessAllFundEndpoints() throws Exception {
        FundDto fundDto = new FundDto();
        fundDto.setId(0L);
        fundDto.setBalance(2000.0);
        fundDto.setOwnerId(clientUser.getId());

        mockMvc.perform(post("/api/fund")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundDto)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/fund")
                        .header("Authorization", adminToken))
                .andExpect(status().isFound());

        mockMvc.perform(get("/api/fund/{id}", testFund.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isFound());

        mockMvc.perform(put("/api/fund/{id}", testFund.getId())
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundDto)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/fund/{id}", testFund.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testAdminCanAccessAllSavingEndpoints() throws Exception {
        SavingDto savingDto = new SavingDto();
        savingDto.setId(0L);
        savingDto.setBalance(1000.0);
        savingDto.setMaxBalance(15000.0);
        savingDto.setOwnerId(clientUser.getId());

        mockMvc.perform(post("/api/saving")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/saving")
                        .header("Authorization", adminToken))
                .andExpect(status().isFound());

        mockMvc.perform(get("/api/saving/{id}", testSaving.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isFound());

        mockMvc.perform(put("/api/saving/{id}", testSaving.getId())
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/saving/{id}", testSaving.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testAdminCanAccessAllAuditEndpoints() throws Exception {
        mockMvc.perform(get("/api/audit-account/statement/FUND/{ownerId}/{page}/{size}", clientUser.getId(), 0, 10)
                        .header("Authorization", adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testAdminCanAccessClientSpecificEndpoints() throws Exception {
        FundOpDto fundOpDto = new FundOpDto();
        fundOpDto.setId(testFund.getId());
        fundOpDto.setOwnerId(clientUser.getId());
        fundOpDto.setBalance(100.0);

        mockMvc.perform(post("/api/fund/deposit")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundOpDto)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/fund/withdraw")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundOpDto)))
                .andExpect(status().isOk());

        OverdrawDto overdrawDto = new OverdrawDto();
        overdrawDto.setId(testFund.getId());
        overdrawDto.setOwnerId(clientUser.getId());
        overdrawDto.setMaxOverdraw(500.0);

        mockMvc.perform(put("/api/fund/request-overdraw")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overdrawDto)))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/fund/cancel-overdraw")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overdrawDto)))
                .andExpect(status().isOk());

        SavingDto savingOpDto = new SavingDto();
        savingOpDto.setId(testSaving.getId());
        savingOpDto.setOwnerId(clientUser.getId());
        savingOpDto.setMaxBalance(10000.0);
        savingOpDto.setBalance(200.0);

        mockMvc.perform(post("/api/saving/deposit")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingOpDto)))
                .andExpect(status().isOk());

        savingOpDto.setBalance(700.0);

        mockMvc.perform(post("/api/saving/withdraw")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingOpDto)))
                .andExpect(status().isOk());

        SavingDto savingDto = new SavingDto();
        savingDto.setId(testSaving.getId());
        savingDto.setBalance(0.0);
        savingDto.setMaxBalance(10000.0);
        savingDto.setOwnerId(clientUser.getId());

        mockMvc.perform(post("/api/saving/close")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isOk());

        savingDto.setId(0L);

        mockMvc.perform(post("/api/saving/open")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void testClientCannotAccessBankUserEndpoints() throws Exception {
        mockMvc.perform(get("/api/bank-user")
                        .header("Authorization", clientToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/bank-user/{id}", adminUser.getId())
                        .header("Authorization", clientToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/bank-user/{page}/{size}", 0, 10)
                        .header("Authorization", clientToken))
                .andExpect(status().isForbidden());

        FundDto fundDto = new FundDto();
        fundDto.setId(0L);
        fundDto.setBalance(2000.0);
        fundDto.setOwnerId(clientUser.getId());

        mockMvc.perform(post("/api/bank-user")
                        .header("Authorization", clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundDto)))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/bank-user/{id}", clientUser.getId())
                        .header("Authorization", clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundDto)))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/bank-user/{id}", clientUser.getId())
                        .header("Authorization", clientToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testClientCannotAccessAdminFundEndpoints() throws Exception {
        FundDto fundDto = new FundDto();
        fundDto.setId(0L);
        fundDto.setBalance(2000.0);
        fundDto.setOwnerId(clientUser.getId());

        mockMvc.perform(post("/api/fund")
                        .header("Authorization", clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundDto)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/fund")
                        .header("Authorization", clientToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/fund/{id}", testFund.getId())
                        .header("Authorization", clientToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/fund/{id}", testFund.getId())
                        .header("Authorization", clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundDto)))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/fund/{id}", testFund.getId())
                        .header("Authorization", clientToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testClientCannotAccessAdminSavingEndpoints() throws Exception {
        SavingDto savingDto = new SavingDto();
        savingDto.setId(0L);
        savingDto.setBalance(1000.0);
        savingDto.setMaxBalance(15000.0);
        savingDto.setOwnerId(clientUser.getId());

        mockMvc.perform(post("/api/saving")
                        .header("Authorization", clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/saving")
                        .header("Authorization", clientToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/saving/{id}", testSaving.getId())
                        .header("Authorization", clientToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/saving/{id}", testSaving.getId())
                        .header("Authorization", clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/saving/{id}", testSaving.getId())
                        .header("Authorization", clientToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testClientCannotAccessAdminAuditEndpoints() throws Exception {
        mockMvc.perform(get("/api/audit-account")
                        .header("Authorization", clientToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/audit-account/{id}", 1L)
                        .header("Authorization", clientToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testClientCanAccessDesignatedFundEndpoints() throws Exception {
        FundOpDto fundOpDto = new FundOpDto();
        fundOpDto.setId(testFund.getId());
        fundOpDto.setOwnerId(clientUser.getId());
        fundOpDto.setBalance(100.0);

        mockMvc.perform(post("/api/fund/deposit")
                        .header("Authorization", clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundOpDto)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/fund/withdraw")
                        .header("Authorization", clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundOpDto)))
                .andExpect(status().isOk());

        OverdrawDto overdrawDto = new OverdrawDto();
        overdrawDto.setId(testFund.getId());
        overdrawDto.setOwnerId(clientUser.getId());
        overdrawDto.setMaxOverdraw(500.0);

        mockMvc.perform(put("/api/fund/request-overdraw")
                        .header("Authorization", clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overdrawDto)))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/fund/cancel-overdraw")
                        .header("Authorization", clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overdrawDto)))
                .andExpect(status().isOk());
    }

    @Test
    void testClientCanAccessDesignatedSavingEndpoints() throws Exception {
        SavingDto savingOpDto = new SavingDto();
        savingOpDto.setId(testSaving.getId());
        savingOpDto.setOwnerId(clientUser.getId());
        savingOpDto.setMaxBalance(10000.0);
        savingOpDto.setBalance(500.0);

        mockMvc.perform(post("/api/saving/withdraw")
                        .header("Authorization", clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingOpDto)))
                .andExpect(status().isOk());

        SavingDto savingDto = new SavingDto();
        savingDto.setId(testSaving.getId());
        savingDto.setBalance(0.0);
        savingDto.setMaxBalance(10000.0);
        savingDto.setOwnerId(clientUser.getId());

        mockMvc.perform(post("/api/saving/close")
                        .header("Authorization", clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isOk());

        savingDto.setId(0L);

        mockMvc.perform(post("/api/saving/open")
                        .header("Authorization", clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void testClientCanAccessAccountStatementEndpoint() throws Exception {
        mockMvc.perform(get("/api/audit-account/statement/FUND/{ownerId}/{page}/{size}", clientUser.getId(), 0, 10)
                        .header("Authorization", clientToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/audit-account/statement/SAVING/{ownerId}/{page}/{size}", clientUser.getId(), 0, 10)
                        .header("Authorization", clientToken))
                .andExpect(status().isOk());
    }

    @Test
    void testUnauthorizedAccessToAllEndpoints() throws Exception {
        mockMvc.perform(get("/api/bank-user"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/fund"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/saving"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/audit-account"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/fund/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/saving/open")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testClientCannotAccessOtherUsersResources() throws Exception {
        BankUser otherClient = createTestUser("Other", "Client", "other@example.com", "password123", BankRole.CLIENT);
        otherClient = bankUserRepository.save(otherClient);

        FundOpDto fundOpDto = new FundOpDto();
        fundOpDto.setOwnerId(otherClient.getId());
        fundOpDto.setBalance(100.0);

        mockMvc.perform(post("/api/fund/deposit")
                        .header("Authorization", clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundOpDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Attempted to access unauthorized funds: REFUSED"));

        SavingDto savingOpDto = new SavingDto();
        savingOpDto.setId(testSaving.getId());
        savingOpDto.setOwnerId(otherClient.getId());
        savingOpDto.setBalance(200.0);
        savingOpDto.setMaxBalance(10000.0);

        mockMvc.perform(post("/api/saving/deposit")
                        .header("Authorization", clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingOpDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Attempted to access unauthorized savings: REFUSED"));

        mockMvc.perform(get("/api/audit-account/statement/FUND/{ownerId}/{page}/{size}", otherClient.getId(), 0, 10)
                        .header("Authorization", clientToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Attempted to access unauthorized statement"));
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
}
