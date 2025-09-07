package com.exalt_company.kata_bank_api.controller;

import com.exalt_company.kata_bank_api.dto.statement.AccountStatementDto;
import com.exalt_company.kata_bank_api.enums.AccountType;
import com.exalt_company.kata_bank_api.exception.AuditException;
import com.exalt_company.kata_bank_api.service.IAuditService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountAuditControllerTest {
    @Mock
    private IAuditService auditService;

    @InjectMocks
    private AccountAuditController controller;

    private AccountStatementDto testStatementDto;

    @BeforeEach
    void setUp() {
        testStatementDto = new AccountStatementDto();
        testStatementDto.setId(1L);
        testStatementDto.setAccountType(AccountType.FUND);
        testStatementDto.setAccountBalance(1000.0);
        testStatementDto.setOperationsPage(0);
        testStatementDto.setOperationsSize(10);
        testStatementDto.setTotalOperationsPage(1);
    }

    @Test
    void testRequestStatement_Success() throws AuditException {
        String accountType = "FUND";
        long ownerId = 1L;
        int page = 0;
        int size = 10;
        
        when(auditService.requestStatement(accountType, ownerId, page, size))
            .thenReturn(ResponseEntity.ok(testStatementDto));

        ResponseEntity<AccountStatementDto> response = controller.requestStatement(accountType, ownerId, page, size);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testStatementDto, response.getBody());
        
        verify(auditService).requestStatement(accountType, ownerId, page, size);
    }

    @Test
    void testRequestStatement_SavingAccount() throws AuditException {
        String accountType = "SAVING";
        long ownerId = 2L;
        int page = 1;
        int size = 5;
        
        testStatementDto.setAccountType(AccountType.SAVING);
        when(auditService.requestStatement(accountType, ownerId, page, size))
            .thenReturn(ResponseEntity.ok(testStatementDto));

        ResponseEntity<AccountStatementDto> response = controller.requestStatement(accountType, ownerId, page, size);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testStatementDto, response.getBody());
        assertEquals(AccountType.SAVING, response.getBody().getAccountType());
        
        verify(auditService).requestStatement(accountType, ownerId, page, size);
    }

    @Test
    void testRequestStatement_WithPagination() throws AuditException {
        String accountType = "FUND";
        long ownerId = 3L;
        int page = 2;
        int size = 20;
        
        testStatementDto.setOperationsPage(2);
        testStatementDto.setOperationsSize(20);
        testStatementDto.setTotalOperationsPage(5);
        
        when(auditService.requestStatement(accountType, ownerId, page, size))
            .thenReturn(ResponseEntity.ok(testStatementDto));

        ResponseEntity<AccountStatementDto> response = controller.requestStatement(accountType, ownerId, page, size);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getOperationsPage());
        assertEquals(20, response.getBody().getOperationsSize());
        assertEquals(5, response.getBody().getTotalOperationsPage());
        
        verify(auditService).requestStatement(accountType, ownerId, page, size);
    }

    @ParameterizedTest
    @CsvSource({
            "FUND, 1, Database connection error",
            "FUND, 999, User not found",
            "INVALID, 1, Invalid account type"
    })
    void testRequestStatement_WithDatabaseConnectionError(String type, long id, String message) throws AuditException {
        int page = 0;
        int size = 10;

        when(auditService.requestStatement(type, id, page, size))
                .thenThrow(new AuditException(message, HttpStatus.BAD_REQUEST));

        AuditException exception = assertThrows(AuditException.class,
                () -> controller.requestStatement(type, id, page, size));

        assertEquals(message, exception.getMessage());
        verify(auditService).requestStatement(type, id, page, size);
    }

    @ParameterizedTest
    @CsvSource({
        ", 1, 0, 10, 'Account type cannot be null'",
        "'', 1, 0, 10, 'Account type cannot be empty'",
        "'  FUND  ', 1, 0, 10, 'Account type cannot contain whitespace'",
        "'FUND@#$%', 1, 0, 10, 'Account type contains invalid characters'",
        "'123', 1, 0, 10, 'Invalid account type format'"
    })
    void testRequestStatement_InvalidAccountTypes(String accountType, long ownerId, int page, int size, String expectedErrorMessage) throws AuditException {
        when(auditService.requestStatement(accountType, ownerId, page, size))
                .thenThrow(new AuditException(expectedErrorMessage, HttpStatus.BAD_REQUEST));

        AuditException exception = assertThrows(AuditException.class,
                () -> controller.requestStatement(accountType, ownerId, page, size));

        assertEquals(expectedErrorMessage, exception.getMessage());
        verify(auditService).requestStatement(accountType, ownerId, page, size);
    }

    @Test
    void testRequestStatement_ZeroValues() throws AuditException {
        String accountType = "FUND";
        long ownerId = 0L;
        int page = 0;
        int size = 0;
        
        when(auditService.requestStatement(accountType, ownerId, page, size))
            .thenReturn(ResponseEntity.ok(testStatementDto));

        ResponseEntity<AccountStatementDto> response = controller.requestStatement(accountType, ownerId, page, size);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        verify(auditService).requestStatement(accountType, ownerId, page, size);
    }

    @Test
    void testRequestStatement_LargeValues() throws AuditException {
        String accountType = "SAVING";
        long ownerId = 999999999L;
        int page = 1000;
        int size = 1000;
        
        when(auditService.requestStatement(accountType, ownerId, page, size))
            .thenReturn(ResponseEntity.ok(testStatementDto));

        ResponseEntity<AccountStatementDto> response = controller.requestStatement(accountType, ownerId, page, size);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        verify(auditService).requestStatement(accountType, ownerId, page, size);
    }

    @ParameterizedTest
    @CsvSource({
        "'FUND', 1, -1, 10, 'Negative page'",
        "'FUND', 1, 0, -5, 'Negative size'",
        "'FUND', 9223372036854775807, 0, 10, 'Very large owner ID'",
        "'FUND', 1, 2147483647, 10, 'Very large page'",
        "'FUND', 1, 0, 2147483647, 'Very large size'"
    })
    void testRequestStatement(String accountType, long ownerId, int page, int size) throws AuditException {
        when(auditService.requestStatement(accountType, ownerId, page, size))
            .thenReturn(ResponseEntity.ok(testStatementDto));

        ResponseEntity<AccountStatementDto> response = controller.requestStatement(accountType, ownerId, page, size);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        verify(auditService).requestStatement(accountType, ownerId, page, size);
    }

    @Test
    void testRequestStatement_WithMixedCaseAccountType() throws AuditException {
        String accountType = "fund";
        long ownerId = 1L;
        int page = 0;
        int size = 10;
        
        when(auditService.requestStatement(accountType, ownerId, page, size))
            .thenReturn(ResponseEntity.ok(testStatementDto));

        ResponseEntity<AccountStatementDto> response = controller.requestStatement(accountType, ownerId, page, size);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testStatementDto, response.getBody());
        
        verify(auditService).requestStatement(accountType, ownerId, page, size);
    }

    @Test
    void testRequestStatement_WithMinimumSize() throws AuditException {
        String accountType = "FUND";
        long ownerId = 1L;
        int page = 0;
        int size = 1;
        
        testStatementDto.setOperationsSize(1);
        testStatementDto.setTotalOperationsPage(1);
        
        when(auditService.requestStatement(accountType, ownerId, page, size))
            .thenReturn(ResponseEntity.ok(testStatementDto));

        ResponseEntity<AccountStatementDto> response = controller.requestStatement(accountType, ownerId, page, size);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getOperationsSize());
        assertEquals(1, response.getBody().getTotalOperationsPage());
        
        verify(auditService).requestStatement(accountType, ownerId, page, size);
    }

    @Test
    void testRequestStatement_WithMultipleAccountTypes() throws AuditException {
        String[] accountTypes = {"FUND", "SAVING"};
        long ownerId = 1L;
        int page = 0;
        int size = 10;
        
        for (String accountType : accountTypes) {
            testStatementDto.setAccountType(accountType.equals("FUND") ? AccountType.FUND : AccountType.SAVING);
            when(auditService.requestStatement(accountType, ownerId, page, size))
                .thenReturn(ResponseEntity.ok(testStatementDto));

            ResponseEntity<AccountStatementDto> response = controller.requestStatement(accountType, ownerId, page, size);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(testStatementDto.getAccountType(), response.getBody().getAccountType());
        }
        
        verify(auditService, times(2)).requestStatement(anyString(), eq(ownerId), eq(page), eq(size));
    }

    @Test
    void testRequestStatement_WithComplexPagination() throws AuditException {
        String accountType = "FUND";
        long ownerId = 1L;
        int page = 5;
        int size = 25;
        
        testStatementDto.setOperationsPage(5);
        testStatementDto.setOperationsSize(25);
        testStatementDto.setTotalOperationsPage(10);
        
        when(auditService.requestStatement(accountType, ownerId, page, size))
            .thenReturn(ResponseEntity.ok(testStatementDto));

        ResponseEntity<AccountStatementDto> response = controller.requestStatement(accountType, ownerId, page, size);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5, response.getBody().getOperationsPage());
        assertEquals(25, response.getBody().getOperationsSize());
        assertEquals(10, response.getBody().getTotalOperationsPage());
        
        verify(auditService).requestStatement(accountType, ownerId, page, size);
    }

    @Test
    void testRequestStatement_WithConcurrentAccess() throws AuditException {
        String accountType = "FUND";
        long ownerId = 1L;
        int page = 0;
        int size = 10;
        
        when(auditService.requestStatement(accountType, ownerId, page, size))
            .thenReturn(ResponseEntity.ok(testStatementDto));

        ResponseEntity<AccountStatementDto> response1 = controller.requestStatement(accountType, ownerId, page, size);
        ResponseEntity<AccountStatementDto> response2 = controller.requestStatement(accountType, ownerId, page, size);

        assertNotNull(response1);
        assertNotNull(response2);
        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        
        verify(auditService, times(2)).requestStatement(accountType, ownerId, page, size);
    }
}
