package com.exalt_company.kata_bank_api.service;

import com.exalt_company.kata_bank_api.dto.BankUserDto;
import com.exalt_company.kata_bank_api.dto.PasswordedBankUserDto;
import com.exalt_company.kata_bank_api.entity.BankUser;
import com.exalt_company.kata_bank_api.entity.user_fields.Credentials;
import com.exalt_company.kata_bank_api.entity.user_fields.Identity;
import com.exalt_company.kata_bank_api.enums.BankRole;
import com.exalt_company.kata_bank_api.exception.BaseException;
import com.exalt_company.kata_bank_api.mapper.BankUserMapper;
import com.exalt_company.kata_bank_api.repository.BankUserRepository;
import com.exalt_company.kata_bank_api.service.impl.BankUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankUserServiceTest {
    @Mock
    private BankUserMapper mapper;

    @Mock
    private BankUserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private BankUserService bankUserService;

    private BankUserDto bankUserDto;
    private BankUser bankUser;
    private PasswordedBankUserDto passwordedBankUserDto;

    @BeforeEach
    void setUp() {
        bankUserDto = new BankUserDto();
        bankUserDto.setId(1L);
        bankUserDto.setEmail("test@example.com");
        bankUserDto.setBankRole(BankRole.CLIENT);

        Identity identity = new Identity();
        identity.setName("John");
        identity.setSurname("Doe");

        bankUser = new BankUser();
        bankUser.setId(1L);
        bankUser.setBankRole(BankRole.CLIENT);

        Credentials credentials = new Credentials();
        credentials.setEmail("test@example.com");
        credentials.setPassword("temporary123");
        bankUser.setCredentials(credentials);
        bankUser.setIdentity(identity);

        passwordedBankUserDto = new PasswordedBankUserDto();
        passwordedBankUserDto.setId(1L);
        passwordedBankUserDto.setEmail("test@example.com");
        passwordedBankUserDto.setBankRole(BankRole.CLIENT);
        passwordedBankUserDto.setPassword("temporary123");
    }

    @Test
    void testCreate_Success() throws BaseException {
        when(mapper.toEntity(any(PasswordedBankUserDto.class))).thenReturn(bankUser);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(repository.save(any(BankUser.class))).thenReturn(bankUser);
        when(mapper.toCreatedDto(any(BankUser.class))).thenReturn(passwordedBankUserDto);

        ResponseEntity<BankUserDto> response = bankUserService.create(bankUserDto);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("temporary123", ((PasswordedBankUserDto) response.getBody()).getPassword());

        verify(mapper).toEntity(argThat(dto -> 
            dto != null &&
            "temporary123".equals(dto.getPassword())
        ));
        verify(passwordEncoder).encode("temporary123");
        verify(repository).save(any(BankUser.class));
        verify(mapper).toCreatedDto(any(BankUser.class));
    }

    @Test
    void testCreate_WithPasswordEncoding() throws BaseException {
        when(mapper.toEntity(any(PasswordedBankUserDto.class))).thenReturn(bankUser);
        when(passwordEncoder.encode("temporary123")).thenReturn("encodedPassword");
        when(repository.save(any(BankUser.class))).thenReturn(bankUser);
        when(mapper.toCreatedDto(any(BankUser.class))).thenReturn(passwordedBankUserDto);

        ResponseEntity<BankUserDto> response = bankUserService.create(bankUserDto);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        verify(repository).save(argThat(user ->
            "encodedPassword".equals(user.getCredentials().getPassword())
        ));
    }

    @Test
    void testGetById_Success() throws BaseException {
        when(repository.findById(1L)).thenReturn(Optional.of(bankUser));
        when(mapper.toDto(any(BankUser.class))).thenReturn(bankUserDto);

        ResponseEntity<BankUserDto> response = bankUserService.getById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());

        verify(repository).findById(1L);
        verify(mapper).toDto(bankUser);
    }

    @Test
    void testGetById_NotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        BaseException exception = assertThrows(BaseException.class, () -> 
            bankUserService.getById(999L)
        );

        assertEquals("BankUser not found", exception.getMessage());
        verify(repository).findById(999L);
        verify(mapper, never()).toDto(any(BankUser.class));
    }

    @Test
    void testGetAll_Success() {
        List<BankUser> users = List.of(bankUser);
        List<BankUserDto> userDtos = List.of(bankUserDto);

        when(repository.findAll()).thenReturn(users);
        when(mapper.toDtos(users)).thenReturn(userDtos);

        ResponseEntity<List<BankUserDto>> response = bankUserService.getAll();

        assertNotNull(response);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());

        verify(repository).findAll();
        verify(mapper).toDtos(users);
    }

    @Test
    void testGetAll_EmptyList() {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        when(mapper.toDtos(Collections.emptyList())).thenReturn(Collections.emptyList());

        ResponseEntity<List<BankUserDto>> response = bankUserService.getAll();

        assertNotNull(response);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());

        verify(repository).findAll();
        verify(mapper).toDtos(Collections.emptyList());
    }

    @Test
    void testGetPage_Success() {
        Page<BankUser> userPage = new PageImpl<>(List.of(bankUser), PageRequest.of(0, 10), 1);
        List<BankUserDto> userDtos = List.of(bankUserDto);

        when(repository.findAll(PageRequest.of(0, 10))).thenReturn(userPage);
        when(mapper.toDtos(List.of(bankUser))).thenReturn(userDtos);

        ResponseEntity<com.exalt_company.kata_bank_api.dto.PageDto<BankUserDto>> response = 
            bankUserService.getPage(0, 10);

        assertNotNull(response);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals(0, response.getBody().getNumber());
        assertEquals(10, response.getBody().getSize());
        assertEquals(1, response.getBody().getTotalPages());
        assertEquals(1, response.getBody().getTotalElements());

        verify(repository).findAll(PageRequest.of(0, 10));
        verify(mapper).toDtos(List.of(bankUser));
    }

    @Test
    void testUpdate_Success() throws BaseException {
        BankUserDto updatedDto = new BankUserDto();
        updatedDto.setId(1L);
        updatedDto.setEmail("updated@example.com");
        updatedDto.setBankRole(BankRole.ADMIN);

        when(repository.findById(1L)).thenReturn(Optional.of(bankUser));
        when(repository.save(any(BankUser.class))).thenReturn(bankUser);
        when(mapper.toDto(any(BankUser.class))).thenReturn(updatedDto);

        ResponseEntity<BankUserDto> response = bankUserService.update(1L, updatedDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("updated@example.com", response.getBody().getEmail());

        verify(repository).findById(1L);
        verify(mapper).updateEntityFromDto(updatedDto, bankUser);
        verify(repository).save(bankUser);
        verify(mapper).toDto(bankUser);
    }

    @Test
    void testUpdate_NotFound() {
        BankUserDto updatedDto = new BankUserDto();
        updatedDto.setId(1L);

        when(repository.findById(999L)).thenReturn(Optional.empty());

        BaseException exception = assertThrows(BaseException.class, () -> 
            bankUserService.update(999L, updatedDto)
        );

        assertEquals("Could not update BankUser: Please create first", exception.getMessage());
        verify(repository).findById(999L);
        verify(repository, never()).save(any(BankUser.class));
    }

    @Test
    void testDeleteById_Success() throws BaseException {
        when(repository.findById(1L)).thenReturn(Optional.of(bankUser));

        ResponseEntity<Boolean> response = bankUserService.deleteById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody());

        verify(repository).findById(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void testDeleteById_NotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        BaseException exception = assertThrows(BaseException.class, () -> 
            bankUserService.deleteById(999L)
        );

        assertEquals("Could not delete BankUser: not found", exception.getMessage());
        verify(repository).findById(999L);
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void testUpdate_WithNullDto() {
        BaseException exception = assertThrows(BaseException.class, () ->
            bankUserService.update(1L, null)
        );

        assertEquals("Could not update BankUser: Nothing to update", exception.getMessage());
        verify(repository, never()).findById(anyLong());
        verify(mapper, never()).updateEntityFromDto(any(), any());
        verify(repository, never()).save(any(BankUser.class));
    }

    @Test
    void testCreate_WithNullDto() {
        BaseException exception = assertThrows(BaseException.class, () -> 
            bankUserService.create(null)
        );

        assertEquals("Could not create BankUser: Nothing to create", exception.getMessage());
        verify(mapper, never()).toEntity(any());
        verify(passwordEncoder, never()).encode(anyString());
        verify(repository, never()).save(any(BankUser.class));
    }

    @Test
    void testCreate_TemporaryPasswordGeneration() throws BaseException {
        when(mapper.toEntity(any(PasswordedBankUserDto.class))).thenReturn(bankUser);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(repository.save(any(BankUser.class))).thenReturn(bankUser);
        when(mapper.toCreatedDto(any(BankUser.class))).thenReturn(passwordedBankUserDto);

        ResponseEntity<BankUserDto> response = bankUserService.create(bankUserDto);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        
        PasswordedBankUserDto result = (PasswordedBankUserDto) response.getBody();
        assertEquals("temporary123", result.getPassword());
    }
}
