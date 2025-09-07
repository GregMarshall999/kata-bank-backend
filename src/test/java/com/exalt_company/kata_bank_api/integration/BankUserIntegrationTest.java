package com.exalt_company.kata_bank_api.integration;

import com.exalt_company.kata_bank_api.dto.BankUserDto;
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
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        bankUserRepository.deleteAll();

        adminUser = createTestUser("Admin", "User", "admin@example.com", "admin123", BankRole.ADMIN);
        adminUser = bankUserRepository.save(adminUser);
        adminToken = "Bearer " + jwtService.generateToken(adminUser, adminUser.getId(), adminUser.getBankRole());

        clientUser = createTestUser("Client", "User", "client@example.com", "client123", BankRole.CLIENT);
        clientUser = bankUserRepository.save(clientUser);
        clientToken = "Bearer " + jwtService.generateToken(clientUser, clientUser.getId(), clientUser.getBankRole());
    }

    @Test
    void testCreateBankUserWithNullName() throws Exception {
        BankUserDto userDto = new BankUserDto();
        userDto.setId(0L);
        userDto.setName(null);
        userDto.setSurname("User");
        userDto.setEmail("newuser@example.com");
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
        BankUserDto userDto = new BankUserDto();
        userDto.setId(0L);
        userDto.setName("");
        userDto.setSurname("User");
        userDto.setEmail("newuser@example.com");
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
        BankUserDto userDto = new BankUserDto();
        userDto.setId(0L);
        userDto.setName("A");
        userDto.setSurname("User");
        userDto.setEmail("newuser@example.com");
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
        BankUserDto userDto = new BankUserDto();
        userDto.setId(0L);
        userDto.setName("A".repeat(51));
        userDto.setSurname("User");
        userDto.setEmail("newuser@example.com");
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
        BankUserDto userDto = new BankUserDto();
        userDto.setId(0L);
        userDto.setName("New");
        userDto.setSurname(null);
        userDto.setEmail("newuser@example.com");
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
        BankUserDto userDto = new BankUserDto();
        userDto.setId(0L);
        userDto.setName("New");
        userDto.setSurname("");
        userDto.setEmail("newuser@example.com");
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
        BankUserDto userDto = new BankUserDto();
        userDto.setId(0L);
        userDto.setName("New");
        userDto.setSurname("U");
        userDto.setEmail("newuser@example.com");
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
        BankUserDto userDto = new BankUserDto();
        userDto.setId(0L);
        userDto.setName("New");
        userDto.setSurname("B".repeat(51));
        userDto.setEmail("newuser@example.com");
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
        BankUserDto userDto = new BankUserDto();
        userDto.setId(0L);
        userDto.setName("New");
        userDto.setSurname("User");
        userDto.setEmail(null);
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
        BankUserDto userDto = new BankUserDto();
        userDto.setId(0L);
        userDto.setName("New");
        userDto.setSurname("User");
        userDto.setEmail("");
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
        BankUserDto userDto = new BankUserDto();
        userDto.setId(0L);
        userDto.setName("New");
        userDto.setSurname("User");
        userDto.setEmail("invalid-email-format");
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
        BankUserDto userDto = new BankUserDto();
        userDto.setId(0L);
        userDto.setName("New");
        userDto.setSurname("User");
        userDto.setEmail("newuser@example.com");
        userDto.setBankRole(null);

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
        BankUserDto userDto = new BankUserDto();
        userDto.setId(0L);
        userDto.setName("New");
        userDto.setSurname("User");
        userDto.setEmail("newuser@example.com");
        userDto.setBankRole(BankRole.CLIENT);
        userDto.setAdvisorId(-1L);

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
        BankUserDto userDto = new BankUserDto();
        userDto.setId(0L);
        userDto.setName("New");
        userDto.setSurname("User");
        userDto.setEmail("newuser@example.com");
        userDto.setBankRole(BankRole.CLIENT);
        userDto.setAdvisorId(0L);



        mockMvc.perform(post("/api/bank-user")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("New"))
                .andExpect(jsonPath("$.surname").value("User"))
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.bankRole").value("CLIENT"))
                .andExpect(jsonPath("$.password").exists());
    }


    @Test
    void testCreateBankUserAsAdmin() throws Exception {
        BankUserDto userDto = new BankUserDto();
        userDto.setId(0L);
        userDto.setName("New");
        userDto.setSurname("User");
        userDto.setEmail("newuser@example.com");
        userDto.setBankRole(BankRole.CLIENT);
        userDto.setAdvisorId(0L);

        mockMvc.perform(post("/api/bank-user")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("New"))
                .andExpect(jsonPath("$.surname").value("User"))
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.bankRole").value("CLIENT"))
                .andExpect(jsonPath("$.password").value("temporary123"));
    }

    @Test
    void testCreateBankUserAsClient_ShouldFail() throws Exception {
        BankUserDto userDto = new BankUserDto();
        userDto.setId(0L);
        userDto.setName("New");
        userDto.setSurname("User");
        userDto.setEmail("newuser@example.com");
        userDto.setBankRole(BankRole.CLIENT);

        mockMvc.perform(post("/api/bank-user")
                        .header("Authorization", clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateBankUserWithoutAuthorization_ShouldFail() throws Exception {
        BankUserDto userDto = new BankUserDto();
        userDto.setId(0L);
        userDto.setName("New");
        userDto.setSurname("User");
        userDto.setEmail("newuser@example.com");
        userDto.setBankRole(BankRole.CLIENT);

        mockMvc.perform(post("/api/bank-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetBankUserByIdAsAdmin() throws Exception {
        mockMvc.perform(get("/api/bank-user/{id}", clientUser.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isFound())
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
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetAllBankUsersAsAdmin() throws Exception {
        mockMvc.perform(get("/api/bank-user")
                        .header("Authorization", adminToken))
                .andExpect(status().isFound())
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
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetBankUsersPageAsAdmin() throws Exception {
        mockMvc.perform(get("/api/bank-user/{page}/{size}", 0, 10)
                        .header("Authorization", adminToken))
                .andExpect(status().isFound())
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
        mockMvc.perform(get("/api/bank-user/{page}/{size}", 0, 10)
                        .header("Authorization", clientToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateBankUserAsAdmin() throws Exception {
        BankUserDto userDto = new BankUserDto();
        userDto.setId(clientUser.getId());
        userDto.setName("Updated");
        userDto.setSurname("Client");
        userDto.setEmail("updated@example.com");
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

        BankUser updatedUser = bankUserRepository.findById(clientUser.getId()).orElse(null);
        assertNotNull(updatedUser);
        assertEquals("Updated", updatedUser.getIdentity().getName());
        assertEquals("Client", updatedUser.getIdentity().getSurname());
        assertEquals("updated@example.com", updatedUser.getCredentials().getEmail());
    }

    @Test
    void testUpdateBankUserAsClient_ShouldFail() throws Exception {
        BankUserDto userDto = new BankUserDto();
        userDto.setId(adminUser.getId());
        userDto.setName("Hacked");
        userDto.setSurname("Admin");
        userDto.setEmail("hacked@example.com");
        userDto.setBankRole(BankRole.ADMIN);

        mockMvc.perform(put("/api/bank-user/{id}", adminUser.getId())
                        .header("Authorization", clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateBankUserWithoutAuthorization_ShouldFail() throws Exception {
        BankUserDto userDto = new BankUserDto();
        userDto.setId(clientUser.getId());
        userDto.setName("Updated");
        userDto.setSurname("Client");
        userDto.setEmail("updated@example.com");
        userDto.setBankRole(BankRole.CLIENT);

        mockMvc.perform(put("/api/bank-user/{id}", clientUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteBankUserByIdAsAdmin() throws Exception {
        mockMvc.perform(delete("/api/bank-user/{id}", clientUser.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(true));

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
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteBankUserByDtoAsClient_ShouldFail() throws Exception {
        BankUserDto userDto = new BankUserDto();
        userDto.setId(adminUser.getId());
        userDto.setName("Admin");
        userDto.setSurname("User");
        userDto.setEmail("admin@example.com");
        userDto.setBankRole(BankRole.ADMIN);

        mockMvc.perform(delete("/api/bank-user")
                        .header("Authorization", clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateBankUserWithDuplicateEmail_ShouldFail() throws Exception {
        BankUserDto userDto = new BankUserDto();
        userDto.setId(0L);
        userDto.setName("Duplicate");
        userDto.setSurname("User");
        userDto.setEmail("client@example.com");
        userDto.setBankRole(BankRole.CLIENT);

        mockMvc.perform(post("/api/bank-user")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Violated a unique field."))
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
                .andExpect(jsonPath("$.message").value("BankUser not found"))
                .andExpect(jsonPath("$.path").exists());
    }

    @Test
    void testUpdateNonExistentBankUser_ShouldFail() throws Exception {
        BankUserDto userDto = new BankUserDto();
        userDto.setId(999L);
        userDto.setName("Non");
        userDto.setSurname("Existent");
        userDto.setEmail("nonexistent@example.com");
        userDto.setBankRole(BankRole.CLIENT);

        mockMvc.perform(put("/api/bank-user/{id}", 999L)
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Could not update BankUser: Please create first"))
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
                .andExpect(jsonPath("$.message").value("Could not delete BankUser: not found"))
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
