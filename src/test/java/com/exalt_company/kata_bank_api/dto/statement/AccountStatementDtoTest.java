package com.exalt_company.kata_bank_api.dto.statement;

import com.exalt_company.kata_bank_api.enums.AccountType;
import com.exalt_company.kata_bank_api.enums.AuditOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class AccountStatementDtoTest {

    private AccountStatementDto accountStatementDto;
    private List<OperationDto> testOperations;

    @BeforeEach
    void setUp() {
        accountStatementDto = new AccountStatementDto();
        
        testOperations = new ArrayList<>();
        
        OperationDto operation1 = new OperationDto();
        operation1.setOperation(AuditOperation.DEPOSIT);
        operation1.setOperationAuthor("John Doe");
        
        OperationDto operation2 = new OperationDto();
        operation2.setOperation(AuditOperation.WITHDRAW);
        operation2.setOperationAuthor("John Doe");
        
        testOperations.add(operation1);
        testOperations.add(operation2);
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(accountStatementDto);
        assertEquals(0, accountStatementDto.getId());
        assertNull(accountStatementDto.getAccountType());
        assertEquals(0.0, accountStatementDto.getAccountBalance());
        assertNull(accountStatementDto.getOperations());
        assertEquals(0, accountStatementDto.getOperationsPage());
        assertEquals(0, accountStatementDto.getOperationsSize());
        assertEquals(0, accountStatementDto.getTotalOperationsPage());
    }

    @Test
    void testSetAndGetAccountType() {
        AccountType accountType = AccountType.FUND;
        
        accountStatementDto.setAccountType(accountType);
        
        assertEquals(accountType, accountStatementDto.getAccountType());
    }

    @Test
    void testSetAndGetAccountBalance() {
        double balance = 1500.75;
        
        accountStatementDto.setAccountBalance(balance);
        
        assertEquals(balance, accountStatementDto.getAccountBalance());
    }

    @Test
    void testSetAndGetOperations() {
        accountStatementDto.setOperations(testOperations);
        
        assertNotNull(accountStatementDto.getOperations());
        assertEquals(2, accountStatementDto.getOperations().size());
        assertEquals(AuditOperation.DEPOSIT, accountStatementDto.getOperations().get(0).getOperation());
        assertEquals(AuditOperation.WITHDRAW, accountStatementDto.getOperations().get(1).getOperation());
    }

    @Test
    void testSetAndGetOperationsPage() {
        int page = 2;
        
        accountStatementDto.setOperationsPage(page);
        
        assertEquals(page, accountStatementDto.getOperationsPage());
    }

    @Test
    void testSetAndGetOperationsSize() {
        int size = 25;
        
        accountStatementDto.setOperationsSize(size);
        
        assertEquals(size, accountStatementDto.getOperationsSize());
    }

    @Test
    void testSetAndGetTotalOperationsPage() {
        int totalPages = 5;
        
        accountStatementDto.setTotalOperationsPage(totalPages);
        
        assertEquals(totalPages, accountStatementDto.getTotalOperationsPage());
    }

    @Test
    void testInheritedId() {
        accountStatementDto.setId(123L);
        
        assertEquals(123L, accountStatementDto.getId());
    }

    @Test
    void testAllFieldsTogether() {
        AccountType accountType = AccountType.SAVING;
        double balance = 2500.50;
        int page = 1;
        int size = 15;
        int totalPages = 3;
        
        accountStatementDto.setAccountType(accountType);
        accountStatementDto.setAccountBalance(balance);
        accountStatementDto.setOperations(testOperations);
        accountStatementDto.setOperationsPage(page);
        accountStatementDto.setOperationsSize(size);
        accountStatementDto.setTotalOperationsPage(totalPages);
        
        assertEquals(accountType, accountStatementDto.getAccountType());
        assertEquals(balance, accountStatementDto.getAccountBalance());
        assertEquals(testOperations, accountStatementDto.getOperations());
        assertEquals(page, accountStatementDto.getOperationsPage());
        assertEquals(size, accountStatementDto.getOperationsSize());
        assertEquals(totalPages, accountStatementDto.getTotalOperationsPage());
    }

    @Test
    void testNullValues() {
        accountStatementDto.setAccountType(null);
        accountStatementDto.setOperations(null);
        
        assertNull(accountStatementDto.getAccountType());
        assertNull(accountStatementDto.getOperations());
    }

    @Test
    void testEmptyOperationsList() {
        List<OperationDto> emptyOperations = new ArrayList<>();
        
        accountStatementDto.setOperations(emptyOperations);
        
        assertNotNull(accountStatementDto.getOperations());
        assertEquals(0, accountStatementDto.getOperations().size());
    }

    @Test
    void testDecimalPrecision() {
        double preciseBalance = 1234.56789;
        
        accountStatementDto.setAccountBalance(preciseBalance);
        
        assertEquals(preciseBalance, accountStatementDto.getAccountBalance());
    }

    @Test
    void testNegativeValues() {
        double negativeBalance = -500.0;
        int negativePage = -1;
        int negativeSize = -10;
        int negativeTotalPages = -5;
        
        accountStatementDto.setAccountBalance(negativeBalance);
        accountStatementDto.setOperationsPage(negativePage);
        accountStatementDto.setOperationsSize(negativeSize);
        accountStatementDto.setTotalOperationsPage(negativeTotalPages);
        
        assertEquals(negativeBalance, accountStatementDto.getAccountBalance());
        assertEquals(negativePage, accountStatementDto.getOperationsPage());
        assertEquals(negativeSize, accountStatementDto.getOperationsSize());
        assertEquals(negativeTotalPages, accountStatementDto.getTotalOperationsPage());
    }

    @Test
    void testZeroValues() {
        double zeroBalance = 0.0;
        int zeroPage = 0;
        int zeroSize = 0;
        int zeroTotalPages = 0;
        
        accountStatementDto.setAccountBalance(zeroBalance);
        accountStatementDto.setOperationsPage(zeroPage);
        accountStatementDto.setOperationsSize(zeroSize);
        accountStatementDto.setTotalOperationsPage(zeroTotalPages);
        
        assertEquals(zeroBalance, accountStatementDto.getAccountBalance());
        assertEquals(zeroPage, accountStatementDto.getOperationsPage());
        assertEquals(zeroSize, accountStatementDto.getOperationsSize());
        assertEquals(zeroTotalPages, accountStatementDto.getTotalOperationsPage());
    }

    @Test
    void testLargeValues() {
        double largeBalance = 999999999.99;
        int largePage = 999999;
        int largeSize = 999999;
        int largeTotalPages = 999999;
        
        accountStatementDto.setAccountBalance(largeBalance);
        accountStatementDto.setOperationsPage(largePage);
        accountStatementDto.setOperationsSize(largeSize);
        accountStatementDto.setTotalOperationsPage(largeTotalPages);
        
        assertEquals(largeBalance, accountStatementDto.getAccountBalance());
        assertEquals(largePage, accountStatementDto.getOperationsPage());
        assertEquals(largeSize, accountStatementDto.getOperationsSize());
        assertEquals(largeTotalPages, accountStatementDto.getTotalOperationsPage());
    }

    @Test
    void testOperationsModification() {
        accountStatementDto.setOperations(testOperations);
        
        OperationDto newOperation = new OperationDto();
        newOperation.setOperation(AuditOperation.OPEN);
        newOperation.setOperationAuthor("Jane Smith");
        
        accountStatementDto.getOperations().add(newOperation);
        
        assertEquals(3, accountStatementDto.getOperations().size());
        assertEquals(AuditOperation.OPEN, accountStatementDto.getOperations().get(2).getOperation());
        assertEquals("Jane Smith", accountStatementDto.getOperations().get(2).getOperationAuthor());
    }

    @Test
    void testMultipleAccountTypes() {
        accountStatementDto.setAccountType(AccountType.FUND);
        assertEquals(AccountType.FUND, accountStatementDto.getAccountType());
        
        accountStatementDto.setAccountType(AccountType.SAVING);
        assertEquals(AccountType.SAVING, accountStatementDto.getAccountType());
        
        accountStatementDto.setAccountType(AccountType.FUND);
        assertEquals(AccountType.FUND, accountStatementDto.getAccountType());
    }
}
