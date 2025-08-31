package com.exalt_company.kata_bank_api.entity.user_fields;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CredentialsTest {
    private Credentials credentials;

    @BeforeEach
    void setUp() {
        credentials = new Credentials();
    }

    @Test
    void testCredentials() {
        String email = "test@example.com";
        String password = "password123";
        
        credentials.setEmail(email);
        credentials.setPassword(password);
        
        assertEquals(email, credentials.getEmail());
        assertEquals(password, credentials.getPassword());
    }

    @Test
    void testEmptyValues() {
        credentials.setEmail("");
        credentials.setPassword("");
        
        assertEquals("", credentials.getEmail());
        assertEquals("", credentials.getPassword());
    }

    @Test
    void testNullValues() {
        assertNull(credentials.getEmail());
        assertNull(credentials.getPassword());
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
            credentials.setEmail(email);
            assertEquals(email, credentials.getEmail());
        }
    }

    @Test
    void testLongEmail() {
        String longEmail = "a".repeat(100) + "@example.com";
        credentials.setEmail(longEmail);
        assertEquals(longEmail, credentials.getEmail());
    }

    @Test
    void testLongPassword() {
        String longPassword = "a".repeat(1000);
        credentials.setPassword(longPassword);
        assertEquals(longPassword, credentials.getPassword());
    }

    @Test
    void testUnicodeCharacters() {
        String emailWithUnicode = "tëst@exämple.com";
        String passwordWithUnicode = "pässwörd";
        
        credentials.setEmail(emailWithUnicode);
        credentials.setPassword(passwordWithUnicode);
        
        assertEquals(emailWithUnicode, credentials.getEmail());
        assertEquals(passwordWithUnicode, credentials.getPassword());
    }
} 