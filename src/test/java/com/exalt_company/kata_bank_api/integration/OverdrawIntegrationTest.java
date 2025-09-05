package com.exalt_company.kata_bank_api.integration;

import com.exalt_company.kata_bank_api.dto.fund.FundOpDto;
import com.exalt_company.kata_bank_api.dto.fund.OverdrawDto;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class OverdrawIntegrationTest {
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
    void testRequestOverdrawCapabilitiesSuccess() throws Exception {
        Fund existingFund = new Fund();
        existingFund.setBalance(1000.0);
        existingFund.setOwner(testUser);
        existingFund.setCanOverdraw(false);
        existingFund.setMaxOverdraw(0.0);
        existingFund = fundRepository.save(existingFund);

        OverdrawDto overdrawDto = new OverdrawDto();
        overdrawDto.setId(existingFund.getId());
        overdrawDto.setMaxOverdraw(500.0);
        overdrawDto.setOwnerId(testUser.getId());

        mockMvc.perform(put("/api/fund/request-overdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overdrawDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(Banking.AUTHORIZED.name()));

        Fund updatedFund = fundRepository.findById(existingFund.getId()).orElse(null);
        assertNotNull(updatedFund);
        assertTrue(updatedFund.canOverdraw());
        assertEquals(500.0, updatedFund.getMaxOverdraw());
    }

    @Test
    void testRequestOverdrawCapabilitiesWithNonExistentFund() throws Exception {
        OverdrawDto overdrawDto = new OverdrawDto();
        overdrawDto.setId(999L);
        overdrawDto.setMaxOverdraw(500.0);
        overdrawDto.setOwnerId(testUser.getId());

        mockMvc.perform(put("/api/fund/request-overdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overdrawDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("No funds to overdraw"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testRequestOverdrawCapabilitiesWithZeroId() throws Exception {
        OverdrawDto overdrawDto = new OverdrawDto();
        overdrawDto.setId(0L);
        overdrawDto.setMaxOverdraw(500.0);
        overdrawDto.setOwnerId(testUser.getId());

        mockMvc.perform(put("/api/fund/request-overdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overdrawDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("No funds to overdraw"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testRequestOverdrawCapabilitiesUnauthorizedAccess() throws Exception {
        BankUser otherUser = createTestUser("other", "user", "otheruser@example.com", "password123", BankRole.CLIENT);
        otherUser = bankUserRepository.save(otherUser);

        Fund otherUserFund = new Fund();
        otherUserFund.setBalance(1000.0);
        otherUserFund.setOwner(otherUser);
        otherUserFund = fundRepository.save(otherUserFund);

        OverdrawDto overdrawDto = new OverdrawDto();
        overdrawDto.setId(otherUserFund.getId());
        overdrawDto.setMaxOverdraw(500.0);
        overdrawDto.setOwnerId(otherUser.getId());

        mockMvc.perform(put("/api/fund/request-overdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overdrawDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Attempted to access unauthorized funds"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testCancelOverdrawCapabilitiesSuccess() throws Exception {
        Fund existingFund = new Fund();
        existingFund.setBalance(100.0);
        existingFund.setOwner(testUser);
        existingFund.setCanOverdraw(true);
        existingFund.setMaxOverdraw(500.0);
        existingFund = fundRepository.save(existingFund);

        OverdrawDto overdrawDto = new OverdrawDto();
        overdrawDto.setId(existingFund.getId());
        overdrawDto.setMaxOverdraw(0.0);
        overdrawDto.setOwnerId(testUser.getId());

        mockMvc.perform(put("/api/fund/cancel-overdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overdrawDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(Banking.COMPLETED.name()));

        Fund updatedFund = fundRepository.findById(existingFund.getId()).orElse(null);
        assertNotNull(updatedFund);
        assertFalse(updatedFund.canOverdraw());
        assertEquals(0.0, updatedFund.getMaxOverdraw());
    }

    @Test
    void testCancelOverdrawCapabilitiesWithNegativeBalance() throws Exception {
        Fund existingFund = new Fund();
        existingFund.setBalance(-100.0);
        existingFund.setOwner(testUser);
        existingFund.setCanOverdraw(true);
        existingFund.setMaxOverdraw(500.0);
        existingFund = fundRepository.save(existingFund);

        OverdrawDto overdrawDto = new OverdrawDto();
        overdrawDto.setId(existingFund.getId());
        overdrawDto.setMaxOverdraw(0.0);
        overdrawDto.setOwnerId(testUser.getId());

        mockMvc.perform(put("/api/fund/cancel-overdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overdrawDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Cannot cancel overdraw when balance is negative"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testCancelOverdrawCapabilitiesWithNonExistentFund() throws Exception {
        OverdrawDto overdrawDto = new OverdrawDto();
        overdrawDto.setId(999L);
        overdrawDto.setMaxOverdraw(0.0);
        overdrawDto.setOwnerId(testUser.getId());

        mockMvc.perform(put("/api/fund/cancel-overdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overdrawDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("No funds overdrawn to cancel"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testCancelOverdrawCapabilitiesWithZeroId() throws Exception {
        OverdrawDto overdrawDto = new OverdrawDto();
        overdrawDto.setId(0L);
        overdrawDto.setMaxOverdraw(0.0);
        overdrawDto.setOwnerId(testUser.getId());

        mockMvc.perform(put("/api/fund/cancel-overdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overdrawDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("No funds overdrawn to cancel"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testWithdrawWithOverdrawEnabledSuccess() throws Exception {
        Fund existingFund = new Fund();
        existingFund.setBalance(100.0);
        existingFund.setOwner(testUser);
        existingFund.setCanOverdraw(true);
        existingFund.setMaxOverdraw(500.0);
        existingFund = fundRepository.save(existingFund);

        FundOpDto fundOpDto = new FundOpDto();
        fundOpDto.setId(existingFund.getId());
        fundOpDto.setBalance(200.0);
        fundOpDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/fund/withdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundOpDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(Banking.WITHDREW.name()));

        Fund updatedFund = fundRepository.findById(existingFund.getId()).orElse(null);
        assertNotNull(updatedFund);
        assertEquals(-100.0, updatedFund.getBalance());
    }

    @Test
    void testWithdrawWithOverdrawEnabledExceedingLimit() throws Exception {
        Fund existingFund = new Fund();
        existingFund.setBalance(100.0);
        existingFund.setOwner(testUser);
        existingFund.setCanOverdraw(true);
        existingFund.setMaxOverdraw(500.0);
        existingFund = fundRepository.save(existingFund);

        FundOpDto fundOpDto = new FundOpDto();
        fundOpDto.setId(existingFund.getId());
        fundOpDto.setBalance(700.0);
        fundOpDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/fund/withdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundOpDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Attempting to withdraw more than allowed"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testWithdrawWithOverdrawDisabledExceedingBalance() throws Exception {
        Fund existingFund = new Fund();
        existingFund.setBalance(100.0);
        existingFund.setOwner(testUser);
        existingFund.setCanOverdraw(false);
        existingFund.setMaxOverdraw(0.0);
        existingFund = fundRepository.save(existingFund);

        FundOpDto fundOpDto = new FundOpDto();
        fundOpDto.setId(existingFund.getId());
        fundOpDto.setBalance(200.0);
        fundOpDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/fund/withdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundOpDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Attempting to withdraw more than available"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testWithdrawWithOverdrawEnabledAtLimit() throws Exception {
        Fund existingFund = new Fund();
        existingFund.setBalance(100.0);
        existingFund.setOwner(testUser);
        existingFund.setCanOverdraw(true);
        existingFund.setMaxOverdraw(500.0);
        existingFund = fundRepository.save(existingFund);

        FundOpDto fundOpDto = new FundOpDto();
        fundOpDto.setId(existingFund.getId());
        fundOpDto.setBalance(600.0);
        fundOpDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/fund/withdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundOpDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(Banking.WITHDREW.name()));

        Fund updatedFund = fundRepository.findById(existingFund.getId()).orElse(null);
        assertNotNull(updatedFund);
        assertEquals(-500.0, updatedFund.getBalance());
    }

    @Test
    void testWithdrawWithOverdrawEnabledWithinBalance() throws Exception {
        Fund existingFund = new Fund();
        existingFund.setBalance(1000.0);
        existingFund.setOwner(testUser);
        existingFund.setCanOverdraw(true);
        existingFund.setMaxOverdraw(500.0);
        existingFund = fundRepository.save(existingFund);

        FundOpDto fundOpDto = new FundOpDto();
        fundOpDto.setId(existingFund.getId());
        fundOpDto.setBalance(300.0);
        fundOpDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/fund/withdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundOpDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(Banking.WITHDREW.name()));

        Fund updatedFund = fundRepository.findById(existingFund.getId()).orElse(null);
        assertNotNull(updatedFund);
        assertEquals(700.0, updatedFund.getBalance());
    }

    @Test
    void testWithdrawWithOverdrawEnabledZeroBalance() throws Exception {
        Fund existingFund = new Fund();
        existingFund.setBalance(0.0);
        existingFund.setOwner(testUser);
        existingFund.setCanOverdraw(true);
        existingFund.setMaxOverdraw(500.0);
        existingFund = fundRepository.save(existingFund);

        FundOpDto fundOpDto = new FundOpDto();
        fundOpDto.setId(existingFund.getId());
        fundOpDto.setBalance(200.0);
        fundOpDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/fund/withdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundOpDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(Banking.WITHDREW.name()));

        Fund updatedFund = fundRepository.findById(existingFund.getId()).orElse(null);
        assertNotNull(updatedFund);
        assertEquals(-200.0, updatedFund.getBalance());
    }

    @Test
    void testWithdrawWithOverdrawEnabledNegativeBalance() throws Exception {
        Fund existingFund = new Fund();
        existingFund.setBalance(-100.0);
        existingFund.setOwner(testUser);
        existingFund.setCanOverdraw(true);
        existingFund.setMaxOverdraw(500.0);
        existingFund = fundRepository.save(existingFund);

        FundOpDto fundOpDto = new FundOpDto();
        fundOpDto.setId(existingFund.getId());
        fundOpDto.setBalance(200.0);
        fundOpDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/fund/withdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundOpDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(Banking.WITHDREW.name()));

        Fund updatedFund = fundRepository.findById(existingFund.getId()).orElse(null);
        assertNotNull(updatedFund);
        assertEquals(-300.0, updatedFund.getBalance());
    }

    @Test
    void testRequestOverdrawCapabilitiesWithoutAuthorization() throws Exception {
        OverdrawDto overdrawDto = new OverdrawDto();
        overdrawDto.setId(1L);
        overdrawDto.setMaxOverdraw(500.0);
        overdrawDto.setOwnerId(testUser.getId());

        mockMvc.perform(put("/api/fund/request-overdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overdrawDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCancelOverdrawCapabilitiesWithoutAuthorization() throws Exception {
        OverdrawDto overdrawDto = new OverdrawDto();
        overdrawDto.setId(1L);
        overdrawDto.setMaxOverdraw(0.0);
        overdrawDto.setOwnerId(testUser.getId());

        mockMvc.perform(put("/api/fund/cancel-overdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overdrawDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testRequestOverdrawCapabilitiesWithInvalidJson() throws Exception {
        mockMvc.perform(put("/api/fund/request-overdraw")
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
    void testCancelOverdrawCapabilitiesWithInvalidJson() throws Exception {
        mockMvc.perform(put("/api/fund/cancel-overdraw")
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
    void testRequestOverdrawCapabilitiesWithNegativeMaxOverdraw() throws Exception {
        Fund existingFund = new Fund();
        existingFund.setBalance(1000.0);
        existingFund.setOwner(testUser);
        existingFund.setCanOverdraw(false);
        existingFund.setMaxOverdraw(0.0);
        existingFund = fundRepository.save(existingFund);

        OverdrawDto overdrawDto = new OverdrawDto();
        overdrawDto.setId(existingFund.getId());
        overdrawDto.setMaxOverdraw(-100.0);
        overdrawDto.setOwnerId(testUser.getId());

        mockMvc.perform(put("/api/fund/request-overdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overdrawDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Wrong value for max overdraw"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testRequestOverdrawCapabilitiesWithZeroMaxOverdraw() throws Exception {
        Fund existingFund = new Fund();
        existingFund.setBalance(1000.0);
        existingFund.setOwner(testUser);
        existingFund.setCanOverdraw(false);
        existingFund.setMaxOverdraw(0.0);
        existingFund = fundRepository.save(existingFund);

        OverdrawDto overdrawDto = new OverdrawDto();
        overdrawDto.setId(existingFund.getId());
        overdrawDto.setMaxOverdraw(0.0);
        overdrawDto.setOwnerId(testUser.getId());

        mockMvc.perform(put("/api/fund/request-overdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overdrawDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Wrong value for max overdraw"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testRequestOverdrawCapabilitiesOnAlreadyOverdrawEnabledFund() throws Exception {
        Fund existingFund = new Fund();
        existingFund.setBalance(1000.0);
        existingFund.setOwner(testUser);
        existingFund.setCanOverdraw(true);
        existingFund.setMaxOverdraw(500.0);
        existingFund = fundRepository.save(existingFund);

        OverdrawDto overdrawDto = new OverdrawDto();
        overdrawDto.setId(existingFund.getId());
        overdrawDto.setMaxOverdraw(1000.0);
        overdrawDto.setOwnerId(testUser.getId());

        mockMvc.perform(put("/api/fund/request-overdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overdrawDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(Banking.AUTHORIZED.name()));

        Fund updatedFund = fundRepository.findById(existingFund.getId()).orElse(null);
        assertNotNull(updatedFund);
        assertTrue(updatedFund.canOverdraw());
        assertEquals(1000.0, updatedFund.getMaxOverdraw());
    }

    @Test
    void testCancelOverdrawCapabilitiesOnNonOverdrawEnabledFund() throws Exception {
        Fund existingFund = new Fund();
        existingFund.setBalance(1000.0);
        existingFund.setOwner(testUser);
        existingFund.setCanOverdraw(false);
        existingFund.setMaxOverdraw(0.0);
        existingFund = fundRepository.save(existingFund);

        OverdrawDto overdrawDto = new OverdrawDto();
        overdrawDto.setId(existingFund.getId());
        overdrawDto.setMaxOverdraw(0.0);
        overdrawDto.setOwnerId(testUser.getId());

        mockMvc.perform(put("/api/fund/cancel-overdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overdrawDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("No funds overdrawn to cancel"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testCompleteOverdrawWorkflow() throws Exception {
        // 1. Create fund
        Fund existingFund = new Fund();
        existingFund.setBalance(1000.0);
        existingFund.setOwner(testUser);
        existingFund.setCanOverdraw(false);
        existingFund.setMaxOverdraw(0.0);
        existingFund = fundRepository.save(existingFund);

        // 2. Request overdraw capabilities
        OverdrawDto requestDto = new OverdrawDto();
        requestDto.setId(existingFund.getId());
        requestDto.setMaxOverdraw(500.0);
        requestDto.setOwnerId(testUser.getId());

        mockMvc.perform(put("/api/fund/request-overdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(Banking.AUTHORIZED.name()));

        // 3. Withdraw more than balance (using overdraw)
        FundOpDto withdrawDto = new FundOpDto();
        withdrawDto.setId(existingFund.getId());
        withdrawDto.setBalance(1200.0); // 1000 + 200 overdraw
        withdrawDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/fund/withdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(Banking.WITHDREW.name()));

        // 4. Deposit to restore positive balance
        FundOpDto depositDto = new FundOpDto();
        depositDto.setId(existingFund.getId());
        depositDto.setBalance(300.0);
        depositDto.setOwnerId(testUser.getId());

        mockMvc.perform(post("/api/fund/deposit")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(Banking.DEPOSITED.name()));

        // 5. Cancel overdraw capabilities
        OverdrawDto cancelDto = new OverdrawDto();
        cancelDto.setId(existingFund.getId());
        cancelDto.setMaxOverdraw(0.0);
        cancelDto.setOwnerId(testUser.getId());

        mockMvc.perform(put("/api/fund/cancel-overdraw")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(Banking.COMPLETED.name()));

        // Verify final state
        Fund finalFund = fundRepository.findById(existingFund.getId()).orElse(null);
        assertNotNull(finalFund);
        assertEquals(100.0, finalFund.getBalance());
        assertFalse(finalFund.canOverdraw());
        assertEquals(0.0, finalFund.getMaxOverdraw());
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
