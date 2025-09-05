package com.exalt_company.kata_bank_api.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class PageDtoTest {
    private PageDto<BankUserDto> pageDto;
    private List<BankUserDto> testContent;

    @BeforeEach
    void setUp() {
        pageDto = new PageDto<>();
        testContent = new ArrayList<>();
        
        BankUserDto user1 = new BankUserDto();
        user1.setId(1L);
        user1.setName("John");
        user1.setSurname("Doe");
        
        BankUserDto user2 = new BankUserDto();
        user2.setId(2L);
        user2.setName("Jane");
        user2.setSurname("Smith");
        
        testContent.add(user1);
        testContent.add(user2);
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(pageDto);
        assertNull(pageDto.getContent());
        assertEquals(0, pageDto.getNumber());
        assertEquals(0, pageDto.getSize());
        assertEquals(0, pageDto.getTotalPages());
        assertEquals(0L, pageDto.getTotalElements());
        assertEquals(0, pageDto.getNumberOfElements());
    }

    @Test
    void testSetAndGetContent() {
        pageDto.setContent(testContent);
        
        assertNotNull(pageDto.getContent());
        assertEquals(2, pageDto.getContent().size());
        assertEquals("John", pageDto.getContent().get(0).getName());
        assertEquals("Jane", pageDto.getContent().get(1).getName());
    }

    @Test
    void testSetAndGetNumber() {
        int pageNumber = 2;
        
        pageDto.setNumber(pageNumber);
        
        assertEquals(pageNumber, pageDto.getNumber());
    }

    @Test
    void testSetAndGetSize() {
        int pageSize = 20;
        
        pageDto.setSize(pageSize);
        
        assertEquals(pageSize, pageDto.getSize());
    }

    @Test
    void testSetAndGetTotalPages() {
        int totalPages = 5;
        
        pageDto.setTotalPages(totalPages);
        
        assertEquals(totalPages, pageDto.getTotalPages());
    }

    @Test
    void testSetAndGetTotalElements() {
        long totalElements = 100L;
        
        pageDto.setTotalElements(totalElements);
        
        assertEquals(totalElements, pageDto.getTotalElements());
    }

    @Test
    void testSetAndGetNumberOfElements() {
        int numberOfElements = 10;
        
        pageDto.setNumberOfElements(numberOfElements);
        
        assertEquals(numberOfElements, pageDto.getNumberOfElements());
    }

    @Test
    void testAllFieldsTogether() {
        pageDto.setContent(testContent);
        pageDto.setNumber(1);
        pageDto.setSize(10);
        pageDto.setTotalPages(3);
        pageDto.setTotalElements(25L);
        pageDto.setNumberOfElements(10);
        
        assertNotNull(pageDto.getContent());
        assertEquals(2, pageDto.getContent().size());
        assertEquals(1, pageDto.getNumber());
        assertEquals(10, pageDto.getSize());
        assertEquals(3, pageDto.getTotalPages());
        assertEquals(25L, pageDto.getTotalElements());
        assertEquals(10, pageDto.getNumberOfElements());
    }

    @Test
    void testNullContent() {
        pageDto.setContent(null);
        
        assertNull(pageDto.getContent());
    }

    @Test
    void testEmptyContent() {
        List<BankUserDto> emptyContent = new ArrayList<>();
        
        pageDto.setContent(emptyContent);
        
        assertNotNull(pageDto.getContent());
        assertEquals(0, pageDto.getContent().size());
    }

    @Test
    void testNegativeValues() {
        pageDto.setNumber(-1);
        pageDto.setSize(-10);
        pageDto.setTotalPages(-5);
        pageDto.setTotalElements(-100L);
        pageDto.setNumberOfElements(-10);
        
        assertEquals(-1, pageDto.getNumber());
        assertEquals(-10, pageDto.getSize());
        assertEquals(-5, pageDto.getTotalPages());
        assertEquals(-100L, pageDto.getTotalElements());
        assertEquals(-10, pageDto.getNumberOfElements());
    }

    @Test
    void testZeroValues() {
        pageDto.setNumber(0);
        pageDto.setSize(0);
        pageDto.setTotalPages(0);
        pageDto.setTotalElements(0L);
        pageDto.setNumberOfElements(0);
        
        assertEquals(0, pageDto.getNumber());
        assertEquals(0, pageDto.getSize());
        assertEquals(0, pageDto.getTotalPages());
        assertEquals(0L, pageDto.getTotalElements());
        assertEquals(0, pageDto.getNumberOfElements());
    }

    @Test
    void testLargeValues() {
        pageDto.setNumber(999999);
        pageDto.setSize(999999);
        pageDto.setTotalPages(999999);
        pageDto.setTotalElements(999999L);
        pageDto.setNumberOfElements(999999);
        
        assertEquals(999999, pageDto.getNumber());
        assertEquals(999999, pageDto.getSize());
        assertEquals(999999, pageDto.getTotalPages());
        assertEquals(999999L, pageDto.getTotalElements());
        assertEquals(999999, pageDto.getNumberOfElements());
    }

    @Test
    void testMaxIntegerValues() {
        pageDto.setNumber(Integer.MAX_VALUE);
        pageDto.setSize(Integer.MAX_VALUE);
        pageDto.setTotalPages(Integer.MAX_VALUE);
        pageDto.setTotalElements(Long.MAX_VALUE);
        pageDto.setNumberOfElements(Integer.MAX_VALUE);
        
        assertEquals(Integer.MAX_VALUE, pageDto.getNumber());
        assertEquals(Integer.MAX_VALUE, pageDto.getSize());
        assertEquals(Integer.MAX_VALUE, pageDto.getTotalPages());
        assertEquals(Long.MAX_VALUE, pageDto.getTotalElements());
        assertEquals(Integer.MAX_VALUE, pageDto.getNumberOfElements());
    }

    @Test
    void testMinIntegerValues() {
        pageDto.setNumber(Integer.MIN_VALUE);
        pageDto.setSize(Integer.MIN_VALUE);
        pageDto.setTotalPages(Integer.MIN_VALUE);
        pageDto.setTotalElements(Long.MIN_VALUE);
        pageDto.setNumberOfElements(Integer.MIN_VALUE);
        
        assertEquals(Integer.MIN_VALUE, pageDto.getNumber());
        assertEquals(Integer.MIN_VALUE, pageDto.getSize());
        assertEquals(Integer.MIN_VALUE, pageDto.getTotalPages());
        assertEquals(Long.MIN_VALUE, pageDto.getTotalElements());
        assertEquals(Integer.MIN_VALUE, pageDto.getNumberOfElements());
    }

    @Test
    void testContentModification() {
        pageDto.setContent(testContent);
        
        BankUserDto newUser = new BankUserDto();
        newUser.setId(3L);
        newUser.setName("Bob");
        newUser.setSurname("Johnson");
        
        pageDto.getContent().add(newUser);
        
        assertEquals(3, pageDto.getContent().size());
        assertEquals("Bob", pageDto.getContent().get(2).getName());
    }

    @Test
    void testMultipleUpdates() {
        pageDto.setNumber(0);
        pageDto.setSize(10);
        pageDto.setTotalPages(1);
        
        pageDto.setNumber(1);
        pageDto.setSize(20);
        pageDto.setTotalPages(2);
        
        assertEquals(1, pageDto.getNumber());
        assertEquals(20, pageDto.getSize());
        assertEquals(2, pageDto.getTotalPages());
    }

    @Test
    void testRealisticPaginationScenario() {
        pageDto.setContent(testContent);
        pageDto.setNumber(0);
        pageDto.setSize(10);
        pageDto.setTotalPages(5);
        pageDto.setTotalElements(50L);
        pageDto.setNumberOfElements(10);
        
        assertNotNull(pageDto.getContent());
        assertEquals(2, pageDto.getContent().size());
        assertEquals(0, pageDto.getNumber());
        assertEquals(10, pageDto.getSize());
        assertEquals(5, pageDto.getTotalPages());
        assertEquals(50L, pageDto.getTotalElements());
        assertEquals(10, pageDto.getNumberOfElements());
    }

    @Test
    void testLastPageScenario() {
        List<BankUserDto> lastPageContent = new ArrayList<>();
        BankUserDto lastUser = new BankUserDto();
        lastUser.setId(50L);
        lastUser.setName("Last");
        lastUser.setSurname("User");
        lastPageContent.add(lastUser);
        
        pageDto.setContent(lastPageContent);
        pageDto.setNumber(4);
        pageDto.setSize(10);
        pageDto.setTotalPages(5);
        pageDto.setTotalElements(50L);
        pageDto.setNumberOfElements(1);
        
        assertNotNull(pageDto.getContent());
        assertEquals(1, pageDto.getContent().size());
        assertEquals(4, pageDto.getNumber());
        assertEquals(10, pageDto.getSize());
        assertEquals(5, pageDto.getTotalPages());
        assertEquals(50L, pageDto.getTotalElements());
        assertEquals(1, pageDto.getNumberOfElements());
    }

    @Test
    void testSinglePageScenario() {
        pageDto.setContent(testContent);
        pageDto.setNumber(0);
        pageDto.setSize(10);
        pageDto.setTotalPages(1);
        pageDto.setTotalElements(2L);
        pageDto.setNumberOfElements(2);
        
        assertNotNull(pageDto.getContent());
        assertEquals(2, pageDto.getContent().size());
        assertEquals(0, pageDto.getNumber());
        assertEquals(10, pageDto.getSize());
        assertEquals(1, pageDto.getTotalPages());
        assertEquals(2L, pageDto.getTotalElements());
        assertEquals(2, pageDto.getNumberOfElements());
    }

    @Test
    void testGenericTypeWithDifferentDto() {
        PageDto<SavingDto> savingPageDto = new PageDto<>();
        List<SavingDto> savingContent = new ArrayList<>();
        
        SavingDto saving1 = new SavingDto();
        saving1.setId(1L);
        saving1.setBalance(1000.0);
        saving1.setMaxBalance(10000.0);
        saving1.setOwnerId(100L);
        
        savingContent.add(saving1);
        
        savingPageDto.setContent(savingContent);
        savingPageDto.setNumber(0);
        savingPageDto.setSize(5);
        savingPageDto.setTotalPages(1);
        savingPageDto.setTotalElements(1L);
        savingPageDto.setNumberOfElements(1);
        
        assertNotNull(savingPageDto.getContent());
        assertEquals(1, savingPageDto.getContent().size());
        assertEquals(1000.0, savingPageDto.getContent().get(0).getBalance(), 0.001);
        assertEquals(0, savingPageDto.getNumber());
        assertEquals(5, savingPageDto.getSize());
        assertEquals(1, savingPageDto.getTotalPages());
        assertEquals(1L, savingPageDto.getTotalElements());
        assertEquals(1, savingPageDto.getNumberOfElements());
    }
}
