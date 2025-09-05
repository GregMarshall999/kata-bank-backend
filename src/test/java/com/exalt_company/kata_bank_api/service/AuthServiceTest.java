package com.exalt_company.kata_bank_api.service;

import com.exalt_company.kata_bank_api.dto.auth.AuthenticationRequest;
import com.exalt_company.kata_bank_api.dto.auth.AuthenticationResponse;
import com.exalt_company.kata_bank_api.dto.auth.RegisterRequest;
import com.exalt_company.kata_bank_api.entity.BankUser;
import com.exalt_company.kata_bank_api.entity.user_fields.Credentials;
import com.exalt_company.kata_bank_api.enums.BankRole;
import com.exalt_company.kata_bank_api.exception.AuthException;
import com.exalt_company.kata_bank_api.repository.BankUserRepository;
import com.exalt_company.kata_bank_api.security.JwtService;
import com.exalt_company.kata_bank_api.service.impl.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private BankUserRepository repository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private AuthenticationRequest authenticationRequest;
    private BankUser bankUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("test@example.com");
        authenticationRequest.setPassword("password123");

        Credentials credentials = new Credentials();
        credentials.setEmail("test@example.com");
        credentials.setPassword("encodedPassword");

        bankUser = new BankUser();
        bankUser.setId(1L);
        bankUser.setCredentials(credentials);
        bankUser.setBankRole(BankRole.CLIENT);
    }

    @Test
    void testRegisterSuccess() throws AuthException {
        when(encoder.encode(anyString())).thenReturn("encodedPassword");
        when(repository.findAll()).thenReturn(Collections.emptyList());
        when(repository.save(any(BankUser.class))).thenReturn(bankUser);
        when(jwtService.generateToken(any(BankUser.class), anyLong(), any(BankRole.class)))
                .thenReturn("jwtToken");

        ResponseEntity<AuthenticationResponse> response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwtToken", response.getBody().getToken());
        
        verify(encoder).encode("password123");
        verify(repository).save(any(BankUser.class));
        verify(jwtService).generateToken(any(BankUser.class), eq(1L), eq(BankRole.ADMIN));
    }

    @Test
    void testRegisterFirstUserAsAdmin() throws AuthException {
        when(encoder.encode(anyString())).thenReturn("encodedPassword");
        when(repository.findAll()).thenReturn(Collections.emptyList());
        when(repository.save(any(BankUser.class))).thenReturn(bankUser);
        when(jwtService.generateToken(any(BankUser.class), anyLong(), any(BankRole.class)))
                .thenReturn("jwtToken");

        ResponseEntity<AuthenticationResponse> response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        
        verify(repository).save(argThat(user -> user.getBankRole() == BankRole.ADMIN));
    }

    @Test
    void testRegisterSubsequentUserAsClient() throws AuthException {
        BankUser existingUser = new BankUser();
        when(encoder.encode(anyString())).thenReturn("encodedPassword");
        when(repository.findAll()).thenReturn(Collections.singletonList(existingUser));
        when(repository.save(any(BankUser.class))).thenReturn(bankUser);
        when(jwtService.generateToken(any(BankUser.class), anyLong(), any(BankRole.class)))
                .thenReturn("jwtToken");

        ResponseEntity<AuthenticationResponse> response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        
        verify(repository).save(argThat(user -> user.getBankRole() == BankRole.CLIENT));
    }

    @Test
    void testAuthenticateSuccess() throws AuthException {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(repository.findByCredentialsEmail("test@example.com"))
                .thenReturn(Optional.of(bankUser));
        when(jwtService.generateToken(any(BankUser.class), anyLong(), any(BankRole.class)))
                .thenReturn("jwtToken");

        ResponseEntity<AuthenticationResponse> response = authService.authenticate(authenticationRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwtToken", response.getBody().getToken());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(repository).findByCredentialsEmail("test@example.com");
        verify(jwtService).generateToken(any(BankUser.class), eq(1L), eq(BankRole.CLIENT));
    }

    @Test
    void testAuthenticateUserNotFound() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(repository.findByCredentialsEmail("test@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authService.authenticate(authenticationRequest));
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(repository).findByCredentialsEmail("test@example.com");
    }

    @Test
    void testAuthenticateAuthenticationFailure() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Authentication failed"));

        assertThrows(AuthException.class, () -> {
            try {
                authService.authenticate(authenticationRequest);
            } catch (AuthException e) {
                assertEquals("Bad credentials", e.getMessage());
                throw e;
            }
        });
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(repository, never()).findByCredentialsEmail(anyString());
    }

    @Test
    void testRegisterWithExistingEmail() {
        when(repository.findByCredentialsEmail("test@example.com"))
                .thenReturn(Optional.of(bankUser));

        assertThrows(AuthException.class, () -> {
            try {
                authService.register(registerRequest);
            } catch (AuthException e) {
                assertEquals("User with this email already exists", e.getMessage());
                throw e;
            }
        });
        
        verify(repository).findByCredentialsEmail("test@example.com");
        verify(repository, never()).save(any(BankUser.class));
    }

    @Test
    void testRegisterWithEmptyEmail() throws AuthException {
        registerRequest.setEmail("");
        
        when(repository.findByCredentialsEmail("")).thenReturn(Optional.empty());
        when(encoder.encode(anyString())).thenReturn("encodedPassword");
        when(repository.findAll()).thenReturn(Collections.emptyList());
        when(repository.save(any(BankUser.class))).thenReturn(bankUser);
        when(jwtService.generateToken(any(BankUser.class), anyLong(), any(BankRole.class)))
                .thenReturn("jwtToken");

        ResponseEntity<AuthenticationResponse> response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwtToken", response.getBody().getToken());
    }

    @Test
    void testRegisterWithNullEmail() throws AuthException {
        registerRequest.setEmail(null);
        
        when(repository.findByCredentialsEmail(null)).thenReturn(Optional.empty());
        when(encoder.encode(anyString())).thenReturn("encodedPassword");
        when(repository.findAll()).thenReturn(Collections.emptyList());
        when(repository.save(any(BankUser.class))).thenReturn(bankUser);
        when(jwtService.generateToken(any(BankUser.class), anyLong(), any(BankRole.class)))
                .thenReturn("jwtToken");

        ResponseEntity<AuthenticationResponse> response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwtToken", response.getBody().getToken());
    }

    @Test
    void testRegisterWithEmptyPassword() throws AuthException {
        registerRequest.setPassword("");
        
        when(repository.findByCredentialsEmail("test@example.com")).thenReturn(Optional.empty());
        when(encoder.encode("")).thenReturn("encodedPassword");
        when(repository.findAll()).thenReturn(Collections.emptyList());
        when(repository.save(any(BankUser.class))).thenReturn(bankUser);
        when(jwtService.generateToken(any(BankUser.class), anyLong(), any(BankRole.class)))
                .thenReturn("jwtToken");

        ResponseEntity<AuthenticationResponse> response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwtToken", response.getBody().getToken());
    }

    @Test
    void testAuthenticateWithEmptyEmail() {
        authenticationRequest.setEmail("");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(repository.findByCredentialsEmail(""))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authService.authenticate(authenticationRequest));
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(repository).findByCredentialsEmail("");
    }

    @Test
    void testAuthenticateWithEmptyPassword() throws AuthException {
        authenticationRequest.setPassword("");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(repository.findByCredentialsEmail("test@example.com"))
                .thenReturn(Optional.of(bankUser));
        when(jwtService.generateToken(any(BankUser.class), anyLong(), any(BankRole.class)))
                .thenReturn("jwtToken");

        ResponseEntity<AuthenticationResponse> response = authService.authenticate(authenticationRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwtToken", response.getBody().getToken());
    }

    @Test
    void testRegisterWithNullPassword() throws AuthException {
        registerRequest.setPassword(null);
        
        when(repository.findByCredentialsEmail("test@example.com")).thenReturn(Optional.empty());
        when(encoder.encode(null)).thenReturn("encodedPassword");
        when(repository.findAll()).thenReturn(Collections.emptyList());
        when(repository.save(any(BankUser.class))).thenReturn(bankUser);
        when(jwtService.generateToken(any(BankUser.class), anyLong(), any(BankRole.class)))
                .thenReturn("jwtToken");

        ResponseEntity<AuthenticationResponse> response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwtToken", response.getBody().getToken());
    }

    @Test
    void testAuthenticateWithNullPassword() throws AuthException {
        authenticationRequest.setPassword(null);
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(repository.findByCredentialsEmail("test@example.com"))
                .thenReturn(Optional.of(bankUser));
        when(jwtService.generateToken(any(BankUser.class), anyLong(), any(BankRole.class)))
                .thenReturn("jwtToken");

        ResponseEntity<AuthenticationResponse> response = authService.authenticate(authenticationRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwtToken", response.getBody().getToken());
    }
} 