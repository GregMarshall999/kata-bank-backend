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

class BankUserDtoTest {
    private BankUserDto bankUserDto;
    private Validator validator;

    @BeforeEach
    void setUp() {
        bankUserDto = new BankUserDto();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(bankUserDto);
        assertEquals(0L, bankUserDto.getId());
        assertNull(bankUserDto.getName());
        assertNull(bankUserDto.getSurname());
        assertNull(bankUserDto.getEmail());
        assertNull(bankUserDto.getBankRole());
        assertEquals(0L, bankUserDto.getAdvisorId());
    }

    @Test
    void testSetAndGetName() {
        String name = "John";
        
        bankUserDto.setName(name);
        
        assertEquals(name, bankUserDto.getName());
    }

    @Test
    void testSetAndGetSurname() {
        String surname = "Doe";
        
        bankUserDto.setSurname(surname);
        
        assertEquals(surname, bankUserDto.getSurname());
    }

    @Test
    void testSetAndGetEmail() {
        String email = "john.doe@example.com";
        
        bankUserDto.setEmail(email);
        
        assertEquals(email, bankUserDto.getEmail());
    }

    @Test
    void testSetAndGetBankRole() {
        BankRole role = BankRole.CLIENT;
        
        bankUserDto.setBankRole(role);
        
        assertEquals(role, bankUserDto.getBankRole());
    }

    @Test
    void testSetAndGetAdvisorId() {
        long advisorId = 123L;
        
        bankUserDto.setAdvisorId(advisorId);
        
        assertEquals(advisorId, bankUserDto.getAdvisorId());
    }

    @Test
    void testAllFieldsTogether() {
        long id = 1L;
        String name = "Jane";
        String surname = "Smith";
        String email = "jane.smith@example.com";
        BankRole role = BankRole.ADVISOR;
        long advisorId = 456L;
        
        bankUserDto.setId(id);
        bankUserDto.setName(name);
        bankUserDto.setSurname(surname);
        bankUserDto.setEmail(email);
        bankUserDto.setBankRole(role);
        bankUserDto.setAdvisorId(advisorId);
        
        assertEquals(id, bankUserDto.getId());
        assertEquals(name, bankUserDto.getName());
        assertEquals(surname, bankUserDto.getSurname());
        assertEquals(email, bankUserDto.getEmail());
        assertEquals(role, bankUserDto.getBankRole());
        assertEquals(advisorId, bankUserDto.getAdvisorId());
    }

    @Test
    void testNullValues() {
        bankUserDto.setName(null);
        bankUserDto.setSurname(null);
        bankUserDto.setEmail(null);
        bankUserDto.setBankRole(null);
        
        assertNull(bankUserDto.getName());
        assertNull(bankUserDto.getSurname());
        assertNull(bankUserDto.getEmail());
        assertNull(bankUserDto.getBankRole());
    }

    @Test
    void testEmptyStringValues() {
        bankUserDto.setName("");
        bankUserDto.setSurname("");
        bankUserDto.setEmail("");
        
        assertEquals("", bankUserDto.getName());
        assertEquals("", bankUserDto.getSurname());
        assertEquals("", bankUserDto.getEmail());
    }

    @Test
    void testAllBankRoles() {
        BankRole[] roles = BankRole.values();
        
        for (BankRole role : roles) {
            bankUserDto.setBankRole(role);
            assertEquals(role, bankUserDto.getBankRole());
        }
    }

    @Test
    void testLongNameAndSurname() {
        String longName = "This is a very long name that contains multiple words and should be properly handled";
        String longSurname = "This is a very long surname that contains multiple words and should be properly handled";
        
        bankUserDto.setName(longName);
        bankUserDto.setSurname(longSurname);
        
        assertEquals(longName, bankUserDto.getName());
        assertEquals(longSurname, bankUserDto.getSurname());
    }

    @Test
    void testSpecialCharactersInName() {
        String nameWithSpecialChars = "José María O'Connor-Smith Jr.";
        String surnameWithSpecialChars = "García-López & Associates";
        
        bankUserDto.setName(nameWithSpecialChars);
        bankUserDto.setSurname(surnameWithSpecialChars);
        
        assertEquals(nameWithSpecialChars, bankUserDto.getName());
        assertEquals(surnameWithSpecialChars, bankUserDto.getSurname());
    }

    @Test
    void testUnicodeCharacters() {
        String unicodeName = "José María";
        String unicodeSurname = "García-López";
        String unicodeEmail = "josé.maría@exämple.com";
        
        bankUserDto.setName(unicodeName);
        bankUserDto.setSurname(unicodeSurname);
        bankUserDto.setEmail(unicodeEmail);
        
        assertEquals(unicodeName, bankUserDto.getName());
        assertEquals(unicodeSurname, bankUserDto.getSurname());
        assertEquals(unicodeEmail, bankUserDto.getEmail());
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
            bankUserDto.setEmail(email);
            assertEquals(email, bankUserDto.getEmail());
        }
    }

    @Test
    void testAdvisorIdWithDifferentValues() {
        long[] advisorIds = {0L, 1L, 100L, 999L, Long.MAX_VALUE, Long.MIN_VALUE};
        
        for (long advisorId : advisorIds) {
            bankUserDto.setAdvisorId(advisorId);
            assertEquals(advisorId, bankUserDto.getAdvisorId());
        }
    }

    @Test
    void testNegativeAdvisorId() {
        long negativeAdvisorId = -1L;
        
        bankUserDto.setAdvisorId(negativeAdvisorId);
        
        assertEquals(negativeAdvisorId, bankUserDto.getAdvisorId());
    }

    @Test
    void testMultipleFieldUpdates() {
        bankUserDto.setName("John");
        bankUserDto.setSurname("Doe");
        bankUserDto.setEmail("john@example.com");
        bankUserDto.setBankRole(BankRole.CLIENT);
        
        bankUserDto.setName("Jane");
        bankUserDto.setSurname("Smith");
        bankUserDto.setEmail("jane@example.com");
        bankUserDto.setBankRole(BankRole.ADVISOR);
        
        assertEquals("Jane", bankUserDto.getName());
        assertEquals("Smith", bankUserDto.getSurname());
        assertEquals("jane@example.com", bankUserDto.getEmail());
        assertEquals(BankRole.ADVISOR, bankUserDto.getBankRole());
    }

    @Test
    void testInheritedIdField() {
        long testId = 789L;
        
        bankUserDto.setId(testId);
        
        assertEquals(testId, bankUserDto.getId());
    }

    @Test
    void testWhitespaceInNames() {
        String nameWithWhitespace = "  John  ";
        String surnameWithWhitespace = "  Doe  ";
        
        bankUserDto.setName(nameWithWhitespace);
        bankUserDto.setSurname(surnameWithWhitespace);
        
        assertEquals(nameWithWhitespace, bankUserDto.getName());
        assertEquals(surnameWithWhitespace, bankUserDto.getSurname());
    }

    @Test
    void testNumbersInNames() {
        String nameWithNumbers = "John123";
        String surnameWithNumbers = "Doe456";
        
        bankUserDto.setName(nameWithNumbers);
        bankUserDto.setSurname(surnameWithNumbers);
        
        assertEquals(nameWithNumbers, bankUserDto.getName());
        assertEquals(surnameWithNumbers, bankUserDto.getSurname());
    }

    // Validation Tests
    @Test
    void testValidBankUserDto() {
        bankUserDto.setName("John");
        bankUserDto.setSurname("Doe");
        bankUserDto.setEmail("john.doe@example.com");
        bankUserDto.setBankRole(BankRole.CLIENT);
        bankUserDto.setAdvisorId(1L);
        
        Set<ConstraintViolation<BankUserDto>> violations = validator.validate(bankUserDto);
        assertTrue(violations.isEmpty(), "Valid bank user DTO should have no violations");
    }

    @Test
    void testNullNameValidation() {
        bankUserDto.setName(null);
        bankUserDto.setSurname("Doe");
        bankUserDto.setEmail("john.doe@example.com");
        bankUserDto.setBankRole(BankRole.CLIENT);
        bankUserDto.setAdvisorId(1L);
        
        Set<ConstraintViolation<BankUserDto>> violations = validator.validate(bankUserDto);
        assertTrue(violations.size() >= 1, "Null name should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Name is required")));
    }

    @Test
    void testBlankNameValidation() {
        bankUserDto.setName("");
        bankUserDto.setSurname("Doe");
        bankUserDto.setEmail("john.doe@example.com");
        bankUserDto.setBankRole(BankRole.CLIENT);
        bankUserDto.setAdvisorId(1L);
        
        Set<ConstraintViolation<BankUserDto>> violations = validator.validate(bankUserDto);
        assertTrue(violations.size() >= 1, "Blank name should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Name is required")));
    }

    @Test
    void testShortNameValidation() {
        bankUserDto.setName("J"); // Less than 2 characters
        bankUserDto.setSurname("Doe");
        bankUserDto.setEmail("john.doe@example.com");
        bankUserDto.setBankRole(BankRole.CLIENT);
        bankUserDto.setAdvisorId(1L);
        
        Set<ConstraintViolation<BankUserDto>> violations = validator.validate(bankUserDto);
        assertTrue(violations.size() >= 1, "Short name should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Name must be between 2 and 50 characters")));
    }

    @Test
    void testLongNameValidation() {
        bankUserDto.setName("a".repeat(51)); // More than 50 characters
        bankUserDto.setSurname("Doe");
        bankUserDto.setEmail("john.doe@example.com");
        bankUserDto.setBankRole(BankRole.CLIENT);
        bankUserDto.setAdvisorId(1L);
        
        Set<ConstraintViolation<BankUserDto>> violations = validator.validate(bankUserDto);
        assertTrue(violations.size() >= 1, "Long name should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Name must be between 2 and 50 characters")));
    }

    @Test
    void testNullSurnameValidation() {
        bankUserDto.setName("John");
        bankUserDto.setSurname(null);
        bankUserDto.setEmail("john.doe@example.com");
        bankUserDto.setBankRole(BankRole.CLIENT);
        bankUserDto.setAdvisorId(1L);
        
        Set<ConstraintViolation<BankUserDto>> violations = validator.validate(bankUserDto);
        assertTrue(violations.size() >= 1, "Null surname should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Surname is required")));
    }

    @Test
    void testBlankSurnameValidation() {
        bankUserDto.setName("John");
        bankUserDto.setSurname("");
        bankUserDto.setEmail("john.doe@example.com");
        bankUserDto.setBankRole(BankRole.CLIENT);
        bankUserDto.setAdvisorId(1L);
        
        Set<ConstraintViolation<BankUserDto>> violations = validator.validate(bankUserDto);
        assertTrue(violations.size() >= 1, "Blank surname should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Surname is required")));
    }

    @Test
    void testShortSurnameValidation() {
        bankUserDto.setName("John");
        bankUserDto.setSurname("D"); // Less than 2 characters
        bankUserDto.setEmail("john.doe@example.com");
        bankUserDto.setBankRole(BankRole.CLIENT);
        bankUserDto.setAdvisorId(1L);
        
        Set<ConstraintViolation<BankUserDto>> violations = validator.validate(bankUserDto);
        assertTrue(violations.size() >= 1, "Short surname should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Surname must be between 2 and 50 characters")));
    }

    @Test
    void testLongSurnameValidation() {
        bankUserDto.setName("John");
        bankUserDto.setSurname("a".repeat(51)); // More than 50 characters
        bankUserDto.setEmail("john.doe@example.com");
        bankUserDto.setBankRole(BankRole.CLIENT);
        bankUserDto.setAdvisorId(1L);
        
        Set<ConstraintViolation<BankUserDto>> violations = validator.validate(bankUserDto);
        assertTrue(violations.size() >= 1, "Long surname should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Surname must be between 2 and 50 characters")));
    }

    @Test
    void testNullEmailValidation() {
        bankUserDto.setName("John");
        bankUserDto.setSurname("Doe");
        bankUserDto.setEmail(null);
        bankUserDto.setBankRole(BankRole.CLIENT);
        bankUserDto.setAdvisorId(1L);
        
        Set<ConstraintViolation<BankUserDto>> violations = validator.validate(bankUserDto);
        assertTrue(violations.size() >= 1, "Null email should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email is required")));
    }

    @Test
    void testBlankEmailValidation() {
        bankUserDto.setName("John");
        bankUserDto.setSurname("Doe");
        bankUserDto.setEmail("");
        bankUserDto.setBankRole(BankRole.CLIENT);
        bankUserDto.setAdvisorId(1L);
        
        Set<ConstraintViolation<BankUserDto>> violations = validator.validate(bankUserDto);
        assertTrue(violations.size() >= 1, "Blank email should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email is required")));
    }

    @Test
    void testInvalidEmailFormatValidation() {
        bankUserDto.setName("John");
        bankUserDto.setSurname("Doe");
        bankUserDto.setEmail("invalid-email");
        bankUserDto.setBankRole(BankRole.CLIENT);
        bankUserDto.setAdvisorId(1L);
        
        Set<ConstraintViolation<BankUserDto>> violations = validator.validate(bankUserDto);
        assertTrue(violations.size() >= 1, "Invalid email format should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email must be a valid email address")));
    }

    @Test
    void testNullBankRoleValidation() {
        bankUserDto.setName("John");
        bankUserDto.setSurname("Doe");
        bankUserDto.setEmail("john.doe@example.com");
        bankUserDto.setBankRole(null);
        bankUserDto.setAdvisorId(1L);
        
        Set<ConstraintViolation<BankUserDto>> violations = validator.validate(bankUserDto);
        assertTrue(violations.size() >= 1, "Null bank role should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Bank role is required")));
    }

    @Test
    void testNegativeAdvisorIdValidation() {
        bankUserDto.setName("John");
        bankUserDto.setSurname("Doe");
        bankUserDto.setEmail("john.doe@example.com");
        bankUserDto.setBankRole(BankRole.CLIENT);
        bankUserDto.setAdvisorId(-1L);
        
        Set<ConstraintViolation<BankUserDto>> violations = validator.validate(bankUserDto);
        assertTrue(violations.size() >= 1, "Negative advisor ID should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Advisor ID must be a positive number")));
    }

    @Test
    void testZeroAdvisorIdValidation() {
        bankUserDto.setName("John");
        bankUserDto.setSurname("Doe");
        bankUserDto.setEmail("john.doe@example.com");
        bankUserDto.setBankRole(BankRole.CLIENT);
        bankUserDto.setAdvisorId(0L);
        
        Set<ConstraintViolation<BankUserDto>> violations = validator.validate(bankUserDto);
        assertTrue(violations.size() >= 1, "Zero advisor ID should have validation violations");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Advisor ID must be a positive number")));
    }

    @Test
    void testNameAndSurnameLengthBoundaries() {
        // Test minimum valid length (2 characters)
        bankUserDto.setName("Jo");
        bankUserDto.setSurname("Do");
        bankUserDto.setEmail("john.doe@example.com");
        bankUserDto.setBankRole(BankRole.CLIENT);
        bankUserDto.setAdvisorId(1L);
        
        Set<ConstraintViolation<BankUserDto>> violations = validator.validate(bankUserDto);
        assertTrue(violations.isEmpty(), "2-character name and surname should be valid");
        
        // Test maximum valid length (50 characters)
        bankUserDto.setName("a".repeat(50));
        bankUserDto.setSurname("b".repeat(50));
        violations = validator.validate(bankUserDto);
        assertTrue(violations.isEmpty(), "50-character name and surname should be valid");
    }

    @Test
    void testAllBankRolesWithValidation() {
        bankUserDto.setName("John");
        bankUserDto.setSurname("Doe");
        bankUserDto.setEmail("john.doe@example.com");
        bankUserDto.setAdvisorId(1L);
        
        for (BankRole role : BankRole.values()) {
            bankUserDto.setBankRole(role);
            
            Set<ConstraintViolation<BankUserDto>> violations = validator.validate(bankUserDto);
            assertTrue(violations.isEmpty(), "Valid bank role '" + role + "' should have no violations");
        }
    }

    @Test
    void testMultipleValidationErrors() {
        bankUserDto.setName(""); // Blank name
        bankUserDto.setSurname(""); // Blank surname
        bankUserDto.setEmail("invalid-email"); // Invalid email
        bankUserDto.setBankRole(null); // Null bank role
        bankUserDto.setAdvisorId(-1L); // Negative advisor ID
        
        Set<ConstraintViolation<BankUserDto>> violations = validator.validate(bankUserDto);
        assertTrue(violations.size() >= 5, "Multiple validation errors should be detected");
    }
}
