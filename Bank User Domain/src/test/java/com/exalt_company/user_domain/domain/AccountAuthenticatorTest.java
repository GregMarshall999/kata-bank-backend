package com.exalt_company.user_domain.domain;

import com.exalt_company.user_domain.api.AccountAuthentication;
import com.exalt_company.user_domain.api.resource.AuthenticationResponse;
import com.exalt_company.user_domain.api.resource.SignInUser;
import com.exalt_company.user_domain.api.resource.SignUpUser;
import com.exalt_company.user_domain.shared.exception.AuthenticationException;
import com.exalt_company.user_domain.spi.Authentication;
import com.exalt_company.user_domain.spi.BankUsers;
import com.exalt_company.user_domain.spi.stub.InMemoryAuthentication;
import com.exalt_company.user_domain.spi.stub.InMemoryBankUserAccounts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountAuthenticatorTest {
    private Authentication<String> authentication;
    private BankUsers bankUsers;

    private AccountAuthentication<String> accountAuthentication;

    @BeforeEach
    void setup() {
        authentication = new InMemoryAuthentication();
        bankUsers = new InMemoryBankUserAccounts();

        accountAuthentication = new AccountAuthenticator(authentication, bankUsers);
    }

    @Test
    void testSignUpRequest_Success() throws AuthenticationException {
        // Given
        SignUpUser newUser = new SignUpUser(
                "John",
                "Doe",
                "john.doe@example.com",
                "securePassword123"
        );

        // When
        AuthenticationResponse<String> response = accountAuthentication.signUpRequest(newUser);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.authenticationToken()).isNotNull();
        assertThat(response.authenticationToken()).isEqualTo("stub-token-string");
    }

    @Test
    void testSignUpRequest_ThrowsException_WhenEmailAlreadyExists() throws AuthenticationException {
        // Given
        SignUpUser firstUser = new SignUpUser(
                "John",
                "Doe",
                "john.doe@example.com",
                "password123"
        );
        SignUpUser duplicateUser = new SignUpUser(
                "Jane",
                "Smith",
                "john.doe@example.com",
                "anotherPassword456"
        );

        // When - create first user
        accountAuthentication.signUpRequest(firstUser);

        // Then - attempt to create duplicate user should throw exception
        assertThatThrownBy(() -> accountAuthentication.signUpRequest(duplicateUser))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void testSignInRequest_Success() throws AuthenticationException {
        // Given - First create a user
        SignUpUser newUser = new SignUpUser(
                "Jane",
                "Smith",
                "jane.smith@example.com",
                "myPassword789"
        );
        accountAuthentication.signUpRequest(newUser);

        // And - prepare sign in credentials
        SignInUser signInUser = new SignInUser(
                "jane.smith@example.com",
                "myPassword789"
        );

        // When
        AuthenticationResponse<String> response = accountAuthentication.signInRequest(signInUser);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.authenticationToken()).isNotNull();
        assertThat(response.authenticationToken()).isEqualTo("stub-token-string");
    }

    @Test
    void testSignInRequest_ThrowsException_WhenUserNotFound() {
        // Given
        SignInUser nonExistentUser = new SignInUser(
                "nonexistent@example.com",
                "anyPassword"
        );

        // When & Then
        assertThatThrownBy(() -> accountAuthentication.signInRequest(nonExistentUser))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void testSignInRequest_ThrowsException_WhenPasswordIsIncorrect() throws AuthenticationException {
        // Given - First create a user
        SignUpUser newUser = new SignUpUser(
                "Bob",
                "Johnson",
                "bob.johnson@example.com",
                "correctPassword123"
        );
        accountAuthentication.signUpRequest(newUser);

        // And - prepare sign in with wrong password
        SignInUser signInUser = new SignInUser(
                "bob.johnson@example.com",
                "wrongPassword456"
        );

        // When & Then
        assertThatThrownBy(() -> accountAuthentication.signInRequest(signInUser))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Wrong Credentials");
    }

    @Test
    void testMultipleUsersCanSignUp() throws AuthenticationException {
        // Given
        SignUpUser user1 = new SignUpUser("Alice", "Brown", "alice@example.com", "pass1");
        SignUpUser user2 = new SignUpUser("Charlie", "Green", "charlie@example.com", "pass2");
        SignUpUser user3 = new SignUpUser("David", "White", "david@example.com", "pass3");

        // When
        AuthenticationResponse<String> response1 = accountAuthentication.signUpRequest(user1);
        AuthenticationResponse<String> response2 = accountAuthentication.signUpRequest(user2);
        AuthenticationResponse<String> response3 = accountAuthentication.signUpRequest(user3);

        // Then
        assertThat(response1.authenticationToken()).isNotNull();
        assertThat(response2.authenticationToken()).isNotNull();
        assertThat(response3.authenticationToken()).isNotNull();
    }

    @Test
    void testMultipleUsersCanSignIn() throws AuthenticationException {
        // Given - Create multiple users
        accountAuthentication.signUpRequest(new SignUpUser("User1", "Last1", "user1@example.com", "pass1"));
        accountAuthentication.signUpRequest(new SignUpUser("User2", "Last2", "user2@example.com", "pass2"));
        accountAuthentication.signUpRequest(new SignUpUser("User3", "Last3", "user3@example.com", "pass3"));

        // When
        AuthenticationResponse<String> response1 = accountAuthentication.signInRequest(
                new SignInUser("user1@example.com", "pass1")
        );
        AuthenticationResponse<String> response2 = accountAuthentication.signInRequest(
                new SignInUser("user2@example.com", "pass2")
        );
        AuthenticationResponse<String> response3 = accountAuthentication.signInRequest(
                new SignInUser("user3@example.com", "pass3")
        );

        // Then
        assertThat(response1.authenticationToken()).isNotNull();
        assertThat(response2.authenticationToken()).isNotNull();
        assertThat(response3.authenticationToken()).isNotNull();
    }
}
