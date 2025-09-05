package com.exalt_company.kata_bank_api.dto.auth;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegisterRequestTest {
    private RegisterRequest registerRequest;
    private Validator validator;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(registerRequest);
        assertNull(registerRequest.getEmail());
        assertNull(registerRequest.getPassword());
        assertNull(registerRequest.getName());
        assertNull(registerRequest.getSurname());
    }

    @Test
    void testSetAndGetName() {
        String name = "John";
        
        registerRequest.setName(name);
        
        assertEquals(name, registerRequest.getName());
    }

    @Test
    void testSetAndGetSurname() {
        String surname = "Doe";
        
        registerRequest.setSurname(surname);
        
        assertEquals(surname, registerRequest.getSurname());
    }

    @Test
    void testInheritedFields() {
        String email = "john.doe@example.com";
        String password = "password123";
        
        registerRequest.setEmail(email);
        registerRequest.setPassword(password);
        
        assertEquals(email, registerRequest.getEmail());
        assertEquals(password, registerRequest.getPassword());
    }

    @Test
    void testAllFieldsTogether() {
        String name = "Jane";
        String surname = "Smith";
        String email = "jane.smith@example.com";
        String password = "securePassword123";
        
        registerRequest.setName(name);
        registerRequest.setSurname(surname);
        registerRequest.setEmail(email);
        registerRequest.setPassword(password);
        
        assertEquals(name, registerRequest.getName());
        assertEquals(surname, registerRequest.getSurname());
        assertEquals(email, registerRequest.getEmail());
        assertEquals(password, registerRequest.getPassword());
    }

    @Test
    void testNullValues() {
        registerRequest.setName(null);
        registerRequest.setSurname(null);
        registerRequest.setEmail(null);
        registerRequest.setPassword(null);
        
        assertNull(registerRequest.getName());
        assertNull(registerRequest.getSurname());
        assertNull(registerRequest.getEmail());
        assertNull(registerRequest.getPassword());
    }

    @Test
    void testEmptyStringValues() {
        registerRequest.setName("");
        registerRequest.setSurname("");
        registerRequest.setEmail("");
        registerRequest.setPassword("");
        
        assertEquals("", registerRequest.getName());
        assertEquals("", registerRequest.getSurname());
        assertEquals("", registerRequest.getEmail());
        assertEquals("", registerRequest.getPassword());
    }

    @Test
    void testLongNameAndSurname() {
        String longName = "This is a very long name that contains multiple words and should be properly handled";
        String longSurname = "This is a very long surname that contains multiple words and should be properly handled";
        
        registerRequest.setName(longName);
        registerRequest.setSurname(longSurname);
        
        assertEquals(longName, registerRequest.getName());
        assertEquals(longSurname, registerRequest.getSurname());
    }

    @Test
    void testSpecialCharactersInName() {
        String nameWithSpecialChars = "José María O'Connor-Smith Jr.";
        String surnameWithSpecialChars = "García-López & Associates";
        
        registerRequest.setName(nameWithSpecialChars);
        registerRequest.setSurname(surnameWithSpecialChars);
        
        assertEquals(nameWithSpecialChars, registerRequest.getName());
        assertEquals(surnameWithSpecialChars, registerRequest.getSurname());
    }

    @Test
    void testUnicodeCharacters() {
        String unicodeName = "José María";
        String unicodeSurname = "García-López";
        String unicodeEmail = "josé.maría@exämple.com";
        String unicodePassword = "pässwörd123";
        
        registerRequest.setName(unicodeName);
        registerRequest.setSurname(unicodeSurname);
        registerRequest.setEmail(unicodeEmail);
        registerRequest.setPassword(unicodePassword);
        
        assertEquals(unicodeName, registerRequest.getName());
        assertEquals(unicodeSurname, registerRequest.getSurname());
        assertEquals(unicodeEmail, registerRequest.getEmail());
        assertEquals(unicodePassword, registerRequest.getPassword());
    }

    @Test
    void testValidEmailFormats() {
        String[] validEmails = {
            "user@example.com",
            "user.name@example.com",
            "user+tag@example.com",
            "user@subdomain.example.com",
            "123@example.com",
            "user@example.co.uk",
            "user@example-domain.com"
        };
        
        for (String email : validEmails) {
            registerRequest.setEmail(email);
            assertEquals(email, registerRequest.getEmail());
        }
    }

    @Test
    void testPasswordWithSpecialCharacters() {
        String passwordWithSpecialChars = "P@ssw0rd!@#$%^&*()_+-=[]{}|;':\",./<>?";
        
        registerRequest.setPassword(passwordWithSpecialChars);
        
        assertEquals(passwordWithSpecialChars, registerRequest.getPassword());
    }

    @Test
    void testLongPassword() {
        String longPassword = "a".repeat(1000);
        
        registerRequest.setPassword(longPassword);
        
        assertEquals(longPassword, registerRequest.getPassword());
    }

    @Test
    void testMultipleFieldUpdates() {
        registerRequest.setName("John");
        registerRequest.setSurname("Doe");
        registerRequest.setEmail("john@example.com");
        registerRequest.setPassword("password1");
        
        registerRequest.setName("Jane");
        registerRequest.setSurname("Smith");
        registerRequest.setEmail("jane@example.com");
        registerRequest.setPassword("password2");
        
        assertEquals("Jane", registerRequest.getName());
        assertEquals("Smith", registerRequest.getSurname());
        assertEquals("jane@example.com", registerRequest.getEmail());
        assertEquals("password2", registerRequest.getPassword());
    }

    @Test
    void testWhitespaceInNames() {
        String nameWithWhitespace = "  John  ";
        String surnameWithWhitespace = "  Doe  ";
        
        registerRequest.setName(nameWithWhitespace);
        registerRequest.setSurname(surnameWithWhitespace);
        
        assertEquals(nameWithWhitespace, registerRequest.getName());
        assertEquals(surnameWithWhitespace, registerRequest.getSurname());
    }

    @Test
    void testNumbersInNames() {
        String nameWithNumbers = "John123";
        String surnameWithNumbers = "Doe456";
        
        registerRequest.setName(nameWithNumbers);
        registerRequest.setSurname(surnameWithNumbers);
        
        assertEquals(nameWithNumbers, registerRequest.getName());
        assertEquals(surnameWithNumbers, registerRequest.getSurname());
    }

    @Test
    void testRealisticRegistrationScenario() {
        String name = "Alice";
        String surname = "Johnson";
        String email = "alice.johnson@email.com";
        String password = "SecurePass123!";
        
        registerRequest.setName(name);
        registerRequest.setSurname(surname);
        registerRequest.setEmail(email);
        registerRequest.setPassword(password);
        
        assertEquals(name, registerRequest.getName());
        assertEquals(surname, registerRequest.getSurname());
        assertEquals(email, registerRequest.getEmail());
        assertEquals(password, registerRequest.getPassword());
    }

    @Test
    void testNameAndSurnameIndependence() {
        String name = "John";
        String surname = "Doe";
        
        registerRequest.setName(name);
        registerRequest.setSurname(surname);
        
        assertEquals(name, registerRequest.getName());
        assertEquals(surname, registerRequest.getSurname());
        
        registerRequest.setName("Jane");
        
        assertEquals("Jane", registerRequest.getName());
        assertEquals(surname, registerRequest.getSurname());
    }

    @Test
    void testEmailAndPasswordIndependence() {
        String email = "test@example.com";
        String password = "password123";
        
        registerRequest.setEmail(email);
        registerRequest.setPassword(password);
        
        assertEquals(email, registerRequest.getEmail());
        assertEquals(password, registerRequest.getPassword());
        
        registerRequest.setEmail("new@example.com");
        
        assertEquals("new@example.com", registerRequest.getEmail());
        assertEquals(password, registerRequest.getPassword());
    }

    @Test
    void testAllFieldsIndependence() {
        String name = "John";
        String surname = "Doe";
        String email = "john@example.com";
        String password = "password123";
        
        registerRequest.setName(name);
        registerRequest.setSurname(surname);
        registerRequest.setEmail(email);
        registerRequest.setPassword(password);
        
        registerRequest.setName("Jane");
        assertEquals("Jane", registerRequest.getName());
        assertEquals(surname, registerRequest.getSurname());
        assertEquals(email, registerRequest.getEmail());
        assertEquals(password, registerRequest.getPassword());
        
        registerRequest.setSurname("Smith");
        assertEquals("Jane", registerRequest.getName());
        assertEquals("Smith", registerRequest.getSurname());
        assertEquals(email, registerRequest.getEmail());
        assertEquals(password, registerRequest.getPassword());
        
        registerRequest.setEmail("jane@example.com");
        assertEquals("Jane", registerRequest.getName());
        assertEquals("Smith", registerRequest.getSurname());
        assertEquals("jane@example.com", registerRequest.getEmail());
        assertEquals(password, registerRequest.getPassword());
        
        registerRequest.setPassword("newpassword");
        assertEquals("Jane", registerRequest.getName());
        assertEquals("Smith", registerRequest.getSurname());
        assertEquals("jane@example.com", registerRequest.getEmail());
        assertEquals("newpassword", registerRequest.getPassword());
    }

    @Test
    void testCommonRegistrationPatterns() {
        String[] names = {"John", "Jane", "Bob", "Alice", "Charlie"};
        String[] surnames = {"Doe", "Smith", "Johnson", "Brown", "Wilson"};
        String[] emails = {"john@example.com", "jane@test.com", "bob@demo.org"};
        String[] passwords = {"password123", "securePass", "myPassword"};
        
        for (String name : names) {
            registerRequest.setName(name);
            assertEquals(name, registerRequest.getName());
        }
        
        for (String surname : surnames) {
            registerRequest.setSurname(surname);
            assertEquals(surname, registerRequest.getSurname());
        }
        
        for (String email : emails) {
            registerRequest.setEmail(email);
            assertEquals(email, registerRequest.getEmail());
        }
        
        for (String password : passwords) {
            registerRequest.setPassword(password);
            assertEquals(password, registerRequest.getPassword());
        }
    }

    // Validation Tests
    @Test
    void testValidRegisterRequest() {
        registerRequest.setName("John");
        registerRequest.setSurname("Doe");
        registerRequest.setEmail("john.doe@example.com");
        registerRequest.setPassword("password123");
        
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertTrue(violations.isEmpty(), "Valid register request should have no violations");
    }

    @Test
    void testNullNameValidation() {
        registerRequest.setName(null);
        registerRequest.setSurname("Doe");
        registerRequest.setEmail("john.doe@example.com");
        registerRequest.setPassword("password123");
        
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertTrue(violations.size() >= 1, "Null name should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Name is required")));
    }

    @Test
    void testBlankNameValidation() {
        registerRequest.setName("");
        registerRequest.setSurname("Doe");
        registerRequest.setEmail("john.doe@example.com");
        registerRequest.setPassword("password123");
        
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertTrue(violations.size() >= 1, "Blank name should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Name is required")));
    }

    @Test
    void testShortNameValidation() {
        registerRequest.setName("J"); // Less than 2 characters
        registerRequest.setSurname("Doe");
        registerRequest.setEmail("john.doe@example.com");
        registerRequest.setPassword("password123");
        
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertTrue(violations.size() >= 1, "Short name should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Name must be between 2 and 50 characters")));
    }

    @Test
    void testLongNameValidation() {
        registerRequest.setName("a".repeat(51)); // More than 50 characters
        registerRequest.setSurname("Doe");
        registerRequest.setEmail("john.doe@example.com");
        registerRequest.setPassword("password123");
        
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertTrue(violations.size() >= 1, "Long name should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Name must be between 2 and 50 characters")));
    }

    @Test
    void testNullSurnameValidation() {
        registerRequest.setName("John");
        registerRequest.setSurname(null);
        registerRequest.setEmail("john.doe@example.com");
        registerRequest.setPassword("password123");
        
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertTrue(violations.size() >= 1, "Null surname should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Surname is required")));
    }

    @Test
    void testBlankSurnameValidation() {
        registerRequest.setName("John");
        registerRequest.setSurname("");
        registerRequest.setEmail("john.doe@example.com");
        registerRequest.setPassword("password123");
        
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertTrue(violations.size() >= 1, "Blank surname should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Surname is required")));
    }

    @Test
    void testShortSurnameValidation() {
        registerRequest.setName("John");
        registerRequest.setSurname("D"); // Less than 2 characters
        registerRequest.setEmail("john.doe@example.com");
        registerRequest.setPassword("password123");
        
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertTrue(violations.size() >= 1, "Short surname should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Surname must be between 2 and 50 characters")));
    }

    @Test
    void testLongSurnameValidation() {
        registerRequest.setName("John");
        registerRequest.setSurname("a".repeat(51)); // More than 50 characters
        registerRequest.setEmail("john.doe@example.com");
        registerRequest.setPassword("password123");
        
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertTrue(violations.size() >= 1, "Long surname should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Surname must be between 2 and 50 characters")));
    }

    @Test
    void testNameAndSurnameLengthBoundaries() {
        // Test minimum valid length (2 characters)
        registerRequest.setName("Jo");
        registerRequest.setSurname("Do");
        registerRequest.setEmail("john.doe@example.com");
        registerRequest.setPassword("password123");
        
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertTrue(violations.isEmpty(), "2-character name and surname should be valid");
        
        // Test maximum valid length (50 characters)
        registerRequest.setName("a".repeat(50));
        registerRequest.setSurname("b".repeat(50));
        violations = validator.validate(registerRequest);
        assertTrue(violations.isEmpty(), "50-character name and surname should be valid");
    }

    @Test
    void testInheritedEmailValidation() {
        registerRequest.setName("John");
        registerRequest.setSurname("Doe");
        registerRequest.setEmail("invalid-email");
        registerRequest.setPassword("password123");
        
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertTrue(violations.size() >= 1, "Invalid email should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email must be a valid email address")));
    }

    @Test
    void testInheritedPasswordValidation() {
        registerRequest.setName("John");
        registerRequest.setSurname("Doe");
        registerRequest.setEmail("john.doe@example.com");
        registerRequest.setPassword("123"); // Too short
        
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertTrue(violations.size() >= 1, "Short password should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Password must be between 6 and 100 characters")));
    }

    @Test
    void testMultipleValidationErrors() {
        registerRequest.setName(""); // Blank name
        registerRequest.setSurname(""); // Blank surname
        registerRequest.setEmail("invalid-email"); // Invalid email
        registerRequest.setPassword("123"); // Too short password
        
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(registerRequest);
        assertTrue(violations.size() >= 4, "Multiple validation errors should be detected");
    }
}
