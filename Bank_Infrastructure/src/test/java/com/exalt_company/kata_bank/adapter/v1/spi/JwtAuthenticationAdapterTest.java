package com.exalt_company.kata_bank.adapter.v1.spi;

import com.exalt_company.kata_bank.entity.BankUser;
import com.exalt_company.kata_bank.repository.BankUserRepository;
import com.exalt_company.kata_bank.service.JwtService;
import com.exalt_company.user_domain.domain.account.BankUserAccount;
import com.exalt_company.user_domain.shared.BankRole;
import com.exalt_company.user_domain.shared.exception.AuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationAdapterTest {

    @Mock
    private BankUserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private JwtAuthenticationAdapter jwtAuthenticationAdapter;

    private BankUser bankUser;
    private BankUserAccount userAccount;
    private String email;
    private String password;
    private String encodedPassword;
    private String token;

    @BeforeEach
    void setUp() {
        email = "test@example.com";
        password = "password123";
        encodedPassword = "$2a$10$encodedpasswordhash";
        token = "jwt-access-token-123";

        UUID userId = UUID.randomUUID();
        bankUser = new BankUser();
        bankUser.setId(userId);
        bankUser.setName("John");
        bankUser.setSurname("Doe");
        bankUser.setEmail(email);
        bankUser.setPassword(encodedPassword);
        bankUser.setRole(BankRole.CLIENT);

        userAccount = new BankUserAccount(userId, "John", "Doe", email, password, BankRole.CLIENT);
    }

    @Test
    void should_generate_login_token_with_valid_credentials() throws AuthenticationException {
        //Given
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(bankUser));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(jwtService.generateAccessToken(bankUser)).thenReturn(token);

        //When
        String generatedToken = jwtAuthenticationAdapter.generateLoginUserToken(userAccount);

        //Then
        assertThat(generatedToken).isEqualTo(token);
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, encodedPassword);
        verify(jwtService).generateAccessToken(bankUser);
    }

    @Test
    void should_throw_exception_when_user_not_found_for_login() {
        //Given
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        //When/Then
        assertThatThrownBy(() -> jwtAuthenticationAdapter.generateLoginUserToken(userAccount))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Invalid credentials");

        verify(userRepository).findByEmail(email);
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtService, never()).generateAccessToken(any());
    }

    @Test
    void should_throw_exception_when_password_does_not_match() {
        //Given
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(bankUser));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        //When/Then
        assertThatThrownBy(() -> jwtAuthenticationAdapter.generateLoginUserToken(userAccount))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Invalid credentials");

        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, encodedPassword);
        verify(jwtService, never()).generateAccessToken(any());
    }

    @Test
    void should_generate_new_user_token() throws AuthenticationException {
        //Given
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(bankUser));
        when(jwtService.generateAccessToken(bankUser)).thenReturn(token);

        //When
        String generatedToken = jwtAuthenticationAdapter.generateNewUserToken(userAccount);

        //Then
        assertThat(generatedToken).isEqualTo(token);
        verify(userRepository).findByEmail(email);
        verify(jwtService).generateAccessToken(bankUser);
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void should_throw_exception_when_user_not_found_for_new_user_token() {
        //Given
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        //When/Then
        assertThatThrownBy(() -> jwtAuthenticationAdapter.generateNewUserToken(userAccount))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findByEmail(email);
        verify(jwtService, never()).generateAccessToken(any());
    }

    @Test
    void should_not_verify_password_for_new_user_token() throws AuthenticationException {
        //Given
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(bankUser));
        when(jwtService.generateAccessToken(bankUser)).thenReturn(token);

        //When
        jwtAuthenticationAdapter.generateNewUserToken(userAccount);

        //Then
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void should_handle_empty_email() {
        //Given
        BankUserAccount accountWithEmptyEmail = new BankUserAccount(UUID.randomUUID(), "Test", "User", "", "password", BankRole.CLIENT);
        when(userRepository.findByEmail("")).thenReturn(Optional.empty());

        //When/Then
        assertThatThrownBy(() -> jwtAuthenticationAdapter.generateLoginUserToken(accountWithEmptyEmail))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Invalid credentials");

        assertThatThrownBy(() -> jwtAuthenticationAdapter.generateNewUserToken(accountWithEmptyEmail))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("User not found");
    }
}

