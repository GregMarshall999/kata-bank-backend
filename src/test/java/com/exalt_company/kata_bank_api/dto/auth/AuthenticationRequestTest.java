package com.exalt_company.kata_bank_api.dto.auth;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthenticationRequestTest {
    private AuthenticationRequest request;
    private Validator validator;

    @BeforeEach
    void setUp() {
        request = new AuthenticationRequest();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
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

    @Test
    void testValidAuthenticationRequest() {
        request.setEmail("test@example.com");
        request.setPassword("password123");
        
        Set<ConstraintViolation<AuthenticationRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Valid request should have no violations");
    }

    @Test
    void testNullEmailValidation() {
        request.setEmail(null);
        request.setPassword("password123");
        
        Set<ConstraintViolation<AuthenticationRequest>> violations = validator.validate(request);
        assertTrue(violations.size() >= 1, "Null email should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email is required")));
    }

    @Test
    void testBlankEmailValidation() {
        request.setEmail("");
        request.setPassword("password123");
        
        Set<ConstraintViolation<AuthenticationRequest>> violations = validator.validate(request);
        assertTrue(violations.size() >= 1, "Blank email should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email is required")));
    }

    @Test
    void testInvalidEmailFormatValidation() {
        request.setEmail("invalid-email");
        request.setPassword("password123");
        
        Set<ConstraintViolation<AuthenticationRequest>> violations = validator.validate(request);
        assertTrue(violations.size() >= 1, "Invalid email format should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email must be a valid email address")));
    }

    @Test
    void testNullPasswordValidation() {
        request.setEmail("test@example.com");
        request.setPassword(null);
        
        Set<ConstraintViolation<AuthenticationRequest>> violations = validator.validate(request);
        assertTrue(violations.size() >= 1, "Null password should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Password is required")));
    }

    @Test
    void testBlankPasswordValidation() {
        request.setEmail("test@example.com");
        request.setPassword("");
        
        Set<ConstraintViolation<AuthenticationRequest>> violations = validator.validate(request);
        assertTrue(violations.size() >= 1, "Blank password should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Password is required")));
    }

    @Test
    void testShortPasswordValidation() {
        request.setEmail("test@example.com");
        request.setPassword("12345");
        
        Set<ConstraintViolation<AuthenticationRequest>> violations = validator.validate(request);
        assertTrue(violations.size() >= 1, "Short password should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Password must be between 6 and 100 characters")));
    }

    @Test
    void testLongPasswordValidation() {
        request.setEmail("test@example.com");
        request.setPassword("a".repeat(101));
        
        Set<ConstraintViolation<AuthenticationRequest>> violations = validator.validate(request);
        assertTrue(violations.size() >= 1, "Long password should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Password must be between 6 and 100 characters")));
    }

    @Test
    void testValidEmailFormatsWithValidation() {
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
            request.setPassword("password123");
            
            Set<ConstraintViolation<AuthenticationRequest>> violations = validator.validate(request);
            assertTrue(violations.isEmpty(), "Valid email '" + email + "' should have no violations");
        }
    }

    @Test
    void testInvalidEmailFormatsWithValidation() {
        String[] invalidEmails = {
            "invalid-email",
            "@example.com",
            "user@",
            "user.example.com",
            "user@.com",
            "user@example.",
            "user@example..com"
        };
        
        for (String email : invalidEmails) {
            request.setEmail(email);
            request.setPassword("password123");
            
            Set<ConstraintViolation<AuthenticationRequest>> violations = validator.validate(request);
            assertTrue(violations.size() >= 1, "Invalid email '" + email + "' should have validation violations");
        }
    }

    @Test
    void testPasswordLengthBoundaries() {
        request.setEmail("test@example.com");
        request.setPassword("123456");
        
        Set<ConstraintViolation<AuthenticationRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "6-character password should be valid");
        
        request.setPassword("a".repeat(100));
        violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "100-character password should be valid");
    }

    @Test
    void testMultipleValidationErrors() {
        request.setEmail("invalid-email");
        request.setPassword("123");
        
        Set<ConstraintViolation<AuthenticationRequest>> violations = validator.validate(request);
        assertTrue(violations.size() >= 2, "Multiple validation errors should be detected");
    }
} 