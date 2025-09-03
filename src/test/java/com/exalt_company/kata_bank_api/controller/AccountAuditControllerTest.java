package com.exalt_company.kata_bank_api.controller;

import com.exalt_company.kata_bank_api.dto.statement.AccountStatementDto;
import com.exalt_company.kata_bank_api.enums.AccountType;
import com.exalt_company.kata_bank_api.exception.AuditException;
import com.exalt_company.kata_bank_api.service.IAuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    void testRequestStatement_ServiceThrowsException() throws AuditException {
        String accountType = "FUND";
        long ownerId = 999L;
        int page = 0;
        int size = 10;
        
        String errorMessage = "User not found";
        when(auditService.requestStatement(accountType, ownerId, page, size))
            .thenThrow(new AuditException(errorMessage));

        AuditException exception = assertThrows(AuditException.class,
            () -> controller.requestStatement(accountType, ownerId, page, size));
        
        assertEquals(errorMessage, exception.getMessage());
        verify(auditService).requestStatement(accountType, ownerId, page, size);
    }

    @Test
    void testRequestStatement_InvalidAccountType() throws AuditException {
        String accountType = "INVALID";
        long ownerId = 1L;
        int page = 0;
        int size = 10;
        
        String errorMessage = "Invalid account type";
        when(auditService.requestStatement(accountType, ownerId, page, size))
            .thenThrow(new AuditException(errorMessage));

        AuditException exception = assertThrows(AuditException.class,
            () -> controller.requestStatement(accountType, ownerId, page, size));
        
        assertEquals(errorMessage, exception.getMessage());
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

    @Test
    void testRequestStatement_NullAccountType() throws AuditException {
        String accountType = null;
        long ownerId = 1L;
        int page = 0;
        int size = 10;
        
        String errorMessage = "Account type cannot be null";
        when(auditService.requestStatement(accountType, ownerId, page, size))
            .thenThrow(new AuditException(errorMessage));

        AuditException exception = assertThrows(AuditException.class,
            () -> controller.requestStatement(accountType, ownerId, page, size));
        
        assertEquals(errorMessage, exception.getMessage());
        verify(auditService).requestStatement(accountType, ownerId, page, size);
    }

    @Test
    void testRequestStatement_EmptyAccountType() throws AuditException {
        String accountType = "";
        long ownerId = 1L;
        int page = 0;
        int size = 10;
        
        String errorMessage = "Account type cannot be empty";
        when(auditService.requestStatement(accountType, ownerId, page, size))
            .thenThrow(new AuditException(errorMessage));

        AuditException exception = assertThrows(AuditException.class,
            () -> controller.requestStatement(accountType, ownerId, page, size));
        
        assertEquals(errorMessage, exception.getMessage());
        verify(auditService).requestStatement(accountType, ownerId, page, size);
    }

    @Test
    void testRequestStatement_NegativePage() throws AuditException {
        String accountType = "FUND";
        long ownerId = 1L;
        int page = -1;
        int size = 10;
        
        when(auditService.requestStatement(accountType, ownerId, page, size))
            .thenReturn(ResponseEntity.ok(testStatementDto));

        ResponseEntity<AccountStatementDto> response = controller.requestStatement(accountType, ownerId, page, size);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        verify(auditService).requestStatement(accountType, ownerId, page, size);
    }

    @Test
    void testRequestStatement_NegativeSize() throws AuditException {
        String accountType = "FUND";
        long ownerId = 1L;
        int page = 0;
        int size = -5;
        
        when(auditService.requestStatement(accountType, ownerId, page, size))
            .thenReturn(ResponseEntity.ok(testStatementDto));

        ResponseEntity<AccountStatementDto> response = controller.requestStatement(accountType, ownerId, page, size);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        verify(auditService).requestStatement(accountType, ownerId, page, size);
    }
}
