package com.exalt_company.kata_bank_api.dto;

import com.exalt_company.kata_bank_api.enums.BankRole;
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

class PasswordedBankUserDtoTest {
    private PasswordedBankUserDto passwordedBankUserDto;
    private Validator validator;

    @BeforeEach
    void setUp() {
        passwordedBankUserDto = new PasswordedBankUserDto();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(passwordedBankUserDto);
        assertEquals(0L, passwordedBankUserDto.getId());
        assertNull(passwordedBankUserDto.getName());
        assertNull(passwordedBankUserDto.getSurname());
        assertNull(passwordedBankUserDto.getEmail());
        assertNull(passwordedBankUserDto.getBankRole());
        assertEquals(0L, passwordedBankUserDto.getAdvisorId());
        assertNull(passwordedBankUserDto.getPassword());
    }

    @Test
    void testSetAndGetPassword() {
        String password = "TempPass123!";
        
        passwordedBankUserDto.setPassword(password);
        
        assertEquals(password, passwordedBankUserDto.getPassword());
    }

    @Test
    void testInheritedFields() {
        long id = 1L;
        String name = "John";
        String surname = "Doe";
        String email = "john.doe@example.com";
        BankRole role = BankRole.CLIENT;
        long advisorId = 123L;
        String password = "TempPass123!";
        
        passwordedBankUserDto.setId(id);
        passwordedBankUserDto.setName(name);
        passwordedBankUserDto.setSurname(surname);
        passwordedBankUserDto.setEmail(email);
        passwordedBankUserDto.setBankRole(role);
        passwordedBankUserDto.setAdvisorId(advisorId);
        passwordedBankUserDto.setPassword(password);
        
        assertEquals(id, passwordedBankUserDto.getId());
        assertEquals(name, passwordedBankUserDto.getName());
        assertEquals(surname, passwordedBankUserDto.getSurname());
        assertEquals(email, passwordedBankUserDto.getEmail());
        assertEquals(role, passwordedBankUserDto.getBankRole());
        assertEquals(advisorId, passwordedBankUserDto.getAdvisorId());
        assertEquals(password, passwordedBankUserDto.getPassword());
    }

    @Test
    void testNullPassword() {
        passwordedBankUserDto.setPassword(null);
        
        assertNull(passwordedBankUserDto.getPassword());
    }

    @Test
    void testEmptyPassword() {
        passwordedBankUserDto.setPassword("");
        
        assertEquals("", passwordedBankUserDto.getPassword());
    }

    @Test
    void testPasswordWithSpecialCharacters() {
        String passwordWithSpecialChars = "P@ssw0rd!@#$%^&*()_+-=[]{}|;':\",./<>?";
        
        passwordedBankUserDto.setPassword(passwordWithSpecialChars);
        
        assertEquals(passwordWithSpecialChars, passwordedBankUserDto.getPassword());
    }

    @Test
    void testLongPassword() {
        String longPassword = "a".repeat(1000);
        
        passwordedBankUserDto.setPassword(longPassword);
        
        assertEquals(longPassword, passwordedBankUserDto.getPassword());
    }

    @Test
    void testPasswordWithUnicodeCharacters() {
        String unicodePassword = "pässwörd123";
        
        passwordedBankUserDto.setPassword(unicodePassword);
        
        assertEquals(unicodePassword, passwordedBankUserDto.getPassword());
    }

    @Test
    void testPasswordWithWhitespace() {
        String passwordWithWhitespace = "  password123  ";
        
        passwordedBankUserDto.setPassword(passwordWithWhitespace);
        
        assertEquals(passwordWithWhitespace, passwordedBankUserDto.getPassword());
    }

    @Test
    void testPasswordWithNumbers() {
        String numericPassword = "123456789";
        
        passwordedBankUserDto.setPassword(numericPassword);
        
        assertEquals(numericPassword, passwordedBankUserDto.getPassword());
    }

    @Test
    void testPasswordWithMixedCase() {
        String mixedCasePassword = "PaSsWoRd123";
        
        passwordedBankUserDto.setPassword(mixedCasePassword);
        
        assertEquals(mixedCasePassword, passwordedBankUserDto.getPassword());
    }

    @Test
    void testMultiplePasswordChanges() {
        String password1 = "password1";
        String password2 = "password2";
        String password3 = "password3";
        
        passwordedBankUserDto.setPassword(password1);
        assertEquals(password1, passwordedBankUserDto.getPassword());
        
        passwordedBankUserDto.setPassword(password2);
        assertEquals(password2, passwordedBankUserDto.getPassword());
        
        passwordedBankUserDto.setPassword(password3);
        assertEquals(password3, passwordedBankUserDto.getPassword());
    }

    @Test
    void testPasswordIndependenceFromOtherFields() {
        passwordedBankUserDto.setName("John");
        passwordedBankUserDto.setEmail("john@example.com");
        passwordedBankUserDto.setBankRole(BankRole.ADMIN);
        
        String password = "admin123";
        passwordedBankUserDto.setPassword(password);
        
        assertEquals(password, passwordedBankUserDto.getPassword());
        assertEquals("John", passwordedBankUserDto.getName());
        assertEquals("john@example.com", passwordedBankUserDto.getEmail());
        assertEquals(BankRole.ADMIN, passwordedBankUserDto.getBankRole());
    }

    @Test
    void testAllFieldsWithRealisticValues() {
        long id = 1001L;
        String name = "Administrator";
        String surname = "User";
        String email = "admin@bank.com";
        BankRole role = BankRole.ADMIN;
        long advisorId = 0L;
        String password = "TempAdminPass2024!";
        
        passwordedBankUserDto.setId(id);
        passwordedBankUserDto.setName(name);
        passwordedBankUserDto.setSurname(surname);
        passwordedBankUserDto.setEmail(email);
        passwordedBankUserDto.setBankRole(role);
        passwordedBankUserDto.setAdvisorId(advisorId);
        passwordedBankUserDto.setPassword(password);
        
        assertEquals(id, passwordedBankUserDto.getId());
        assertEquals(name, passwordedBankUserDto.getName());
        assertEquals(surname, passwordedBankUserDto.getSurname());
        assertEquals(email, passwordedBankUserDto.getEmail());
        assertEquals(role, passwordedBankUserDto.getBankRole());
        assertEquals(advisorId, passwordedBankUserDto.getAdvisorId());
        assertEquals(password, passwordedBankUserDto.getPassword());
    }

    @Test
    void testPasswordWithCommonPatterns() {
        String[] commonPasswords = {
            "password",
            "123456",
            "qwerty",
            "abc123",
            "password123",
            "admin",
            "root",
            "user"
        };
        
        for (String password : commonPasswords) {
            passwordedBankUserDto.setPassword(password);
            assertEquals(password, passwordedBankUserDto.getPassword());
        }
    }

    @Test
    void testPasswordWithNewlinesAndTabs() {
        String passwordWithNewlines = "pass\nword\t123";
        
        passwordedBankUserDto.setPassword(passwordWithNewlines);
        
        assertEquals(passwordWithNewlines, passwordedBankUserDto.getPassword());
    }

    @Test
    void testValidPasswordedBankUserDto() {
        passwordedBankUserDto.setName("John");
        passwordedBankUserDto.setSurname("Doe");
        passwordedBankUserDto.setEmail("john.doe@example.com");
        passwordedBankUserDto.setBankRole(BankRole.CLIENT);
        passwordedBankUserDto.setAdvisorId(1L);
        passwordedBankUserDto.setPassword("TempPass123!");
        
        Set<ConstraintViolation<PasswordedBankUserDto>> violations = validator.validate(passwordedBankUserDto);
        assertTrue(violations.isEmpty(), "Valid passworded bank user DTO should have no violations");
    }

    @Test
    void testNullPasswordValidation() {
        passwordedBankUserDto.setName("John");
        passwordedBankUserDto.setSurname("Doe");
        passwordedBankUserDto.setEmail("john.doe@example.com");
        passwordedBankUserDto.setBankRole(BankRole.CLIENT);
        passwordedBankUserDto.setAdvisorId(1L);
        passwordedBankUserDto.setPassword(null);
        
        Set<ConstraintViolation<PasswordedBankUserDto>> violations = validator.validate(passwordedBankUserDto);
        assertTrue(violations.size() >= 1, "Null password should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Password is required")));
    }

    @Test
    void testBlankPasswordValidation() {
        passwordedBankUserDto.setName("John");
        passwordedBankUserDto.setSurname("Doe");
        passwordedBankUserDto.setEmail("john.doe@example.com");
        passwordedBankUserDto.setBankRole(BankRole.CLIENT);
        passwordedBankUserDto.setAdvisorId(1L);
        passwordedBankUserDto.setPassword("");
        
        Set<ConstraintViolation<PasswordedBankUserDto>> violations = validator.validate(passwordedBankUserDto);
        assertTrue(violations.size() >= 1, "Blank password should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Password is required")));
    }

    @Test
    void testShortPasswordValidation() {
        passwordedBankUserDto.setName("John");
        passwordedBankUserDto.setSurname("Doe");
        passwordedBankUserDto.setEmail("john.doe@example.com");
        passwordedBankUserDto.setBankRole(BankRole.CLIENT);
        passwordedBankUserDto.setAdvisorId(1L);
        passwordedBankUserDto.setPassword("12345");
        
        Set<ConstraintViolation<PasswordedBankUserDto>> violations = validator.validate(passwordedBankUserDto);
        assertTrue(violations.size() >= 1, "Short password should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Password must be between 6 and 100 characters")));
    }

    @Test
    void testLongPasswordValidation() {
        passwordedBankUserDto.setName("John");
        passwordedBankUserDto.setSurname("Doe");
        passwordedBankUserDto.setEmail("john.doe@example.com");
        passwordedBankUserDto.setBankRole(BankRole.CLIENT);
        passwordedBankUserDto.setAdvisorId(1L);
        passwordedBankUserDto.setPassword("a".repeat(101));
        
        Set<ConstraintViolation<PasswordedBankUserDto>> violations = validator.validate(passwordedBankUserDto);
        assertTrue(violations.size() >= 1, "Long password should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Password must be between 6 and 100 characters")));
    }

    @Test
    void testPasswordLengthBoundaries() {
        passwordedBankUserDto.setName("John");
        passwordedBankUserDto.setSurname("Doe");
        passwordedBankUserDto.setEmail("john.doe@example.com");
        passwordedBankUserDto.setBankRole(BankRole.CLIENT);
        passwordedBankUserDto.setAdvisorId(1L);
        
        passwordedBankUserDto.setPassword("123456");
        Set<ConstraintViolation<PasswordedBankUserDto>> violations = validator.validate(passwordedBankUserDto);
        assertTrue(violations.isEmpty(), "6-character password should be valid");
        
        passwordedBankUserDto.setPassword("a".repeat(100));
        violations = validator.validate(passwordedBankUserDto);
        assertTrue(violations.isEmpty(), "100-character password should be valid");
    }

    @Test
    void testInheritedValidationFromBankUserDto() {
        passwordedBankUserDto.setName("");
        passwordedBankUserDto.setSurname("");
        passwordedBankUserDto.setEmail("invalid-email");
        passwordedBankUserDto.setBankRole(null);
        passwordedBankUserDto.setAdvisorId(-1L);
        passwordedBankUserDto.setPassword("TempPass123!");
        
        Set<ConstraintViolation<PasswordedBankUserDto>> violations = validator.validate(passwordedBankUserDto);
        assertTrue(violations.size() >= 5, "Inherited validation should be applied");
    }

    @Test
    void testMultipleValidationErrors() {
        passwordedBankUserDto.setName("");
        passwordedBankUserDto.setSurname("");
        passwordedBankUserDto.setEmail("invalid-email");
        passwordedBankUserDto.setBankRole(null);
        passwordedBankUserDto.setAdvisorId(-1L);
        passwordedBankUserDto.setPassword("123");
        
        Set<ConstraintViolation<PasswordedBankUserDto>> violations = validator.validate(passwordedBankUserDto);
        assertTrue(violations.size() >= 6, "Multiple validation errors should be detected");
    }

    @Test
    void testValidPasswordWithSpecialCharacters() {
        passwordedBankUserDto.setName("John");
        passwordedBankUserDto.setSurname("Doe");
        passwordedBankUserDto.setEmail("john.doe@example.com");
        passwordedBankUserDto.setBankRole(BankRole.CLIENT);
        passwordedBankUserDto.setAdvisorId(1L);
        passwordedBankUserDto.setPassword("P@ssw0rd!@#$%^&*()");
        
        Set<ConstraintViolation<PasswordedBankUserDto>> violations = validator.validate(passwordedBankUserDto);
        assertTrue(violations.isEmpty(), "Password with special characters should be valid");
    }

    @Test
    void testValidPasswordWithUnicodeCharacters() {
        passwordedBankUserDto.setName("John");
        passwordedBankUserDto.setSurname("Doe");
        passwordedBankUserDto.setEmail("john.doe@example.com");
        passwordedBankUserDto.setBankRole(BankRole.CLIENT);
        passwordedBankUserDto.setAdvisorId(1L);
        passwordedBankUserDto.setPassword("pässwörd123");
        
        Set<ConstraintViolation<PasswordedBankUserDto>> violations = validator.validate(passwordedBankUserDto);
        assertTrue(violations.isEmpty(), "Password with unicode characters should be valid");
    }
}
