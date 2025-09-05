package com.exalt_company.kata_bank_api.controller;

import com.exalt_company.kata_bank_api.dto.BankUserDto;
import com.exalt_company.kata_bank_api.dto.PageDto;
import com.exalt_company.kata_bank_api.enums.BankRole;
import com.exalt_company.kata_bank_api.exception.BaseException;
import com.exalt_company.kata_bank_api.service.IBankUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankUserControllerTest {

    @Mock
    private IBankUserService bankUserService;

    @InjectMocks
    private BankUserController bankUserController;

    private BankUserDto testBankUser;
    private List<BankUserDto> testBankUsers;
    private PageDto<BankUserDto> testPageDto;

    @BeforeEach
    void setUp() {
        testBankUser = new BankUserDto();
        testBankUser.setId(1L);
        testBankUser.setName("John");
        testBankUser.setSurname("Doe");
        testBankUser.setEmail("john.doe@example.com");
        testBankUser.setBankRole(BankRole.CLIENT);
        testBankUser.setAdvisorId(2L);

        BankUserDto secondUser = new BankUserDto();
        secondUser.setId(2L);
        secondUser.setName("Jane");
        secondUser.setSurname("Smith");
        secondUser.setEmail("jane.smith@example.com");
        secondUser.setBankRole(BankRole.ADVISOR);
        secondUser.setAdvisorId(0L);

        testBankUsers = Arrays.asList(testBankUser, secondUser);

        testPageDto = new PageDto<>();
        testPageDto.setContent(testBankUsers);
        testPageDto.setTotalElements(2L);
        testPageDto.setTotalPages(1);
        testPageDto.setNumber(0);
        testPageDto.setSize(10);
    }

    @Test
    void testCreate_Success() throws BaseException {
        when(bankUserService.create(any(BankUserDto.class)))
                .thenReturn(ResponseEntity.ok(testBankUser));

        ResponseEntity<BankUserDto> response = bankUserController.create(testBankUser);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testBankUser.getId(), response.getBody().getId());
        assertEquals(testBankUser.getName(), response.getBody().getName());
        assertEquals(testBankUser.getSurname(), response.getBody().getSurname());
        assertEquals(testBankUser.getEmail(), response.getBody().getEmail());
        assertEquals(testBankUser.getBankRole(), response.getBody().getBankRole());
        assertEquals(testBankUser.getAdvisorId(), response.getBody().getAdvisorId());

        verify(bankUserService, times(1)).create(testBankUser);
    }

    @Test
    void testCreate_WithValidData() throws BaseException {
        BankUserDto newUser = new BankUserDto();
        newUser.setName("Alice");
        newUser.setSurname("Johnson");
        newUser.setEmail("alice.johnson@example.com");
        newUser.setBankRole(BankRole.ADMIN);
        newUser.setAdvisorId(0L);

        BankUserDto createdUser = new BankUserDto();
        createdUser.setId(3L);
        createdUser.setName("Alice");
        createdUser.setSurname("Johnson");
        createdUser.setEmail("alice.johnson@example.com");
        createdUser.setBankRole(BankRole.ADMIN);
        createdUser.setAdvisorId(0L);

        when(bankUserService.create(any(BankUserDto.class)))
                .thenReturn(ResponseEntity.ok(createdUser));

        ResponseEntity<BankUserDto> response = bankUserController.create(newUser);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3L, response.getBody().getId());
        assertEquals("Alice", response.getBody().getName());
        assertEquals("Johnson", response.getBody().getSurname());
        assertEquals("alice.johnson@example.com", response.getBody().getEmail());
        assertEquals(BankRole.ADMIN, response.getBody().getBankRole());

        verify(bankUserService, times(1)).create(newUser);
    }

    @Test
    void testCreate_ServiceThrowsException() throws BaseException {
        String errorMessage = "User already exists";
        when(bankUserService.create(any(BankUserDto.class)))
                .thenThrow(new BaseException(errorMessage));

        BaseException exception = assertThrows(BaseException.class,
                () -> bankUserController.create(testBankUser));

        assertEquals(errorMessage, exception.getMessage());
        verify(bankUserService, times(1)).create(testBankUser);
    }

    @Test
    void testGetById_Success() throws BaseException {
        long userId = 1L;
        when(bankUserService.getById(userId))
                .thenReturn(ResponseEntity.ok(testBankUser));

        ResponseEntity<BankUserDto> response = bankUserController.getById(userId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testBankUser.getId(), response.getBody().getId());
        assertEquals(testBankUser.getName(), response.getBody().getName());

        verify(bankUserService, times(1)).getById(userId);
    }

    @Test
    void testGetById_UserNotFound() throws BaseException {
        long userId = 999L;
        String errorMessage = "User not found";
        when(bankUserService.getById(userId))
                .thenThrow(new BaseException(errorMessage));

        BaseException exception = assertThrows(BaseException.class,
                () -> bankUserController.getById(userId));

        assertEquals(errorMessage, exception.getMessage());
        verify(bankUserService, times(1)).getById(userId);
    }

    @Test
    void testGetById_WithZeroId() throws BaseException {
        long userId = 0L;
        String errorMessage = "Invalid user ID";
        when(bankUserService.getById(userId))
                .thenThrow(new BaseException(errorMessage));

        BaseException exception = assertThrows(BaseException.class,
                () -> bankUserController.getById(userId));

        assertEquals(errorMessage, exception.getMessage());
        verify(bankUserService, times(1)).getById(userId);
    }

    @Test
    void testGetById_WithNegativeId() throws BaseException {
        long userId = -1L;
        String errorMessage = "Invalid user ID";
        when(bankUserService.getById(userId))
                .thenThrow(new BaseException(errorMessage));

        BaseException exception = assertThrows(BaseException.class,
                () -> bankUserController.getById(userId));

        assertEquals(errorMessage, exception.getMessage());
        verify(bankUserService, times(1)).getById(userId);
    }

    @Test
    void testGetAll_Success() {
        when(bankUserService.getAll())
                .thenReturn(ResponseEntity.ok(testBankUsers));

        ResponseEntity<List<BankUserDto>> response = bankUserController.getAll();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(testBankUser.getId(), response.getBody().get(0).getId());
        assertEquals("Jane", response.getBody().get(1).getName());

        verify(bankUserService, times(1)).getAll();
    }

    @Test
    void testGetAll_EmptyList() {
        List<BankUserDto> emptyList = List.of();
        when(bankUserService.getAll())
                .thenReturn(ResponseEntity.ok(emptyList));

        ResponseEntity<List<BankUserDto>> response = bankUserController.getAll();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(bankUserService, times(1)).getAll();
    }

    @Test
    void testGetPage_Success() {
        int page = 0;
        int size = 10;
        when(bankUserService.getPage(page, size))
                .thenReturn(ResponseEntity.ok(testPageDto));

        ResponseEntity<PageDto<BankUserDto>> response = bankUserController.getPage(page, size);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2L, response.getBody().getTotalElements());
        assertEquals(1, response.getBody().getTotalPages());
        assertEquals(0, response.getBody().getNumber());
        assertEquals(10, response.getBody().getSize());
        assertEquals(2, response.getBody().getContent().size());

        verify(bankUserService, times(1)).getPage(page, size);
    }

    @Test
    void testGetPage_WithDifferentPagination() {
        int page = 1;
        int size = 5;
        PageDto<BankUserDto> customPageDto = new PageDto<>();
        customPageDto.setContent(Collections.singletonList(testBankUser));
        customPageDto.setTotalElements(1L);
        customPageDto.setTotalPages(2);
        customPageDto.setNumber(1);
        customPageDto.setSize(5);

        when(bankUserService.getPage(page, size))
                .thenReturn(ResponseEntity.ok(customPageDto));

        ResponseEntity<PageDto<BankUserDto>> response = bankUserController.getPage(page, size);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getTotalElements());
        assertEquals(2, response.getBody().getTotalPages());
        assertEquals(1, response.getBody().getNumber());
        assertEquals(5, response.getBody().getSize());

        verify(bankUserService, times(1)).getPage(page, size);
    }

    @Test
    void testGetPage_WithNegativePage() {
        int page = -1;
        int size = 10;
        when(bankUserService.getPage(page, size))
                .thenReturn(ResponseEntity.ok(testPageDto));

        ResponseEntity<PageDto<BankUserDto>> response = bankUserController.getPage(page, size);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(bankUserService, times(1)).getPage(page, size);
    }

    @Test
    void testGetPage_WithZeroSize() {
        int page = 0;
        int size = 0;
        when(bankUserService.getPage(page, size))
                .thenReturn(ResponseEntity.ok(testPageDto));

        ResponseEntity<PageDto<BankUserDto>> response = bankUserController.getPage(page, size);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(bankUserService, times(1)).getPage(page, size);
    }

    @Test
    void testUpdate_Success() throws BaseException {
        long userId = 1L;
        BankUserDto updatedUser = new BankUserDto();
        updatedUser.setId(userId);
        updatedUser.setName("John Updated");
        updatedUser.setSurname("Doe Updated");
        updatedUser.setEmail("john.updated@example.com");
        updatedUser.setBankRole(BankRole.ADVISOR);
        updatedUser.setAdvisorId(3L);

        when(bankUserService.update(eq(userId), any(BankUserDto.class)))
                .thenReturn(ResponseEntity.ok(updatedUser));

        ResponseEntity<BankUserDto> response = bankUserController.update(userId, updatedUser);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userId, response.getBody().getId());
        assertEquals("John Updated", response.getBody().getName());
        assertEquals("Doe Updated", response.getBody().getSurname());
        assertEquals("john.updated@example.com", response.getBody().getEmail());
        assertEquals(BankRole.ADVISOR, response.getBody().getBankRole());
        assertEquals(3L, response.getBody().getAdvisorId());

        verify(bankUserService, times(1)).update(userId, updatedUser);
    }

    @Test
    void testUpdate_UserNotFound() throws BaseException {
        long userId = 999L;
        String errorMessage = "User not found";
        when(bankUserService.update(eq(userId), any(BankUserDto.class)))
                .thenThrow(new BaseException(errorMessage));

        BaseException exception = assertThrows(BaseException.class,
                () -> bankUserController.update(userId, testBankUser));

        assertEquals(errorMessage, exception.getMessage());
        verify(bankUserService, times(1)).update(userId, testBankUser);
    }

    @Test
    void testDeleteById_Success() throws BaseException {
        long userId = 1L;
        when(bankUserService.deleteById(userId))
                .thenReturn(ResponseEntity.ok(true));

        ResponseEntity<Boolean> response = bankUserController.deleteById(userId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());

        verify(bankUserService, times(1)).deleteById(userId);
    }

    @Test
    void testDeleteById_UserNotFound() throws BaseException {
        long userId = 999L;
        String errorMessage = "User not found";
        when(bankUserService.deleteById(userId))
                .thenThrow(new BaseException(errorMessage));

        BaseException exception = assertThrows(BaseException.class,
                () -> bankUserController.deleteById(userId));

        assertEquals(errorMessage, exception.getMessage());
        verify(bankUserService, times(1)).deleteById(userId);
    }

    @Test
    void testDelete_Success() throws BaseException {
        when(bankUserService.delete(any(BankUserDto.class)))
                .thenReturn(ResponseEntity.ok(true));

        ResponseEntity<Boolean> response = bankUserController.delete(testBankUser);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());

        verify(bankUserService, times(1)).delete(testBankUser);
    }

    @Test
    void testDelete_UserNotFound() throws BaseException {
        String errorMessage = "User not found";
        when(bankUserService.delete(any(BankUserDto.class)))
                .thenThrow(new BaseException(errorMessage));

        BaseException exception = assertThrows(BaseException.class,
                () -> bankUserController.delete(testBankUser));

        assertEquals(errorMessage, exception.getMessage());
        verify(bankUserService, times(1)).delete(testBankUser);
    }

    @Test
    void testCreate_WithNullName() throws BaseException {
        testBankUser.setName(null);
        String errorMessage = "Name cannot be null";
        when(bankUserService.create(any(BankUserDto.class)))
                .thenThrow(new BaseException(errorMessage));

        BaseException exception = assertThrows(BaseException.class,
                () -> bankUserController.create(testBankUser));

        assertEquals(errorMessage, exception.getMessage());
        verify(bankUserService, times(1)).create(testBankUser);
    }

    @Test
    void testCreate_WithEmptyEmail() throws BaseException {
        testBankUser.setEmail("");
        String errorMessage = "Email cannot be empty";
        when(bankUserService.create(any(BankUserDto.class)))
                .thenThrow(new BaseException(errorMessage));

        BaseException exception = assertThrows(BaseException.class,
                () -> bankUserController.create(testBankUser));

        assertEquals(errorMessage, exception.getMessage());
        verify(bankUserService, times(1)).create(testBankUser);
    }

    @Test
    void testCreate_WithNullBankRole() throws BaseException {
        testBankUser.setBankRole(null);
        String errorMessage = "Bank role cannot be null";
        when(bankUserService.create(any(BankUserDto.class)))
                .thenThrow(new BaseException(errorMessage));

        BaseException exception = assertThrows(BaseException.class,
                () -> bankUserController.create(testBankUser));

        assertEquals(errorMessage, exception.getMessage());
        verify(bankUserService, times(1)).create(testBankUser);
    }
}
