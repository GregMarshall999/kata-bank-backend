package com.exalt_company.kata_bank_api.dto.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AuthenticationRequestTest {
    private AuthenticationRequest request;

    @BeforeEach
    void setUp() {
        request = new AuthenticationRequest();
    }

    @Test
    void testAuthenticationRequestCreation() {
        String email = "test@example.com";
        String password = "password123";
        
        request.setEmail(email);
        request.setPassword(password);
        
        assertEquals(email, request.getEmail());
        assertEquals(password, request.getPassword());
    }

    @Test
    void testEmptyValues() {
        request.setEmail("");
        request.setPassword("");
        
        assertEquals("", request.getEmail());
        assertEquals("", request.getPassword());
    }

    @Test
    void testNullValues() {
        request.setEmail(null);
        request.setPassword(null);
        
        assertNull(request.getEmail());
        assertNull(request.getPassword());
    }

    @Test
    void testValidEmailFormats() {
        String[] validEmails = {
            "user@example.com",
            "user.name@example.com",
            "user+tag@example.com",
            "user@subdomain.example.com",
            "123@example.com",
            "user@example.co.uk"
        };
        
        for (String email : validEmails) {
            request.setEmail(email);
            
            assertEquals(email, request.getEmail());
        }
    }

    @Test
    void testPasswordWithSpecialCharacters() {
        String passwordWithSpecialChars = "P@ssw0rd!@#$%^&*()";
        
        request.setPassword(passwordWithSpecialChars);
        
        assertEquals(passwordWithSpecialChars, request.getPassword());
    }

    @Test
    void testLongEmail() {
        String longEmail = "a".repeat(100) + "@example.com";
        
        request.setEmail(longEmail);
        
        assertEquals(longEmail, request.getEmail());
    }

    @Test
    void testLongPassword() {
        String longPassword = "a".repeat(1000);
        
        request.setPassword(longPassword);
        
        assertEquals(longPassword, request.getPassword());
    }

    @Test
    void testUnicodeCharacters() {
        String emailWithUnicode = "tëst@exämple.com";
        String passwordWithUnicode = "pässwörd";
        
        request.setEmail(emailWithUnicode);
        request.setPassword(passwordWithUnicode);
        
        assertEquals(emailWithUnicode, request.getEmail());
        assertEquals(passwordWithUnicode, request.getPassword());
    }
} 