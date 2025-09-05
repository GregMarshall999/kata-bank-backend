package com.exalt_company.kata_bank_api.integration;

import com.exalt_company.kata_bank_api.dto.fund.FundDto;
import com.exalt_company.kata_bank_api.dto.fund.FundOpDto;
import com.exalt_company.kata_bank_api.entity.BankUser;
import com.exalt_company.kata_bank_api.entity.Fund;
import com.exalt_company.kata_bank_api.entity.user_fields.Credentials;
import com.exalt_company.kata_bank_api.entity.user_fields.Identity;
import com.exalt_company.kata_bank_api.enums.BankRole;
import com.exalt_company.kata_bank_api.enums.Banking;
import com.exalt_company.kata_bank_api.repository.BankUserRepository;
import com.exalt_company.kata_bank_api.repository.FundRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class FundIntegrationTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private BankUserRepository bankUserRepository;

    @Autowired
    private FundRepository fundRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private BankUser testUser;
    private String validToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        bankUserRepository.deleteAll();
        fundRepository.deleteAll();

        testUser = createTestUser("test", "user", "testuser@example.com", "password123", BankRole.CLIENT);
        testUser = bankUserRepository.save(testUser);
        
        validToken = jwtService.generateToken(testUser, testUser.getId(), testUser.getBankRole());
    }

    @Test
    void testDepositSuccess() throws Exception {
        FundDto fundDto = new FundDto();
        fundDto.setId(0L);
        fundDto.setBalance(1000.0);
        fundDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/fund/deposit")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(Banking.DEPOSITED.name()));

        Fund savedFund = fundRepository.findAll().stream()
                .filter(fund -> fund.getOwner().getId() == testUser.getId())
                .findFirst()
                .orElse(null);
        assertNotNull(savedFund);
        assertEquals(1000.0, savedFund.getBalance());
        assertEquals(testUser.getId(), savedFund.getOwner().getId());
    }

    @Test
    void testDepositWithZeroBalance() throws Exception {
        FundOpDto fundOpDto = new FundOpDto();
        fundOpDto.setId(0L);
        fundOpDto.setBalance(0.0);
        fundOpDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/fund/deposit")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundOpDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testDepositWithZeroOwnerId() throws Exception {
        FundOpDto fundOpDto = new FundOpDto();
        fundOpDto.setId(0L);
        fundOpDto.setBalance(100.0);
        fundOpDto.setOwnerId(0L);

        mockMvc.perform(post("/api/fund/deposit")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundOpDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testDepositWithNegativeOwnerId() throws Exception {
        FundOpDto fundOpDto = new FundOpDto();
        fundOpDto.setId(0L);
        fundOpDto.setBalance(100.0);
        fundOpDto.setOwnerId(-1L); // Negative owner ID

        mockMvc.perform(post("/api/fund/deposit")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundOpDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testWithdrawWithZeroBalance() throws Exception {
        FundOpDto fundOpDto = new FundOpDto();
        fundOpDto.setId(0L);
        fundOpDto.setBalance(0.0); // Zero balance
        fundOpDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/fund/withdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundOpDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testDepositToExistingFund() throws Exception {
        Fund existingFund = new Fund();
        existingFund.setBalance(500.0);
        existingFund.setOwner(testUser);
        existingFund = fundRepository.save(existingFund);

        FundDto fundDto = new FundDto();
        fundDto.setId(existingFund.getId());
        fundDto.setBalance(300.0);
        fundDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/fund/deposit")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(Banking.DEPOSITED.name()));

        Fund updatedFund = fundRepository.findById(existingFund.getId()).orElse(null);
        assertNotNull(updatedFund);
        assertEquals(800.0, updatedFund.getBalance());
    }

    @Test
    void testWithdrawSuccess() throws Exception {
        Fund existingFund = new Fund();
        existingFund.setBalance(1000.0);
        existingFund.setOwner(testUser);
        existingFund = fundRepository.save(existingFund);

        FundDto fundDto = new FundDto();
        fundDto.setId(existingFund.getId());
        fundDto.setBalance(300.0);
        fundDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/fund/withdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(Banking.WITHDREW.name()));

        Fund updatedFund = fundRepository.findById(existingFund.getId()).orElse(null);
        assertNotNull(updatedFund);
        assertEquals(700.0, updatedFund.getBalance());
    }

    @Test
    void testDepositWithoutAuthorization() throws Exception {
        FundDto fundDto = new FundDto();
        fundDto.setId(0L);
        fundDto.setBalance(1000.0);
        fundDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/fund/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testWithdrawWithoutAuthorization() throws Exception {
        FundDto fundDto = new FundDto();
        fundDto.setId(1L);
        fundDto.setBalance(100.0);
        fundDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/fund/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDepositWithUnauthorizedAccess() throws Exception {
        BankUser otherUser = createTestUser("other", "user", "otheruser@example.com", "password123", BankRole.CLIENT);
        otherUser = bankUserRepository.save(otherUser);

        FundDto fundDto = new FundDto();
        fundDto.setId(0L);
        fundDto.setBalance(1000.0);
        fundDto.setOwnerId(otherUser.getId());

        mockMvc.perform(post("/api/fund/deposit")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Attempted to access unauthorized funds"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testWithdrawWithInsufficientFunds() throws Exception {
        Fund existingFund = new Fund();
        existingFund.setBalance(100.0);
        existingFund.setOwner(testUser);
        existingFund = fundRepository.save(existingFund);

        FundDto fundDto = new FundDto();
        fundDto.setId(existingFund.getId());
        fundDto.setBalance(200.0);
        fundDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/fund/withdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Attempting to withdraw more than available"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testWithdrawFromNonExistentFund() throws Exception {
        FundDto fundDto = new FundDto();
        fundDto.setId(999L);
        fundDto.setBalance(100.0);
        fundDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/fund/withdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("No balance to withdraw from"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testWithdrawFromNewFund() throws Exception {
        FundDto fundDto = new FundDto();
        fundDto.setId(0L);
        fundDto.setBalance(100.0);
        fundDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/fund/withdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("No balance to withdraw from"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testDepositWithInvalidJson() throws Exception {
        mockMvc.perform(post("/api/fund/deposit")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testWithdrawWithInvalidJson() throws Exception {
        mockMvc.perform(post("/api/fund/withdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testDepositWithNegativeAmount() throws Exception {
        Fund existingFund = new Fund();
        existingFund.setBalance(1000.0);
        existingFund.setOwner(testUser);
        existingFund = fundRepository.save(existingFund);

        FundDto fundDto = new FundDto();
        fundDto.setId(existingFund.getId());
        fundDto.setBalance(-100.0);
        fundDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/fund/deposit")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Wrong value for balance"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testWithdrawWithNegativeAmount() throws Exception {
        Fund existingFund = new Fund();
        existingFund.setBalance(1000.0);
        existingFund.setOwner(testUser);
        existingFund = fundRepository.save(existingFund);

        FundDto fundDto = new FundDto();
        fundDto.setId(existingFund.getId());
        fundDto.setBalance(-100.0);
        fundDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/fund/withdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Wrong value for balance"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testDepositWithZeroAmount() throws Exception {
        Fund existingFund = new Fund();
        existingFund.setBalance(1000.0);
        existingFund.setOwner(testUser);
        existingFund = fundRepository.save(existingFund);

        FundDto fundDto = new FundDto();
        fundDto.setId(existingFund.getId());
        fundDto.setBalance(0.0);
        fundDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/fund/deposit")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Wrong value for balance"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testWithdrawWithZeroAmount() throws Exception {
        Fund existingFund = new Fund();
        existingFund.setBalance(1000.0);
        existingFund.setOwner(testUser);
        existingFund = fundRepository.save(existingFund);

        FundDto fundDto = new FundDto();
        fundDto.setId(existingFund.getId());
        fundDto.setBalance(0.0);
        fundDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/fund/withdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Wrong value for balance"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testCompleteFundWorkflow() throws Exception {
        // 1. Create initial fund through deposit
        FundDto initialDeposit = new FundDto();
        initialDeposit.setId(0L);
        initialDeposit.setBalance(2000.0);
        initialDeposit.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/fund/deposit")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(initialDeposit)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(Banking.DEPOSITED.name()));

        // Get the created fund
        Fund createdFund = fundRepository.findAll().stream()
                .filter(fund -> fund.getOwner().getId() == testUser.getId())
                .findFirst()
                .orElse(null);
        assertNotNull(createdFund);
        assertEquals(2000.0, createdFund.getBalance());

        // 2. Make additional deposit
        FundDto additionalDeposit = new FundDto();
        additionalDeposit.setId(createdFund.getId());
        additionalDeposit.setBalance(500.0);
        additionalDeposit.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/fund/deposit")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(additionalDeposit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(Banking.DEPOSITED.name()));

        // 3. Make withdrawal
        FundDto withdrawal = new FundDto();
        withdrawal.setId(createdFund.getId());
        withdrawal.setBalance(300.0);
        withdrawal.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/fund/withdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(Banking.WITHDREW.name()));

        // Verify final balance
        Fund finalFund = fundRepository.findById(createdFund.getId()).orElse(null);
        assertNotNull(finalFund);
        assertEquals(2200.0, finalFund.getBalance());
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
