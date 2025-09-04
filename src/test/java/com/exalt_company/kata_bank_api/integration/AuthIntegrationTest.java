package com.exalt_company.kata_bank_api.integration;

import com.exalt_company.kata_bank_api.dto.auth.AuthenticationRequest;
import com.exalt_company.kata_bank_api.dto.auth.RegisterRequest;
import com.exalt_company.kata_bank_api.entity.BankUser;
import com.exalt_company.kata_bank_api.entity.user_fields.Credentials;
import com.exalt_company.kata_bank_api.entity.user_fields.Identity;
import com.exalt_company.kata_bank_api.enums.BankRole;
import com.exalt_company.kata_bank_api.repository.BankUserRepository;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class AuthIntegrationTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private BankUserRepository bankUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        bankUserRepository.deleteAll();
    }

    @Test
    void testCompleteRegistrationFlow() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("new");
        request.setSurname("user");
        request.setEmail("newuser@example.com");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty());

        BankUser savedUser = bankUserRepository.findByCredentialsEmail("newuser@example.com").orElse(null);
        assertNotNull(savedUser);
        assertEquals("new", savedUser.getIdentity().getName());
        assertEquals("user", savedUser.getIdentity().getSurname());
        assertEquals("newuser@example.com", savedUser.getCredentials().getEmail());
        assertTrue(passwordEncoder.matches("password123", savedUser.getCredentials().getPassword()));
        assertEquals(BankRole.ADMIN, savedUser.getBankRole());
    }

    @Test
    void testCompleteAuthenticationFlow() throws Exception {
        BankUser user = createTestUser(
                "test",
                "user",
                "testuser@example.com",
                "password123",
                BankRole.CLIENT);
        bankUserRepository.save(user);

        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("testuser@example.com");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void testRegistrationWithExistingEmail() throws Exception {
        BankUser existingUser = createTestUser(
                "New",
                "User",
                "existing@example.com",
                "password123",
                BankRole.CLIENT);
        bankUserRepository.save(existingUser);

        RegisterRequest request = new RegisterRequest();
        request.setName("New");
        request.setSurname("User");
        request.setEmail("existing@example.com");
        request.setPassword("newpassword123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("User with this email already exists"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testAuthenticationWithWrongPassword() throws Exception {
        BankUser user = createTestUser(
                "test",
                "user",
                "testuser@example.com",
                "password123",
                BankRole.CLIENT);
        bankUserRepository.save(user);

        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("testuser@example.com");
        request.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Bad credentials"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testAuthenticationWithNonExistentUser() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("nonexistent@example.com");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Bad credentials"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testAuthenticationWithBadCredentialsException() throws Exception {
        BankUser user = createTestUser(
                "test",
                "user",
                "testuser@example.com",
                "password123",
                BankRole.CLIENT);
        bankUserRepository.save(user);

        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("testuser@example.com");
        request.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Bad credentials"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testRegistrationWithInvalidJson() throws Exception {
        mockMvc.perform(post("/api/auth/register")
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
    void testAuthenticationWithInvalidJson() throws Exception {
        mockMvc.perform(post("/api/auth/authenticate")
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
    void testMultipleRegistrations() throws Exception {
        RegisterRequest request1 = new RegisterRequest();
        request1.setName("User");
        request1.setSurname("One");
        request1.setEmail("user1@example.com");
        request1.setPassword("password1");

        RegisterRequest request2 = new RegisterRequest();
        request2.setName("User");
        request2.setSurname("Two");
        request2.setEmail("user2@example.com");
        request2.setPassword("password2");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        BankUser user1 = bankUserRepository.findByCredentialsEmail("user1@example.com").orElse(null);
        assertNotNull(user1);
        assertEquals(BankRole.ADMIN, user1.getBankRole());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        BankUser user2 = bankUserRepository.findByCredentialsEmail("user2@example.com").orElse(null);
        assertNotNull(user2);
        assertEquals(BankRole.CLIENT, user2.getBankRole());
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