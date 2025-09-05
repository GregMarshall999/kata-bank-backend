package com.exalt_company.kata_bank_api.integration;

import com.exalt_company.kata_bank_api.dto.SavingDto;
import com.exalt_company.kata_bank_api.dto.auth.AuthenticationRequest;
import com.exalt_company.kata_bank_api.dto.auth.RegisterRequest;
import com.exalt_company.kata_bank_api.dto.fund.FundOpDto;
import com.exalt_company.kata_bank_api.dto.fund.OverdrawDto;
import com.exalt_company.kata_bank_api.entity.Fund;
import com.exalt_company.kata_bank_api.entity.Saving;
import com.exalt_company.kata_bank_api.enums.Banking;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class EndToEndIntegrationTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private BankUserRepository bankUserRepository;

    @Autowired
    private FundRepository fundRepository;

    @Autowired
    private SavingRepository savingRepository;


    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private String userToken;
    private Long userId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        bankUserRepository.deleteAll();
        fundRepository.deleteAll();
        savingRepository.deleteAll();
    }

    @Test
    void testCompleteBankingWorkflow() throws Exception {
        // Step 1: Register a new user
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("John");
        registerRequest.setSurname("Doe");
        registerRequest.setEmail("john.doe@example.com");
        registerRequest.setPassword("password123");

        String registerResponse = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract token from response
        userToken = objectMapper.readTree(registerResponse).get("token").asText();
        
        // Get user ID from token
        userId = jwtService.extractId(userToken);

        // Step 2: Authenticate the user
        AuthenticationRequest authRequest = new AuthenticationRequest();
        authRequest.setEmail("john.doe@example.com");
        authRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").exists());

        // Step 3: Create initial fund through deposit
        FundOpDto initialDeposit = new FundOpDto();
        initialDeposit.setId(0L);
        initialDeposit.setBalance(5000.0);
        initialDeposit.setOwnerId(userId);

        mockMvc.perform(post("/api/fund/deposit")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(initialDeposit)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(Banking.DEPOSITED.name()));

        // Step 4: Request overdraw capabilities
        Fund createdFund = fundRepository.findAll().stream()
                .filter(fund -> fund.getOwner().getId() == userId)
                .findFirst()
                .orElse(null);
        assertNotNull(createdFund);

        OverdrawDto overdrawRequest = new OverdrawDto();
        overdrawRequest.setId(createdFund.getId());
        overdrawRequest.setMaxOverdraw(1000.0);
        overdrawRequest.setOwnerId(userId);

        mockMvc.perform(put("/api/fund/request-overdraw")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overdrawRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(Banking.AUTHORIZED.name()));

        // Step 5: Withdraw more than balance (using overdraw)
        FundOpDto overdrawWithdrawal = new FundOpDto();
        overdrawWithdrawal.setId(createdFund.getId());
        overdrawWithdrawal.setBalance(5500.0); // 5000 + 500 overdraw
        overdrawWithdrawal.setOwnerId(userId);

        mockMvc.perform(post("/api/fund/withdraw")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overdrawWithdrawal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(Banking.WITHDREW.name()));

        // Step 6: Open a savings account
        SavingDto openSaving = new SavingDto();
        openSaving.setId(0L);
        openSaving.setBalance(0.0);
        openSaving.setMaxBalance(10000.0);
        openSaving.setOwnerId(userId);

        mockMvc.perform(post("/api/saving/open")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(openSaving)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(Banking.AUTHORIZED.name()));

        // Step 7: Deposit to savings account
        Saving createdSaving = savingRepository.findAll().stream()
                .filter(saving -> saving.getOwner().getId() == userId)
                .findFirst()
                .orElse(null);
        assertNotNull(createdSaving);

        SavingDto savingDeposit = new SavingDto();
        savingDeposit.setId(createdSaving.getId());
        savingDeposit.setBalance(2000.0);
        savingDeposit.setMaxBalance(10000.0);
        savingDeposit.setOwnerId(userId);

        mockMvc.perform(post("/api/saving/deposit")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDeposit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(Banking.DEPOSITED.name()));

        // Step 8: Withdraw from savings account
        SavingDto savingWithdrawal = new SavingDto();
        savingWithdrawal.setId(createdSaving.getId());
        savingWithdrawal.setBalance(500.0);
        savingWithdrawal.setMaxBalance(10000.0);
        savingWithdrawal.setOwnerId(userId);

        mockMvc.perform(post("/api/saving/withdraw")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingWithdrawal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(Banking.WITHDREW.name()));

        // Step 9: Deposit back to fund to restore positive balance
        FundOpDto fundDeposit = new FundOpDto();
        fundDeposit.setId(createdFund.getId());
        fundDeposit.setBalance(1000.0);
        fundDeposit.setOwnerId(userId);

        mockMvc.perform(post("/api/fund/deposit")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundDeposit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(Banking.DEPOSITED.name()));

        // Step 10: Cancel overdraw capabilities
        OverdrawDto cancelOverdraw = new OverdrawDto();
        cancelOverdraw.setId(createdFund.getId());
        cancelOverdraw.setMaxOverdraw(0.0);
        cancelOverdraw.setOwnerId(userId);

        mockMvc.perform(put("/api/fund/cancel-overdraw")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelOverdraw)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(Banking.COMPLETED.name()));

        // Step 11: Withdraw remaining from savings to close account
        SavingDto finalWithdrawal = new SavingDto();
        finalWithdrawal.setId(createdSaving.getId());
        finalWithdrawal.setBalance(1500.0); // Remaining balance
        finalWithdrawal.setMaxBalance(10000.0);
        finalWithdrawal.setOwnerId(userId);

        mockMvc.perform(post("/api/saving/withdraw")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(finalWithdrawal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(Banking.WITHDREW.name()));

        // Step 12: Close savings account
        SavingDto closeSaving = new SavingDto();
        closeSaving.setId(createdSaving.getId());
        closeSaving.setBalance(0.0);
        closeSaving.setMaxBalance(10000.0);
        closeSaving.setOwnerId(userId);

        mockMvc.perform(post("/api/saving/close")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(closeSaving)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(Banking.COMPLETED.name()));

        // Step 13: Request account statements
        mockMvc.perform(get("/api/audit-account/FUND/{ownerId}/{page}/{size}", userId, 0, 10)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountType").value("FUND"))
                .andExpect(jsonPath("$.operations").isArray())
                .andExpect(jsonPath("$.operations.length()").value(3)); // deposit, withdraw, deposit

        // Step 14: Verify final state
        Fund finalFund = fundRepository.findById(createdFund.getId()).orElse(null);
        assertNotNull(finalFund);
        assertEquals(500.0, finalFund.getBalance()); // 5000 - 5500 + 1000
        assertEquals(false, finalFund.canOverdraw());
        assertEquals(0.0, finalFund.getMaxOverdraw());

        // Savings account should be deleted
        boolean savingExists = savingRepository.findById(createdSaving.getId()).isPresent();
        assertEquals(false, savingExists);
    }

    @Test
    void testMultiUserBankingWorkflow() throws Exception {
        // Create two users
        String user1Token = createUserAndGetToken("user1@example.com", "User", "One");
        String user2Token = createUserAndGetToken("user2@example.com", "User", "Two");
        
        Long user1Id = jwtService.extractId(user1Token);
        Long user2Id = jwtService.extractId(user2Token);

        // User 1 creates fund
        FundOpDto user1Deposit = new FundOpDto();
        user1Deposit.setId(0L);
        user1Deposit.setBalance(1000.0);
        user1Deposit.setOwnerId(user1Id);

        mockMvc.perform(post("/api/fund/deposit")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1Deposit)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(Banking.DEPOSITED.name()));

        // User 2 creates fund
        FundOpDto user2Deposit = new FundOpDto();
        user2Deposit.setId(0L);
        user2Deposit.setBalance(2000.0);
        user2Deposit.setOwnerId(user2Id);

        mockMvc.perform(post("/api/fund/deposit")
                        .header("Authorization", "Bearer " + user2Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2Deposit)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(Banking.DEPOSITED.name()));

        // User 1 tries to access User 2's fund (should fail)
        FundOpDto unauthorizedWithdrawal = new FundOpDto();
        unauthorizedWithdrawal.setId(1L); // Assuming user2's fund has ID 1
        unauthorizedWithdrawal.setBalance(100.0);
        unauthorizedWithdrawal.setOwnerId(user2Id);

        mockMvc.perform(post("/api/fund/withdraw")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(unauthorizedWithdrawal)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Attempted to access unauthorized funds"));

        // Verify both users have separate funds
        assertEquals(2, fundRepository.count());
        
        Fund user1Fund = fundRepository.findAll().stream()
                .filter(fund -> fund.getOwner().getId() == user1Id)
                .findFirst()
                .orElse(null);
        assertNotNull(user1Fund);
        assertEquals(1000.0, user1Fund.getBalance());

        Fund user2Fund = fundRepository.findAll().stream()
                .filter(fund -> fund.getOwner().getId() == user2Id)
                .findFirst()
                .orElse(null);
        assertNotNull(user2Fund);
        assertEquals(2000.0, user2Fund.getBalance());
    }

    @Test
    void testErrorHandlingAndRecovery() throws Exception {
        // Create user
        String errorUserToken = createUserAndGetToken("error@example.com", "Error", "User");
        Long errorUserId = jwtService.extractId(errorUserToken);

        // Test invalid operations
        FundOpDto invalidDeposit = new FundOpDto();
        invalidDeposit.setId(0L);
        invalidDeposit.setBalance(-100.0); // Negative amount
        invalidDeposit.setOwnerId(errorUserId);

        mockMvc.perform(post("/api/fund/deposit")
                        .header("Authorization", "Bearer " + errorUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDeposit)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Wrong value for balance"));

        // Test withdrawal from non-existent fund
        FundOpDto invalidWithdrawal = new FundOpDto();
        invalidWithdrawal.setId(999L);
        invalidWithdrawal.setBalance(100.0);
        invalidWithdrawal.setOwnerId(errorUserId);

        mockMvc.perform(post("/api/fund/withdraw")
                        .header("Authorization", "Bearer " + errorUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidWithdrawal)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No balance to withdraw from"));

        // Test successful operation after errors
        FundOpDto validDeposit = new FundOpDto();
        validDeposit.setId(0L);
        validDeposit.setBalance(1000.0);
        validDeposit.setOwnerId(errorUserId);

        mockMvc.perform(post("/api/fund/deposit")
                        .header("Authorization", "Bearer " + errorUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDeposit)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(Banking.DEPOSITED.name()));

        // Verify fund was created successfully
        Fund createdFund = fundRepository.findAll().stream()
                .filter(fund -> fund.getOwner().getId() == errorUserId)
                .findFirst()
                .orElse(null);
        assertNotNull(createdFund);
        assertEquals(1000.0, createdFund.getBalance());
    }

    private String createUserAndGetToken(String email, String name, String surname) throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName(name);
        registerRequest.setSurname(surname);
        registerRequest.setEmail(email);
        registerRequest.setPassword("password123");

        String registerResponse = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(registerResponse).get("token").asText();
    }
}
