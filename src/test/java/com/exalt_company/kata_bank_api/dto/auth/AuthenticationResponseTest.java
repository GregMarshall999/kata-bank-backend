package com.exalt_company.kata_bank_api.dto.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AuthenticationResponseTest {
    private AuthenticationResponse response;

    @BeforeEach
    void setUp() {
        response = new AuthenticationResponse();
    }

    @Test
    void testAuthenticationResponseCreation() {
        String token = "jwtToken123";
        
        response.setToken(token);
        
        assertEquals(token, response.getToken());
    }

    @Test
    void testEmptyToken() {
        response.setToken("");
        
        assertEquals("", response.getToken());
    }

    @Test
    void testNullToken() {
        response.setToken(null);
        
        assertNull(response.getToken());
    }

    @Test
    void testLongToken() {
        String longToken = "a".repeat(10000);
        
        response.setToken(longToken);
        
        assertEquals(longToken, response.getToken());
    }

    @Test
    void testTokenWithSpecialCharacters() {
        String tokenWithSpecialChars = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        
        response.setToken(tokenWithSpecialChars);
        
        assertEquals(tokenWithSpecialChars, response.getToken());
    }

    @Test
    void testTokenWithUnicodeCharacters() {
        String tokenWithUnicode = "tëstTökën123";
        
        response.setToken(tokenWithUnicode);
        
        assertEquals(tokenWithUnicode, response.getToken());
    }

    @Test
    void testMultipleTokenChanges() {
        String token1 = "token1";
        String token2 = "token2";
        String token3 = "token3";
        
        response.setToken(token1);
        assertEquals(token1, response.getToken());
        
        response.setToken(token2);
        assertEquals(token2, response.getToken());
        
        response.setToken(token3);
        assertEquals(token3, response.getToken());
    }
} 