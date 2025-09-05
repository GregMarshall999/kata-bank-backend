package com.exalt_company.kata_bank_api.integration;

import com.exalt_company.kata_bank_api.dto.PasswordedBankUserDto;
import com.exalt_company.kata_bank_api.entity.BankUser;
import com.exalt_company.kata_bank_api.entity.user_fields.Credentials;
import com.exalt_company.kata_bank_api.entity.user_fields.Identity;
import com.exalt_company.kata_bank_api.enums.BankRole;
import com.exalt_company.kata_bank_api.repository.BankUserRepository;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class BankUserIntegrationTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private BankUserRepository bankUserRepository;

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

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        bankUserRepository.deleteAll();

        // Create admin user
        adminUser = createTestUser("Admin", "User", "admin@example.com", "admin123", BankRole.ADMIN);
        adminUser = bankUserRepository.save(adminUser);
        adminToken = jwtService.generateToken(adminUser, adminUser.getId(), adminUser.getBankRole());

        // Create client user
        clientUser = createTestUser("Client", "User", "client@example.com", "client123", BankRole.CLIENT);
        clientUser = bankUserRepository.save(clientUser);
        clientToken = jwtService.generateToken(clientUser, clientUser.getId(), clientUser.getBankRole());
    }

    // Validation Integration Tests
    @Test
    void testCreateBankUserWithNullName() throws Exception {
        PasswordedBankUserDto userDto = new PasswordedBankUserDto();
        userDto.setId(0L);
        userDto.setName(null); // Null name
        userDto.setSurname("User");
        userDto.setEmail("newuser@example.com");
        userDto.setPassword("password123");
        userDto.setBankRole(BankRole.CLIENT);

        mockMvc.perform(post("/api/bank-user")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testCreateBankUserWithBlankName() throws Exception {
        PasswordedBankUserDto userDto = new PasswordedBankUserDto();
        userDto.setId(0L);
        userDto.setName(""); // Blank name
        userDto.setSurname("User");
        userDto.setEmail("newuser@example.com");
        userDto.setPassword("password123");
        userDto.setBankRole(BankRole.CLIENT);

        mockMvc.perform(post("/api/bank-user")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testCreateBankUserWithShortName() throws Exception {
        PasswordedBankUserDto userDto = new PasswordedBankUserDto();
        userDto.setId(0L);
        userDto.setName("A"); // Less than 2 characters
        userDto.setSurname("User");
        userDto.setEmail("newuser@example.com");
        userDto.setPassword("password123");
        userDto.setBankRole(BankRole.CLIENT);

        mockMvc.perform(post("/api/bank-user")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testCreateBankUserWithLongName() throws Exception {
        PasswordedBankUserDto userDto = new PasswordedBankUserDto();
        userDto.setId(0L);
        userDto.setName("A".repeat(51)); // More than 50 characters
        userDto.setSurname("User");
        userDto.setEmail("newuser@example.com");
        userDto.setPassword("password123");
        userDto.setBankRole(BankRole.CLIENT);

        mockMvc.perform(post("/api/bank-user")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testCreateBankUserWithNullSurname() throws Exception {
        PasswordedBankUserDto userDto = new PasswordedBankUserDto();
        userDto.setId(0L);
        userDto.setName("New");
        userDto.setSurname(null); // Null surname
        userDto.setEmail("newuser@example.com");
        userDto.setPassword("password123");
        userDto.setBankRole(BankRole.CLIENT);

        mockMvc.perform(post("/api/bank-user")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testCreateBankUserWithBlankSurname() throws Exception {
        PasswordedBankUserDto userDto = new PasswordedBankUserDto();
        userDto.setId(0L);
        userDto.setName("New");
        userDto.setSurname(""); // Blank surname
        userDto.setEmail("newuser@example.com");
        userDto.setPassword("password123");
        userDto.setBankRole(BankRole.CLIENT);

        mockMvc.perform(post("/api/bank-user")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testCreateBankUserWithShortSurname() throws Exception {
        PasswordedBankUserDto userDto = new PasswordedBankUserDto();
        userDto.setId(0L);
        userDto.setName("New");
        userDto.setSurname("U"); // Less than 2 characters
        userDto.setEmail("newuser@example.com");
        userDto.setPassword("password123");
        userDto.setBankRole(BankRole.CLIENT);

        mockMvc.perform(post("/api/bank-user")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testCreateBankUserWithLongSurname() throws Exception {
        PasswordedBankUserDto userDto = new PasswordedBankUserDto();
        userDto.setId(0L);
        userDto.setName("New");
        userDto.setSurname("B".repeat(51)); // More than 50 characters
        userDto.setEmail("newuser@example.com");
        userDto.setPassword("password123");
        userDto.setBankRole(BankRole.CLIENT);

        mockMvc.perform(post("/api/bank-user")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testCreateBankUserWithNullEmail() throws Exception {
        PasswordedBankUserDto userDto = new PasswordedBankUserDto();
        userDto.setId(0L);
        userDto.setName("New");
        userDto.setSurname("User");
        userDto.setEmail(null); // Null email
        userDto.setPassword("password123");
        userDto.setBankRole(BankRole.CLIENT);

        mockMvc.perform(post("/api/bank-user")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testCreateBankUserWithBlankEmail() throws Exception {
        PasswordedBankUserDto userDto = new PasswordedBankUserDto();
        userDto.setId(0L);
        userDto.setName("New");
        userDto.setSurname("User");
        userDto.setEmail(""); // Blank email
        userDto.setPassword("password123");
        userDto.setBankRole(BankRole.CLIENT);

        mockMvc.perform(post("/api/bank-user")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testCreateBankUserWithInvalidEmailFormat() throws Exception {
        PasswordedBankUserDto userDto = new PasswordedBankUserDto();
        userDto.setId(0L);
        userDto.setName("New");
        userDto.setSurname("User");
        userDto.setEmail("invalid-email-format"); // Invalid email format
        userDto.setPassword("password123");
        userDto.setBankRole(BankRole.CLIENT);

        mockMvc.perform(post("/api/bank-user")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testCreateBankUserWithNullBankRole() throws Exception {
        PasswordedBankUserDto userDto = new PasswordedBankUserDto();
        userDto.setId(0L);
        userDto.setName("New");
        userDto.setSurname("User");
        userDto.setEmail("newuser@example.com");
        userDto.setPassword("password123");
        userDto.setBankRole(null); // Null bank role

        mockMvc.perform(post("/api/bank-user")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testCreateBankUserWithNegativeAdvisorId() throws Exception {
        PasswordedBankUserDto userDto = new PasswordedBankUserDto();
        userDto.setId(0L);
        userDto.setName("New");
        userDto.setSurname("User");
        userDto.setEmail("newuser@example.com");
        userDto.setPassword("password123");
        userDto.setBankRole(BankRole.CLIENT);
        userDto.setAdvisorId(-1L); // Negative advisor ID

        mockMvc.perform(post("/api/bank-user")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testCreateBankUserWithZeroAdvisorId() throws Exception {
        PasswordedBankUserDto userDto = new PasswordedBankUserDto();
        userDto.setId(0L);
        userDto.setName("New");
        userDto.setSurname("User");
        userDto.setEmail("newuser@example.com");
        userDto.setPassword("password123");
        userDto.setBankRole(BankRole.CLIENT);
        userDto.setAdvisorId(0L); // Zero advisor ID

        mockMvc.perform(post("/api/bank-user")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testCreateBankUserWithNullPassword() throws Exception {
        PasswordedBankUserDto userDto = new PasswordedBankUserDto();
        userDto.setId(0L);
        userDto.setName("New");
        userDto.setSurname("User");
        userDto.setEmail("newuser@example.com");
        userDto.setPassword(null); // Null password
        userDto.setBankRole(BankRole.CLIENT);

        mockMvc.perform(post("/api/bank-user")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testCreateBankUserWithBlankPassword() throws Exception {
        PasswordedBankUserDto userDto = new PasswordedBankUserDto();
        userDto.setId(0L);
        userDto.setName("New");
        userDto.setSurname("User");
        userDto.setEmail("newuser@example.com");
        userDto.setPassword(""); // Blank password
        userDto.setBankRole(BankRole.CLIENT);

        mockMvc.perform(post("/api/bank-user")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testCreateBankUserWithShortPassword() throws Exception {
        PasswordedBankUserDto userDto = new PasswordedBankUserDto();
        userDto.setId(0L);
        userDto.setName("New");
        userDto.setSurname("User");
        userDto.setEmail("newuser@example.com");
        userDto.setPassword("12345"); // Less than 6 characters
        userDto.setBankRole(BankRole.CLIENT);

        mockMvc.perform(post("/api/bank-user")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testCreateBankUserWithLongPassword() throws Exception {
        PasswordedBankUserDto userDto = new PasswordedBankUserDto();
        userDto.setId(0L);
        userDto.setName("New");
        userDto.setSurname("User");
        userDto.setEmail("newuser@example.com");
        userDto.setPassword("a".repeat(101)); // More than 100 characters
        userDto.setBankRole(BankRole.CLIENT);

        mockMvc.perform(post("/api/bank-user")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testCreateBankUserAsAdmin() throws Exception {
        PasswordedBankUserDto userDto = new PasswordedBankUserDto();
        userDto.setId(0L);
        userDto.setName("New");
        userDto.setSurname("User");
        userDto.setEmail("newuser@example.com");
        userDto.setPassword("password123");
        userDto.setBankRole(BankRole.CLIENT);

        mockMvc.perform(post("/api/bank-user")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("New"))
                .andExpect(jsonPath("$.surname").value("User"))
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.bankRole").value("CLIENT"))
                .andExpect(jsonPath("$.password").doesNotExist()); // Password should not be returned

        // Verify user was created in database
        BankUser savedUser = bankUserRepository.findByCredentialsEmail("newuser@example.com").orElse(null);
        assertNotNull(savedUser);
        assertEquals("New", savedUser.getIdentity().getName());
        assertEquals("User", savedUser.getIdentity().getSurname());
        assertEquals("newuser@example.com", savedUser.getCredentials().getEmail());
        assertEquals(BankRole.CLIENT, savedUser.getBankRole());
        assertTrue(passwordEncoder.matches("password123", savedUser.getCredentials().getPassword()));
    }

    @Test
    void testCreateBankUserAsClient_ShouldFail() throws Exception {
        PasswordedBankUserDto userDto = new PasswordedBankUserDto();
        userDto.setId(0L);
        userDto.setName("New");
        userDto.setSurname("User");
        userDto.setEmail("newuser@example.com");
        userDto.setPassword("password123");
        userDto.setBankRole(BankRole.CLIENT);

        mockMvc.perform(post("/api/bank-user")
                        .header("Authorization", clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateBankUserWithoutAuthorization_ShouldFail() throws Exception {
        PasswordedBankUserDto userDto = new PasswordedBankUserDto();
        userDto.setId(0L);
        userDto.setName("New");
        userDto.setSurname("User");
        userDto.setEmail("newuser@example.com");
        userDto.setPassword("password123");
        userDto.setBankRole(BankRole.CLIENT);

        mockMvc.perform(post("/api/bank-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetBankUserByIdAsAdmin() throws Exception {
        mockMvc.perform(get("/api/bank-user/{id}", clientUser.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(clientUser.getId()))
                .andExpect(jsonPath("$.name").value("Client"))
                .andExpect(jsonPath("$.surname").value("User"))
                .andExpect(jsonPath("$.email").value("client@example.com"))
                .andExpect(jsonPath("$.bankRole").value("CLIENT"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void testGetBankUserByIdAsClient_ShouldFail() throws Exception {
        mockMvc.perform(get("/api/bank-user/{id}", adminUser.getId())
                        .header("Authorization", clientToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetBankUserByIdWithoutAuthorization_ShouldFail() throws Exception {
        mockMvc.perform(get("/api/bank-user/{id}", clientUser.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetAllBankUsersAsAdmin() throws Exception {
        mockMvc.perform(get("/api/bank-user")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].password").doesNotExist())
                .andExpect(jsonPath("$[1].password").doesNotExist());
    }

    @Test
    void testGetAllBankUsersAsClient_ShouldFail() throws Exception {
        mockMvc.perform(get("/api/bank-user")
                        .header("Authorization", clientToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetAllBankUsersWithoutAuthorization_ShouldFail() throws Exception {
        mockMvc.perform(get("/api/bank-user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetBankUsersPageAsAdmin() throws Exception {
        mockMvc.perform(get("/api/bank-user/page")
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    void testGetBankUsersPageAsClient_ShouldFail() throws Exception {
        mockMvc.perform(get("/api/bank-user/page")
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", clientToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateBankUserAsAdmin() throws Exception {
        PasswordedBankUserDto userDto = new PasswordedBankUserDto();
        userDto.setId(clientUser.getId());
        userDto.setName("Updated");
        userDto.setSurname("Client");
        userDto.setEmail("updated@example.com");
        userDto.setPassword("newpassword123");
        userDto.setBankRole(BankRole.CLIENT);

        mockMvc.perform(put("/api/bank-user/{id}", clientUser.getId())
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(clientUser.getId()))
                .andExpect(jsonPath("$.name").value("Updated"))
                .andExpect(jsonPath("$.surname").value("Client"))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.bankRole").value("CLIENT"))
                .andExpect(jsonPath("$.password").doesNotExist());

        // Verify user was updated in database
        BankUser updatedUser = bankUserRepository.findById(clientUser.getId()).orElse(null);
        assertNotNull(updatedUser);
        assertEquals("Updated", updatedUser.getIdentity().getName());
        assertEquals("Client", updatedUser.getIdentity().getSurname());
        assertEquals("updated@example.com", updatedUser.getCredentials().getEmail());
        assertTrue(passwordEncoder.matches("newpassword123", updatedUser.getCredentials().getPassword()));
    }

    @Test
    void testUpdateBankUserAsClient_ShouldFail() throws Exception {
        PasswordedBankUserDto userDto = new PasswordedBankUserDto();
        userDto.setId(adminUser.getId());
        userDto.setName("Hacked");
        userDto.setSurname("Admin");
        userDto.setEmail("hacked@example.com");
        userDto.setPassword("hacked123");
        userDto.setBankRole(BankRole.ADMIN);

        mockMvc.perform(put("/api/bank-user/{id}", adminUser.getId())
                        .header("Authorization", clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateBankUserWithoutAuthorization_ShouldFail() throws Exception {
        PasswordedBankUserDto userDto = new PasswordedBankUserDto();
        userDto.setId(clientUser.getId());
        userDto.setName("Updated");
        userDto.setSurname("Client");
        userDto.setEmail("updated@example.com");
        userDto.setPassword("newpassword123");
        userDto.setBankRole(BankRole.CLIENT);

        mockMvc.perform(put("/api/bank-user/{id}", clientUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDeleteBankUserByIdAsAdmin() throws Exception {
        mockMvc.perform(delete("/api/bank-user/{id}", clientUser.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(true));

        // Verify user was deleted from database
        boolean userExists = bankUserRepository.findById(clientUser.getId()).isPresent();
        assertEquals(false, userExists);
    }

    @Test
    void testDeleteBankUserByIdAsClient_ShouldFail() throws Exception {
        mockMvc.perform(delete("/api/bank-user/{id}", adminUser.getId())
                        .header("Authorization", clientToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteBankUserByIdWithoutAuthorization_ShouldFail() throws Exception {
        mockMvc.perform(delete("/api/bank-user/{id}", clientUser.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDeleteBankUserByDtoAsAdmin() throws Exception {
        PasswordedBankUserDto userDto = new PasswordedBankUserDto();
        userDto.setId(clientUser.getId());
        userDto.setName("Client");
        userDto.setSurname("User");
        userDto.setEmail("client@example.com");
        userDto.setPassword("client123");
        userDto.setBankRole(BankRole.CLIENT);

        mockMvc.perform(delete("/api/bank-user")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(true));

        // Verify user was deleted from database
        boolean userExists = bankUserRepository.findById(clientUser.getId()).isPresent();
        assertEquals(false, userExists);
    }

    @Test
    void testDeleteBankUserByDtoAsClient_ShouldFail() throws Exception {
        PasswordedBankUserDto userDto = new PasswordedBankUserDto();
        userDto.setId(adminUser.getId());
        userDto.setName("Admin");
        userDto.setSurname("User");
        userDto.setEmail("admin@example.com");
        userDto.setPassword("admin123");
        userDto.setBankRole(BankRole.ADMIN);

        mockMvc.perform(delete("/api/bank-user")
                        .header("Authorization", clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateBankUserWithDuplicateEmail_ShouldFail() throws Exception {
        PasswordedBankUserDto userDto = new PasswordedBankUserDto();
        userDto.setId(0L);
        userDto.setName("Duplicate");
        userDto.setSurname("User");
        userDto.setEmail("client@example.com"); // Same email as existing client
        userDto.setPassword("password123");
        userDto.setBankRole(BankRole.CLIENT);

        mockMvc.perform(post("/api/bank-user")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("User with this email already exists"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testCreateBankUserWithInvalidJson_ShouldFail() throws Exception {
        mockMvc.perform(post("/api/bank-user")
                        .header("Authorization", adminToken)
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
    void testGetNonExistentBankUser_ShouldFail() throws Exception {
        mockMvc.perform(get("/api/bank-user/{id}", 999L)
                        .header("Authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Bank user not found"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testUpdateNonExistentBankUser_ShouldFail() throws Exception {
        PasswordedBankUserDto userDto = new PasswordedBankUserDto();
        userDto.setId(999L);
        userDto.setName("Non");
        userDto.setSurname("Existent");
        userDto.setEmail("nonexistent@example.com");
        userDto.setPassword("password123");
        userDto.setBankRole(BankRole.CLIENT);

        mockMvc.perform(put("/api/bank-user/{id}", 999L)
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Bank user not found"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testDeleteNonExistentBankUser_ShouldFail() throws Exception {
        mockMvc.perform(delete("/api/bank-user/{id}", 999L)
                        .header("Authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Bank user not found"))
                .andExpect(jsonPath("$.path").exists());
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
