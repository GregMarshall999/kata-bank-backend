package com.exalt_company.kata_bank_api.integration;

import com.exalt_company.kata_bank_api.dto.SavingDto;
import com.exalt_company.kata_bank_api.entity.BankUser;
import com.exalt_company.kata_bank_api.entity.Saving;
import com.exalt_company.kata_bank_api.entity.user_fields.Credentials;
import com.exalt_company.kata_bank_api.entity.user_fields.Identity;
import com.exalt_company.kata_bank_api.enums.BankRole;
import com.exalt_company.kata_bank_api.enums.Banking;
import com.exalt_company.kata_bank_api.repository.BankUserRepository;
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
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
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
class SavingIntegrationTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private BankUserRepository bankUserRepository;

    @Autowired
    private SavingRepository savingRepository;

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
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        bankUserRepository.deleteAll();
        savingRepository.deleteAll();

        testUser = createTestUser("test", "user", "testuser@example.com", "password123", BankRole.CLIENT);
        testUser = bankUserRepository.save(testUser);
        
        validToken = "Bearer " + jwtService.generateToken(testUser, testUser.getId(), testUser.getBankRole());
    }

    @Test
    void testOpenSavingsAccountSuccess() throws Exception {
        SavingDto savingDto = new SavingDto();
        savingDto.setId(0L);
        savingDto.setBalance(0.0);
        savingDto.setMaxBalance(10000.0);
        savingDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/saving/open")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(Banking.AUTHORIZED.name()));

        Saving savedSaving = savingRepository.findAll().stream()
                .filter(saving -> saving.getOwner().getId() == testUser.getId())
                .findFirst()
                .orElse(null);
        assertNotNull(savedSaving);
        assertEquals(0.0, savedSaving.getBalance());
        assertEquals(10000.0, savedSaving.getMaxBalance());
        assertEquals(testUser.getId(), savedSaving.getOwner().getId());
    }

    @Test
    void testOpenSavingsAccountWithNullMaxBalance() throws Exception {
        String jsonWithNullMaxBalance = """
                {
                    "id": 0,
                    "balance": 0.0,
                    "maxBalance": null,
                    "ownerId": %d
                }
                """.formatted(testUser.getId());

        mockMvc.perform(post("/api/saving/open")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithNullMaxBalance))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testOpenSavingsAccountWithZeroMaxBalance() throws Exception {
        SavingDto savingDto = new SavingDto();
        savingDto.setId(0L);
        savingDto.setBalance(0.0);
        savingDto.setMaxBalance(0.0);
        savingDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/saving/open")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testOpenSavingsAccountWithNullOwnerId() throws Exception {
        String jsonWithNullOwnerId = """
                {
                    "id": 0,
                    "balance": 0.0,
                    "maxBalance": 10000.0,
                    "ownerId": null
                }
                """;

        mockMvc.perform(post("/api/saving/open")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithNullOwnerId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testOpenSavingsAccountWithZeroOwnerId() throws Exception {
        SavingDto savingDto = new SavingDto();
        savingDto.setId(0L);
        savingDto.setBalance(0.0);
        savingDto.setMaxBalance(10000.0);
        savingDto.setOwnerId(0L);

        mockMvc.perform(post("/api/saving/open")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testOpenSavingsAccountWithNegativeOwnerId() throws Exception {
        SavingDto savingDto = new SavingDto();
        savingDto.setId(0L);
        savingDto.setBalance(0.0);
        savingDto.setMaxBalance(10000.0);
        savingDto.setOwnerId(-1L);

        mockMvc.perform(post("/api/saving/open")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testOpenSavingsAccountWithNegativeMaxBalance() throws Exception {
        SavingDto savingDto = new SavingDto();
        savingDto.setId(0L);
        savingDto.setBalance(0.0);
        savingDto.setMaxBalance(-1000.0);
        savingDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/saving/open")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message", containsString("Validation failed for argument")))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testOpenSavingsAccountWithUnauthorizedAccess() throws Exception {
        BankUser otherUser = createTestUser("other", "user", "otheruser@example.com", "password123", BankRole.CLIENT);
        otherUser = bankUserRepository.save(otherUser);

        SavingDto savingDto = new SavingDto();
        savingDto.setId(0L);
        savingDto.setBalance(0.0);
        savingDto.setMaxBalance(10000.0);
        savingDto.setOwnerId(otherUser.getId());

        mockMvc.perform(post("/api/saving/open")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Attempted to access unauthorized savings: REFUSED"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testDepositSuccess() throws Exception {
        Saving existingSaving = new Saving();
        existingSaving.setBalance(1000.0);
        existingSaving.setMaxBalance(5000.0);
        existingSaving.setOwner(testUser);
        existingSaving = savingRepository.save(existingSaving);

        SavingDto savingDto = new SavingDto();
        savingDto.setId(existingSaving.getId());
        savingDto.setBalance(500.0);
        savingDto.setMaxBalance(5000.0);
        savingDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/saving/deposit")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(Banking.DEPOSITED.name()));

        Saving updatedSaving = savingRepository.findById(existingSaving.getId()).orElse(null);
        assertNotNull(updatedSaving);
        assertEquals(1500.0, updatedSaving.getBalance());
    }

    @Test
    void testDepositExceedingMaxBalance() throws Exception {
        Saving existingSaving = new Saving();
        existingSaving.setBalance(1000.0);
        existingSaving.setMaxBalance(1500.0);
        existingSaving.setOwner(testUser);
        existingSaving = savingRepository.save(existingSaving);

        SavingDto savingDto = new SavingDto();
        savingDto.setId(existingSaving.getId());
        savingDto.setBalance(1000.0);
        savingDto.setMaxBalance(1500.0);
        savingDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/saving/deposit")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Savings cannot exceed the maximum allowed balance: REFUSED"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testDepositWithZeroId() throws Exception {
        SavingDto savingDto = new SavingDto();
        savingDto.setId(0L);
        savingDto.setBalance(500.0);
        savingDto.setMaxBalance(5000.0);
        savingDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/saving/deposit")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Please open a savings account before depositing here: REFUSED"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testDepositWithNegativeAmount() throws Exception {
        Saving existingSaving = new Saving();
        existingSaving.setBalance(1000.0);
        existingSaving.setMaxBalance(5000.0);
        existingSaving.setOwner(testUser);
        existingSaving = savingRepository.save(existingSaving);

        SavingDto savingDto = new SavingDto();
        savingDto.setId(existingSaving.getId());
        savingDto.setBalance(-100.0);
        savingDto.setMaxBalance(5000.0);
        savingDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/saving/deposit")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message", containsString("Validation failed for argument")))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testWithdrawSuccess() throws Exception {
        Saving existingSaving = new Saving();
        existingSaving.setBalance(2000.0);
        existingSaving.setMaxBalance(5000.0);
        existingSaving.setOwner(testUser);
        existingSaving = savingRepository.save(existingSaving);

        SavingDto savingDto = new SavingDto();
        savingDto.setId(existingSaving.getId());
        savingDto.setBalance(500.0);
        savingDto.setMaxBalance(5000.0);
        savingDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/saving/withdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(Banking.WITHDREW.name()));

        Saving updatedSaving = savingRepository.findById(existingSaving.getId()).orElse(null);
        assertNotNull(updatedSaving);
        assertEquals(1500.0, updatedSaving.getBalance());
    }

    @Test
    void testWithdrawExceedingBalance() throws Exception {
        Saving existingSaving = new Saving();
        existingSaving.setBalance(500.0);
        existingSaving.setMaxBalance(5000.0);
        existingSaving.setOwner(testUser);
        existingSaving = savingRepository.save(existingSaving);

        SavingDto savingDto = new SavingDto();
        savingDto.setId(existingSaving.getId());
        savingDto.setBalance(1000.0);
        savingDto.setMaxBalance(5000.0);
        savingDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/saving/withdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Attempting to withdraw more than allowed: REFUSED"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testWithdrawWithZeroId() throws Exception {
        SavingDto savingDto = new SavingDto();
        savingDto.setId(0L);
        savingDto.setBalance(500.0);
        savingDto.setMaxBalance(5000.0);
        savingDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/saving/withdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("No savings to withdraw from: REFUSED"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testWithdrawWithNegativeAmount() throws Exception {
        Saving existingSaving = new Saving();
        existingSaving.setBalance(1000.0);
        existingSaving.setMaxBalance(5000.0);
        existingSaving.setOwner(testUser);
        existingSaving = savingRepository.save(existingSaving);

        SavingDto savingDto = new SavingDto();
        savingDto.setId(existingSaving.getId());
        savingDto.setBalance(-100.0);
        savingDto.setMaxBalance(5000.0);
        savingDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/saving/withdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message", containsString("Validation failed for argument")))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testCloseSavingsAccountSuccess() throws Exception {
        Saving existingSaving = new Saving();
        existingSaving.setBalance(0.0);
        existingSaving.setMaxBalance(5000.0);
        existingSaving.setOwner(testUser);
        existingSaving = savingRepository.save(existingSaving);

        SavingDto savingDto = new SavingDto();
        savingDto.setId(existingSaving.getId());
        savingDto.setBalance(0.0);
        savingDto.setMaxBalance(5000.0);
        savingDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/saving/close")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(Banking.COMPLETED.name()));

        boolean savingExists = savingRepository.findById(existingSaving.getId()).isPresent();
        assertEquals(false, savingExists);
    }

    @Test
    void testCloseSavingsAccountWithNonZeroBalance() throws Exception {
        Saving existingSaving = new Saving();
        existingSaving.setBalance(1000.0);
        existingSaving.setMaxBalance(5000.0);
        existingSaving.setOwner(testUser);
        existingSaving = savingRepository.save(existingSaving);

        SavingDto savingDto = new SavingDto();
        savingDto.setId(existingSaving.getId());
        savingDto.setBalance(1000.0);
        savingDto.setMaxBalance(5000.0);
        savingDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/saving/close")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Can not close savings if balance not empty: REFUSED"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testCloseSavingsAccountWithNonExistentSaving() throws Exception {
        SavingDto savingDto = new SavingDto();
        savingDto.setId(999L);
        savingDto.setBalance(0.0);
        savingDto.setMaxBalance(5000.0);
        savingDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/saving/close")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Could not close non existing savings: REFUSED"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testDepositWithInvalidJson() throws Exception {
        mockMvc.perform(post("/api/saving/deposit")
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
        mockMvc.perform(post("/api/saving/withdraw")
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
    void testOpenSavingsAccountWithInvalidJson() throws Exception {
        mockMvc.perform(post("/api/saving/open")
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
    void testCloseSavingsAccountWithInvalidJson() throws Exception {
        mockMvc.perform(post("/api/saving/close")
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
    void testOpenSavingsAccountWithoutAuthorization() throws Exception {
        SavingDto savingDto = new SavingDto();
        savingDto.setId(0L);
        savingDto.setBalance(0.0);
        savingDto.setMaxBalance(10000.0);
        savingDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/saving/open")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDepositWithoutAuthorization() throws Exception {
        SavingDto savingDto = new SavingDto();
        savingDto.setId(1L);
        savingDto.setBalance(500.0);
        savingDto.setMaxBalance(5000.0);
        savingDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/saving/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testWithdrawWithoutAuthorization() throws Exception {
        SavingDto savingDto = new SavingDto();
        savingDto.setId(1L);
        savingDto.setBalance(500.0);
        savingDto.setMaxBalance(5000.0);
        savingDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/saving/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCloseSavingsAccountWithoutAuthorization() throws Exception {
        SavingDto savingDto = new SavingDto();
        savingDto.setId(1L);
        savingDto.setBalance(0.0);
        savingDto.setMaxBalance(5000.0);
        savingDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/saving/close")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testOpenSavingsAccountWithZeroMaxBalanceValidation() throws Exception {
        SavingDto savingDto = new SavingDto();
        savingDto.setId(0L);
        savingDto.setBalance(0.0);
        savingDto.setMaxBalance(0.0);
        savingDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/saving/open")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message", containsString("Validation failed for argument")))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testOpenSavingsAccountWithVeryLargeMaxBalance() throws Exception {
        SavingDto savingDto = new SavingDto();
        savingDto.setId(0L);
        savingDto.setBalance(0.0);
        savingDto.setMaxBalance(Double.MAX_VALUE);
        savingDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/saving/open")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(Banking.AUTHORIZED.name()));

        Saving savedSaving = savingRepository.findAll().stream()
                .filter(saving -> saving.getOwner().getId() == testUser.getId())
                .findFirst()
                .orElse(null);
        assertNotNull(savedSaving);
        assertEquals(Double.MAX_VALUE, savedSaving.getMaxBalance());
    }

    @Test
    void testDepositWithVeryLargeAmount() throws Exception {
        Saving existingSaving = new Saving();
        existingSaving.setBalance(0.0);
        existingSaving.setMaxBalance(Double.MAX_VALUE);
        existingSaving.setOwner(testUser);
        existingSaving = savingRepository.save(existingSaving);

        SavingDto savingDto = new SavingDto();
        savingDto.setId(existingSaving.getId());
        savingDto.setBalance(1000000.0);
        savingDto.setMaxBalance(Double.MAX_VALUE);
        savingDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/saving/deposit")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(Banking.DEPOSITED.name()));

        Saving updatedSaving = savingRepository.findById(existingSaving.getId()).orElse(null);
        assertNotNull(updatedSaving);
        assertEquals(1000000.0, updatedSaving.getBalance());
    }

    @Test
    void testWithdrawWithVeryLargeAmount() throws Exception {
        Saving existingSaving = new Saving();
        existingSaving.setBalance(2000000.0);
        existingSaving.setMaxBalance(Double.MAX_VALUE);
        existingSaving.setOwner(testUser);
        existingSaving = savingRepository.save(existingSaving);

        SavingDto savingDto = new SavingDto();
        savingDto.setId(existingSaving.getId());
        savingDto.setBalance(1000000.0);
        savingDto.setMaxBalance(Double.MAX_VALUE);
        savingDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/saving/withdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(Banking.WITHDREW.name()));

        Saving updatedSaving = savingRepository.findById(existingSaving.getId()).orElse(null);
        assertNotNull(updatedSaving);
        assertEquals(1000000.0, updatedSaving.getBalance());
    }

    @Test
    void testMultipleSavingsAccountsForSameUser() throws Exception {
        SavingDto savingDto1 = new SavingDto();
        savingDto1.setId(0L);
        savingDto1.setBalance(0.0);
        savingDto1.setMaxBalance(5000.0);
        savingDto1.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/saving/open")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(Banking.AUTHORIZED.name()));

        SavingDto savingDto2 = new SavingDto();
        savingDto2.setId(0L);
        savingDto2.setBalance(0.0);
        savingDto2.setMaxBalance(10000.0);
        savingDto2.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/saving/open")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message", containsString("User can only have one savings account: REFUSED")))
                .andExpect(jsonPath("$.path").exists());

        long savingsCount = savingRepository.findAll().stream()
                .filter(saving -> saving.getOwner().getId() == testUser.getId())
                .count();
        assertEquals(1, savingsCount);
    }

    @Test
    void testDepositWithExactMaxBalance() throws Exception {
        Saving existingSaving = new Saving();
        existingSaving.setBalance(0.0);
        existingSaving.setMaxBalance(1000.0);
        existingSaving.setOwner(testUser);
        existingSaving = savingRepository.save(existingSaving);

        SavingDto savingDto = new SavingDto();
        savingDto.setId(existingSaving.getId());
        savingDto.setBalance(1000.0);
        savingDto.setMaxBalance(1000.0);
        savingDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/saving/deposit")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(Banking.DEPOSITED.name()));

        Saving updatedSaving = savingRepository.findById(existingSaving.getId()).orElse(null);
        assertNotNull(updatedSaving);
        assertEquals(1000.0, updatedSaving.getBalance());
    }

    @Test
    void testWithdrawWithExactBalance() throws Exception {
        Saving existingSaving = new Saving();
        existingSaving.setBalance(1000.0);
        existingSaving.setMaxBalance(5000.0);
        existingSaving.setOwner(testUser);
        existingSaving = savingRepository.save(existingSaving);

        SavingDto savingDto = new SavingDto();
        savingDto.setId(existingSaving.getId());
        savingDto.setBalance(1000.0);
        savingDto.setMaxBalance(5000.0);
        savingDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/saving/withdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savingDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(Banking.WITHDREW.name()));

        Saving updatedSaving = savingRepository.findById(existingSaving.getId()).orElse(null);
        assertNotNull(updatedSaving);
        assertEquals(0.0, updatedSaving.getBalance());
    }

    @Test
    void testCompleteSavingsWorkflow() throws Exception {
        SavingDto openDto = new SavingDto();
        openDto.setId(0L);
        openDto.setBalance(0.0);
        openDto.setMaxBalance(10000.0);
        openDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/saving/open")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(openDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(Banking.AUTHORIZED.name()));

        Saving createdSaving = savingRepository.findAll().stream()
                .filter(saving -> saving.getOwner().getId() == testUser.getId())
                .findFirst()
                .orElse(null);
        assertNotNull(createdSaving);

        SavingDto depositDto = new SavingDto();
        depositDto.setId(createdSaving.getId());
        depositDto.setBalance(2000.0);
        depositDto.setMaxBalance(10000.0);
        depositDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/saving/deposit")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(Banking.DEPOSITED.name()));

        SavingDto withdrawDto = new SavingDto();
        withdrawDto.setId(createdSaving.getId());
        withdrawDto.setBalance(500.0);
        withdrawDto.setMaxBalance(10000.0);
        withdrawDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/saving/withdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(Banking.WITHDREW.name()));

        SavingDto finalWithdrawDto = new SavingDto();
        finalWithdrawDto.setId(createdSaving.getId());
        finalWithdrawDto.setBalance(1500.0);
        finalWithdrawDto.setMaxBalance(10000.0);
        finalWithdrawDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/saving/withdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(finalWithdrawDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(Banking.WITHDREW.name()));

        SavingDto closeDto = new SavingDto();
        closeDto.setId(createdSaving.getId());
        closeDto.setBalance(0.0);
        closeDto.setMaxBalance(10000.0);
        closeDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/saving/close")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(closeDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(Banking.COMPLETED.name()));

        boolean savingExists = savingRepository.findById(createdSaving.getId()).isPresent();
        assertEquals(false, savingExists);
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
