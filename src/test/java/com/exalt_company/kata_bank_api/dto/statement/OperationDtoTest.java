package com.exalt_company.kata_bank_api.dto.statement;

import com.exalt_company.kata_bank_api.enums.AuditOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class OperationDtoTest {

    private OperationDto operationDto;

    @BeforeEach
    void setUp() {
        operationDto = new OperationDto();
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(operationDto);
        assertEquals(0, operationDto.getId());
        assertNull(operationDto.getOperation());
        assertNull(operationDto.getOperationAuthor());
    }

    @Test
    void testSetAndGetOperation() {
        AuditOperation operation = AuditOperation.DEPOSIT;
        
        operationDto.setOperation(operation);
        
        assertEquals(operation, operationDto.getOperation());
    }

    @Test
    void testSetAndGetOperationAuthor() {
        String author = "John Doe";
        
        operationDto.setOperationAuthor(author);
        
        assertEquals(author, operationDto.getOperationAuthor());
    }

    @Test
    void testInheritedId() {
        operationDto.setId(123L);
        
        assertEquals(123L, operationDto.getId());
    }

    @Test
    void testAllFieldsTogether() {
        AuditOperation operation = AuditOperation.WITHDRAW;
        String author = "Jane Smith";
        long id = 456L;
        
        operationDto.setId(id);
        operationDto.setOperation(operation);
        operationDto.setOperationAuthor(author);
        
        assertEquals(id, operationDto.getId());
        assertEquals(operation, operationDto.getOperation());
        assertEquals(author, operationDto.getOperationAuthor());
    }

    @Test
    void testNullValues() {
        operationDto.setOperation(null);
        operationDto.setOperationAuthor(null);
        
        assertNull(operationDto.getOperation());
        assertNull(operationDto.getOperationAuthor());
    }

    @Test
    void testEmptyStringValues() {
        String emptyAuthor = "";
        String whitespaceAuthor = "   ";
        
        operationDto.setOperationAuthor(emptyAuthor);
        
        assertEquals(emptyAuthor, operationDto.getOperationAuthor());
        
        operationDto.setOperationAuthor(whitespaceAuthor);
        
        assertEquals(whitespaceAuthor, operationDto.getOperationAuthor());
    }

    @Test
    void testAllAuditOperations() {
        AuditOperation[] operations = AuditOperation.values();
        
        for (AuditOperation operation : operations) {
            operationDto.setOperation(operation);
            
            assertEquals(operation, operationDto.getOperation());
        }
    }

    @Test
    void testLongAuthorName() {
        String longAuthor = "This is a very long author name that contains multiple words and should be properly handled by the DTO class. It should not cause any issues with the setter or getter methods.";
        
        operationDto.setOperationAuthor(longAuthor);
        
        assertEquals(longAuthor, operationDto.getOperationAuthor());
    }

    @Test
    void testSpecialCharactersInAuthor() {
        String specialAuthor = "John O'Connor-Smith Jr. (CEO) & Co.";
        
        operationDto.setOperationAuthor(specialAuthor);
        
        assertEquals(specialAuthor, operationDto.getOperationAuthor());
    }

    @Test
    void testUnicodeCharactersInAuthor() {
        String unicodeAuthor = "José María García-López";
        
        operationDto.setOperationAuthor(unicodeAuthor);
        
        assertEquals(unicodeAuthor, operationDto.getOperationAuthor());
    }

    @Test
    void testMultipleOperations() {
        AuditOperation[] operations = {AuditOperation.OPEN, AuditOperation.DEPOSIT, AuditOperation.WITHDRAW, AuditOperation.CLOSE};
        
        for (int i = 0; i < operations.length; i++) {
            operationDto.setOperation(operations[i]);
            
            assertEquals(operations[i], operationDto.getOperation());
        }
    }

    @Test
    void testMultipleAuthors() {
        String[] authors = {"John Doe", "Jane Smith", "Bob Johnson", "Alice Brown"};
        
        for (int i = 0; i < authors.length; i++) {
            operationDto.setOperationAuthor(authors[i]);
            
            assertEquals(authors[i], operationDto.getOperationAuthor());
        }
    }

    @Test
    void testOperationAndAuthorIndependence() {
        AuditOperation operation = AuditOperation.OVERDRAW_REQUEST;
        String author = "Risk Manager";
        
        operationDto.setOperation(operation);
        
        assertEquals(operation, operationDto.getOperation());
        assertNull(operationDto.getOperationAuthor());
        
        operationDto.setOperationAuthor(author);
        
        assertEquals(operation, operationDto.getOperation());
        assertEquals(author, operationDto.getOperationAuthor());
        
        AuditOperation newOperation = AuditOperation.OVERDRAW_CANCEL;
        operationDto.setOperation(newOperation);
        
        assertEquals(newOperation, operationDto.getOperation());
        assertEquals(author, operationDto.getOperationAuthor());
    }

    @Test
    void testIdIndependence() {
        long initialId = 100L;
        long newId = 200L;
        
        operationDto.setId(initialId);
        
        assertEquals(initialId, operationDto.getId());
        
        operationDto.setId(newId);
        
        assertEquals(newId, operationDto.getId());
        
        assertNull(operationDto.getOperation());
        assertNull(operationDto.getOperationAuthor());
    }

    @Test
    void testBoundaryValues() {
        operationDto.setId(0L);
        assertEquals(0L, operationDto.getId());
        
        operationDto.setId(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, operationDto.getId());
        
        operationDto.setId(Long.MIN_VALUE);
        assertEquals(Long.MIN_VALUE, operationDto.getId());
    }
}
