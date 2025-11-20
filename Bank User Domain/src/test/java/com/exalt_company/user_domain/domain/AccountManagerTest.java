package com.exalt_company.user_domain.domain;

import com.exalt_company.user_domain.api.AccountManagement;
import com.exalt_company.user_domain.api.resource.management.AdminResponse;
import com.exalt_company.user_domain.api.resource.management.AdminResponseState;
import com.exalt_company.user_domain.domain.account.BankUserAccount;
import com.exalt_company.user_domain.shared.BankRole;
import com.exalt_company.user_domain.shared.Page;
import com.exalt_company.user_domain.spi.BankUsers;
import com.exalt_company.user_domain.spi.stub.InMemoryBankUserAccounts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AccountManagerTest {
    private BankUsers bankUsers;
    private AccountManagement accountManagement;

    @BeforeEach
    void setup() {
        bankUsers = new InMemoryBankUserAccounts();
        accountManagement = new AccountManager(bankUsers);
    }

    // ==================== createCustomAccount Tests ====================

    @Test
    void testCreateCustomAccount_Success_WithPassword() {
        // Given
        BankUserAccount user = new BankUserAccount(
                "Alice",
                "Johnson",
                "alice.johnson@example.com",
                "securePassword123"
        );

        // When
        AdminResponse<BankUserAccount> response = accountManagement.createCustomAccount(user);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.state()).isEqualTo(AdminResponseState.CREATED);
        assertThat(response.content()).isNotNull();
        assertThat(response.content().getId()).isNotNull();
        assertThat(response.content().getName()).isEqualTo("Alice");
        assertThat(response.content().getSurname()).isEqualTo("Johnson");
        assertThat(response.content().getEmail()).isEqualTo("alice.johnson@example.com");
        assertThat(response.content().getPassword()).isEqualTo("securePassword123");
        assertThat(response.errorMessage()).isNull();
    }

    @Test
    void testCreateCustomAccount_Success_WithoutPassword() {
        // Given
        BankUserAccount user = new BankUserAccount(
                "Bob",
                "Smith",
                "bob.smith@example.com",
                null
        );

        // When
        AdminResponse<BankUserAccount> response = accountManagement.createCustomAccount(user);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.state()).isEqualTo(AdminResponseState.CREATED);
        assertThat(response.content()).isNotNull();
        assertThat(response.content().getId()).isNotNull();
        assertThat(response.content().getPassword()).isEqualTo("random-admin-password");
        assertThat(response.errorMessage()).isNull();
    }

    @Test
    void testCreateCustomAccount_Failure_WhenEmailAlreadyExists() {
        // Given
        BankUserAccount firstUser = new BankUserAccount(
                "Charlie",
                "Brown",
                "duplicate@example.com",
                "password1"
        );
        BankUserAccount duplicateUser = new BankUserAccount(
                "David",
                "White",
                "duplicate@example.com",
                "password2"
        );

        // When
        accountManagement.createCustomAccount(firstUser);
        AdminResponse<BankUserAccount> response = accountManagement.createCustomAccount(duplicateUser);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.state()).isEqualTo(AdminResponseState.FAILED);
        assertThat(response.content()).isNull();
        assertThat(response.errorMessage()).isNotNull();
        assertThat(response.errorMessage()).contains("Could not create user");
        assertThat(response.errorMessage()).contains("already exists");
    }

    @Test
    void testCreateCustomAccount_Success_WithCustomRole() {
        // Given
        BankUserAccount admin = new BankUserAccount(
                "Admin",
                "User",
                "admin@example.com",
                "adminPass"
        );
        admin.setRole(BankRole.ADMIN);

        // When
        AdminResponse<BankUserAccount> response = accountManagement.createCustomAccount(admin);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.state()).isEqualTo(AdminResponseState.CREATED);
        assertThat(response.content()).isNotNull();
        assertThat(response.content().getRole()).isEqualTo(BankRole.ADMIN);
    }

    // ==================== deleteAccount Tests ====================

    @Test
    void testDeleteAccount_Success() {
        // Given
        BankUserAccount user = new BankUserAccount(
                "Emma",
                "Davis",
                "emma.davis@example.com",
                "password123"
        );
        AdminResponse<BankUserAccount> createResponse = accountManagement.createCustomAccount(user);
        UUID userId = createResponse.content().getId();

        // When
        AdminResponse<Boolean> response = accountManagement.deleteAccount(userId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.state()).isEqualTo(AdminResponseState.DELETED);
        assertThat(response.content()).isTrue();
        assertThat(response.errorMessage()).isNull();
    }

    @Test
    void testDeleteAccount_Failure_WhenUserDoesNotExist() {
        // Given
        UUID nonExistentUserId = UUID.randomUUID();

        // When
        AdminResponse<Boolean> response = accountManagement.deleteAccount(nonExistentUserId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.state()).isEqualTo(AdminResponseState.FAILED);
        assertThat(response.content()).isFalse();
        assertThat(response.errorMessage()).isNotNull();
        assertThat(response.errorMessage()).contains("Could not delete user");
    }

    // ==================== editAccount Tests ====================

    @Test
    void testEditAccount_Success() {
        // Given
        BankUserAccount user = new BankUserAccount(
                "Frank",
                "Miller",
                "frank.miller@example.com",
                "password123"
        );
        AdminResponse<BankUserAccount> createResponse = accountManagement.createCustomAccount(user);
        UUID userId = createResponse.content().getId();

        BankUserAccount updatedUser = new BankUserAccount(
                "Franklin",
                "Miller Jr.",
                "frank.miller.jr@example.com",
                "newPassword456"
        );
        updatedUser.setRole(BankRole.COUNSELOR);

        // When
        AdminResponse<BankUserAccount> response = accountManagement.editAccount(userId, updatedUser);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.state()).isEqualTo(AdminResponseState.EDITED);
        assertThat(response.content()).isNotNull();
        assertThat(response.content().getName()).isEqualTo("Franklin");
        assertThat(response.content().getSurname()).isEqualTo("Miller Jr.");
        assertThat(response.content().getEmail()).isEqualTo("frank.miller.jr@example.com");
        assertThat(response.content().getRole()).isEqualTo(BankRole.COUNSELOR);
        assertThat(response.errorMessage()).isNull();
    }

    @Test
    void testEditAccount_Failure_WhenUserIdIsNull() {
        // Given
        BankUserAccount user = new BankUserAccount(
                "Grace",
                "Wilson",
                "grace.wilson@example.com",
                "password123"
        );

        // When
        AdminResponse<BankUserAccount> response = accountManagement.editAccount(null, user);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.state()).isEqualTo(AdminResponseState.FAILED);
        assertThat(response.content()).isNull();
        assertThat(response.errorMessage()).isNotNull();
        assertThat(response.errorMessage()).contains("No user to edit");
    }

    @Test
    void testEditAccount_Failure_WhenUserDoesNotExist() {
        // Given
        UUID nonExistentUserId = UUID.randomUUID();
        BankUserAccount user = new BankUserAccount(
                "Henry",
                "Moore",
                "henry.moore@example.com",
                "password123"
        );

        // When
        AdminResponse<BankUserAccount> response = accountManagement.editAccount(nonExistentUserId, user);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.state()).isEqualTo(AdminResponseState.FAILED);
        assertThat(response.content()).isNull();
        assertThat(response.errorMessage()).isNotNull();
        assertThat(response.errorMessage()).contains("Could not edit user");
    }

    // ==================== listAccounts Tests ====================

    @Test
    void testListAccounts_Success_WithMultipleUsers() {
        // Given - Create multiple users
        accountManagement.createCustomAccount(new BankUserAccount(
                "User1", "Last1", "user1@example.com", "pass1"
        ));
        accountManagement.createCustomAccount(new BankUserAccount(
                "User2", "Last2", "user2@example.com", "pass2"
        ));
        accountManagement.createCustomAccount(new BankUserAccount(
                "User3", "Last3", "user3@example.com", "pass3"
        ));

        // When
        AdminResponse<Page<BankUserAccount>> response = accountManagement.listAccounts(0, 10);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.state()).isEqualTo(AdminResponseState.PAGED);
        assertThat(response.content()).isNotNull();
        assertThat(response.content().content()).hasSize(3);
        assertThat(response.content().page()).isZero();
        assertThat(response.content().size()).isEqualTo(10);
        assertThat(response.errorMessage()).isNull();
    }

    @Test
    void testListAccounts_Success_WithPagination() {
        // Given - Create 5 users
        for (int i = 1; i <= 5; i++) {
            accountManagement.createCustomAccount(new BankUserAccount(
                    "User" + i,
                    "Last" + i,
                    "user" + i + "@example.com",
                    "pass" + i
            ));
        }

        // When - Request first page with 2 items per page
        AdminResponse<Page<BankUserAccount>> responsePage0 = accountManagement.listAccounts(0, 2);
        AdminResponse<Page<BankUserAccount>> responsePage1 = accountManagement.listAccounts(1, 2);

        // Then
        assertThat(responsePage0.content().content()).hasSize(2);
        assertThat(responsePage0.content().page()).isZero();

        assertThat(responsePage1.content().content()).hasSize(2);
        assertThat(responsePage1.content().page()).isEqualTo(1);
    }

    @Test
    void testListAccounts_Success_EmptyList() {
        // When
        AdminResponse<Page<BankUserAccount>> response = accountManagement.listAccounts(0, 10);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.state()).isEqualTo(AdminResponseState.PAGED);
        assertThat(response.content()).isNotNull();
        assertThat(response.content().content()).isEmpty();
        assertThat(response.errorMessage()).isNull();
    }

    @Test
    void testListAccounts_Failure_WhenInvalidParameters() {
        // When - negative page number
        AdminResponse<Page<BankUserAccount>> response = accountManagement.listAccounts(-1, 10);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.state()).isEqualTo(AdminResponseState.FAILED);
        assertThat(response.content()).isNull();
        assertThat(response.errorMessage()).isNotNull();
        assertThat(response.errorMessage()).contains("Could not fetch users");
    }

    // ==================== getAccount Tests ====================

    @Test
    void testGetAccount_Success() {
        // Given
        BankUserAccount user = new BankUserAccount(
                "Isabella",
                "Taylor",
                "isabella.taylor@example.com",
                "password123"
        );
        AdminResponse<BankUserAccount> createResponse = accountManagement.createCustomAccount(user);
        UUID userId = createResponse.content().getId();

        // When
        AdminResponse<BankUserAccount> response = accountManagement.getAccount(userId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.state()).isEqualTo(AdminResponseState.FOUND);
        assertThat(response.content()).isNotNull();
        assertThat(response.content().getId()).isEqualTo(userId);
        assertThat(response.content().getName()).isEqualTo("Isabella");
        assertThat(response.content().getSurname()).isEqualTo("Taylor");
        assertThat(response.content().getEmail()).isEqualTo("isabella.taylor@example.com");
        assertThat(response.errorMessage()).isNull();
    }

    @Test
    void testGetAccount_Failure_WhenUserNotFound() {
        // Given
        UUID nonExistentUserId = UUID.randomUUID();

        // When
        AdminResponse<BankUserAccount> response = accountManagement.getAccount(nonExistentUserId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.state()).isEqualTo(AdminResponseState.FAILED);
        assertThat(response.content()).isNull();
        assertThat(response.errorMessage()).isNotNull();
        assertThat(response.errorMessage()).contains("Could not find the account");
    }

    // ==================== Integration Tests ====================

    @Test
    void testCompleteAccountLifecycle() {
        // Create
        BankUserAccount user = new BankUserAccount(
                "Jack",
                "Anderson",
                "jack.anderson@example.com",
                "password123"
        );
        AdminResponse<BankUserAccount> createResponse = accountManagement.createCustomAccount(user);
        assertThat(createResponse.state()).isEqualTo(AdminResponseState.CREATED);
        UUID userId = createResponse.content().getId();

        // Get
        AdminResponse<BankUserAccount> getResponse = accountManagement.getAccount(userId);
        assertThat(getResponse.state()).isEqualTo(AdminResponseState.FOUND);
        assertThat(getResponse.content().getEmail()).isEqualTo("jack.anderson@example.com");

        // Edit
        BankUserAccount updatedUser = new BankUserAccount(
                "Jackson",
                "Anderson",
                "jackson.anderson@example.com",
                "newPassword"
        );
        AdminResponse<BankUserAccount> editResponse = accountManagement.editAccount(userId, updatedUser);
        assertThat(editResponse.state()).isEqualTo(AdminResponseState.EDITED);
        assertThat(editResponse.content().getName()).isEqualTo("Jackson");

        // Delete
        AdminResponse<Boolean> deleteResponse = accountManagement.deleteAccount(userId);
        assertThat(deleteResponse.state()).isEqualTo(AdminResponseState.DELETED);
        assertThat(deleteResponse.content()).isTrue();

        // Verify deletion
        AdminResponse<BankUserAccount> finalGetResponse = accountManagement.getAccount(userId);
        assertThat(finalGetResponse.state()).isEqualTo(AdminResponseState.FAILED);
    }

    @Test
    void testMultipleAccountsCanBeCreatedAndListed() {
        // Given & When - Create multiple accounts with different roles
        BankUserAccount client = new BankUserAccount("Client", "One", "client@example.com", "pass1");
        BankUserAccount counselor = new BankUserAccount("Counselor", "Two", "counselor@example.com", "pass2");
        counselor.setRole(BankRole.COUNSELOR);
        BankUserAccount admin = new BankUserAccount("Admin", "Three", "admin@example.com", "pass3");
        admin.setRole(BankRole.ADMIN);

        accountManagement.createCustomAccount(client);
        accountManagement.createCustomAccount(counselor);
        accountManagement.createCustomAccount(admin);

        // When
        AdminResponse<Page<BankUserAccount>> listResponse = accountManagement.listAccounts(0, 10);

        // Then
        assertThat(listResponse.state()).isEqualTo(AdminResponseState.PAGED);
        assertThat(listResponse.content().content()).hasSize(3);
        
        // Verify different roles exist
        assertThat(listResponse.content().content())
                .extracting(BankUserAccount::getRole)
                .containsExactlyInAnyOrder(BankRole.CLIENT, BankRole.COUNSELOR, BankRole.ADMIN);
    }
}