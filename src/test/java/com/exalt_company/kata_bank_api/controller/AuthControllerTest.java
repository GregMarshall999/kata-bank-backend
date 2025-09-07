package com.exalt_company.kata_bank_api.controller;

import com.exalt_company.kata_bank_api.dto.auth.AuthenticationRequest;
import com.exalt_company.kata_bank_api.dto.auth.AuthenticationResponse;
import com.exalt_company.kata_bank_api.dto.auth.RegisterRequest;
import com.exalt_company.kata_bank_api.exception.AuthException;
import com.exalt_company.kata_bank_api.service.IAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private IAuthService authService;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest registerRequest;
    private AuthenticationRequest authenticationRequest;
    private AuthenticationResponse authenticationResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("john.doe@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setName("John");
        registerRequest.setSurname("Doe");

        authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("john.doe@example.com");
        authenticationRequest.setPassword("password123");

        authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...");
    }

    @Test
    void testRegister_Success() throws AuthException {
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(ResponseEntity.ok(authenticationResponse));

        ResponseEntity<AuthenticationResponse> response = authController.register(registerRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(authenticationResponse.getToken(), response.getBody().getToken());
        
        verify(authService, times(1)).register(registerRequest);
    }

    @Test
    void testRegister_WithValidData() throws AuthException {
        RegisterRequest validRequest = new RegisterRequest();
        validRequest.setEmail("jane.smith@example.com");
        validRequest.setPassword("securePassword456");
        validRequest.setName("Jane");
        validRequest.setSurname("Smith");

        AuthenticationResponse responseToken = new AuthenticationResponse();
        responseToken.setToken("new.jwt.token.here");

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(ResponseEntity.ok(responseToken));

        ResponseEntity<AuthenticationResponse> response = authController.register(validRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseToken.getToken(), response.getBody().getToken());
        
        verify(authService, times(1)).register(validRequest);
    }

    @Test
    void testRegister_ServiceThrowsException() throws AuthException {
        String errorMessage = "User already exists";
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new AuthException(errorMessage, HttpStatus.BAD_REQUEST));

        AuthException exception = assertThrows(AuthException.class,
                () -> authController.register(registerRequest));
        
        assertEquals(errorMessage, exception.getMessage());
        verify(authService, times(1)).register(registerRequest);
    }

    @ParameterizedTest
    @CsvSource({
        "null, 'john.doe@example.com', 'password123', 'John', 'Doe', 'Email cannot be null'",
        "'', 'john.doe@example.com', 'password123', 'John', 'Doe', 'Email cannot be empty'",
        "'john.doe@example.com', null, 'password123', 'John', 'Doe', 'Password cannot be null'",
        "'john.doe@example.com', '', 'password123', 'John', 'Doe', 'Password cannot be empty'",
        "'john.doe@example.com', 'password123', null, 'John', 'Doe', 'Name cannot be null'",
        "'john.doe@example.com', 'password123', 'John', null, 'Doe', 'Surname cannot be null'"
    })
    void testRegister_WithInvalidData(String email, String password, String name, String surname, String expectedErrorMessage) throws AuthException {
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setEmail("null".equals(email) ? null : email);
        invalidRequest.setPassword("null".equals(password) ? null : password);
        invalidRequest.setName("null".equals(name) ? null : name);
        invalidRequest.setSurname("null".equals(surname) ? null : surname);
        
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new AuthException(expectedErrorMessage, HttpStatus.BAD_REQUEST));

        AuthException exception = assertThrows(AuthException.class,
                () -> authController.register(invalidRequest));
        
        assertEquals(expectedErrorMessage, exception.getMessage());
        verify(authService, times(1)).register(invalidRequest);
    }

    @Test
    void testAuthenticate_Success() throws AuthException {
        when(authService.authenticate(any(AuthenticationRequest.class)))
                .thenReturn(ResponseEntity.ok(authenticationResponse));

        ResponseEntity<AuthenticationResponse> response = authController.authenticate(authenticationRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(authenticationResponse.getToken(), response.getBody().getToken());
        
        verify(authService, times(1)).authenticate(authenticationRequest);
    }

    @Test
    void testAuthenticate_WithValidCredentials() throws AuthException {
        AuthenticationRequest validRequest = new AuthenticationRequest();
        validRequest.setEmail("user@example.com");
        validRequest.setPassword("correctPassword");

        AuthenticationResponse responseToken = new AuthenticationResponse();
        responseToken.setToken("valid.jwt.token");

        when(authService.authenticate(any(AuthenticationRequest.class)))
                .thenReturn(ResponseEntity.ok(responseToken));

        ResponseEntity<AuthenticationResponse> response = authController.authenticate(validRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseToken.getToken(), response.getBody().getToken());
        
        verify(authService, times(1)).authenticate(validRequest);
    }

    @Test
    void testAuthenticate_InvalidCredentials() throws AuthException {
        String errorMessage = "Invalid credentials";
        when(authService.authenticate(any(AuthenticationRequest.class)))
                .thenThrow(new AuthException(errorMessage, HttpStatus.UNAUTHORIZED));

        AuthException exception = assertThrows(AuthException.class,
                () -> authController.authenticate(authenticationRequest));
        
        assertEquals(errorMessage, exception.getMessage());
        verify(authService, times(1)).authenticate(authenticationRequest);
    }

    @ParameterizedTest
    @CsvSource({
        "null, 'password123', 'Email cannot be null'",
        "'', 'password123', 'Email cannot be empty'",
        "'john.doe@example.com', null, 'Password cannot be null'",
        "'john.doe@example.com', '', 'Password cannot be empty'"
    })
    void testAuthenticate_WithInvalidData(String email, String password, String expectedErrorMessage) throws AuthException {
        AuthenticationRequest invalidRequest = new AuthenticationRequest();
        invalidRequest.setEmail("null".equals(email) ? null : email);
        invalidRequest.setPassword("null".equals(password) ? null : password);
        
        when(authService.authenticate(any(AuthenticationRequest.class)))
                .thenThrow(new AuthException(expectedErrorMessage, HttpStatus.BAD_REQUEST));

        AuthException exception = assertThrows(AuthException.class,
                () -> authController.authenticate(invalidRequest));
        
        assertEquals(expectedErrorMessage, exception.getMessage());
        verify(authService, times(1)).authenticate(invalidRequest);
    }

    @Test
    void testAuthenticate_UserNotFound() throws AuthException {
        String errorMessage = "User not found";
        when(authService.authenticate(any(AuthenticationRequest.class)))
                .thenThrow(new AuthException(errorMessage, HttpStatus.BAD_REQUEST));

        AuthException exception = assertThrows(AuthException.class,
                () -> authController.authenticate(authenticationRequest));
        
        assertEquals(errorMessage, exception.getMessage());
        verify(authService, times(1)).authenticate(authenticationRequest);
    }

    @Test
    void testAuthenticate_WithSpecialCharactersInEmail() throws AuthException {
        authenticationRequest.setEmail("user+test@example-domain.co.uk");
        when(authService.authenticate(any(AuthenticationRequest.class)))
                .thenReturn(ResponseEntity.ok(authenticationResponse));

        ResponseEntity<AuthenticationResponse> response = authController.authenticate(authenticationRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authenticationResponse.getToken(), response.getBody().getToken());
        
        verify(authService, times(1)).authenticate(authenticationRequest);
    }

    @Test
    void testAuthenticate_WithLongPassword() throws AuthException {
        authenticationRequest.setPassword("a".repeat(100));
        when(authService.authenticate(any(AuthenticationRequest.class)))
                .thenReturn(ResponseEntity.ok(authenticationResponse));

        ResponseEntity<AuthenticationResponse> response = authController.authenticate(authenticationRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authenticationResponse.getToken(), response.getBody().getToken());
        
        verify(authService, times(1)).authenticate(authenticationRequest);
    }
}
