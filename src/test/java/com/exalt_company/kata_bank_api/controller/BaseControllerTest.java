package com.exalt_company.kata_bank_api.controller;

import com.exalt_company.kata_bank_api.dto.BaseDto;
import com.exalt_company.kata_bank_api.dto.PageDto;
import com.exalt_company.kata_bank_api.exception.BaseException;
import com.exalt_company.kata_bank_api.service.IBaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
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
class BaseControllerTest {

    @Mock
    private IBaseService<TestDto> baseService;

    private TestBaseController testBaseController;
    private TestDto testDto;
    private List<TestDto> testDtos;
    private PageDto<TestDto> testPageDto;

    @BeforeEach
    void setUp() {
        testBaseController = new TestBaseController(baseService);
        
        testDto = new TestDto();
        testDto.setId(1L);
        testDto.setName("Test Entity");
        testDto.setValue(100.0);

        TestDto secondDto = new TestDto();
        secondDto.setId(2L);
        secondDto.setName("Second Entity");
        secondDto.setValue(200.0);

        testDtos = Arrays.asList(testDto, secondDto);

        testPageDto = new PageDto<>();
        testPageDto.setContent(testDtos);
        testPageDto.setTotalElements(2L);
        testPageDto.setTotalPages(1);
        testPageDto.setNumber(0);
        testPageDto.setSize(10);
    }

    @Test
    void testCreate_Success() throws BaseException {
        when(baseService.create(any(TestDto.class)))
                .thenReturn(ResponseEntity.ok(testDto));

        ResponseEntity<TestDto> response = testBaseController.create(testDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testDto.getId(), response.getBody().getId());
        assertEquals(testDto.getName(), response.getBody().getName());
        assertEquals(testDto.getValue(), response.getBody().getValue());

        verify(baseService, times(1)).create(testDto);
    }

    @Test
    void testCreate_WithValidData() throws BaseException {
        TestDto newDto = new TestDto();
        newDto.setName("New Entity");
        newDto.setValue(300.0);

        TestDto createdDto = new TestDto();
        createdDto.setId(3L);
        createdDto.setName("New Entity");
        createdDto.setValue(300.0);

        when(baseService.create(any(TestDto.class)))
                .thenReturn(ResponseEntity.ok(createdDto));

        ResponseEntity<TestDto> response = testBaseController.create(newDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3L, response.getBody().getId());
        assertEquals("New Entity", response.getBody().getName());
        assertEquals(300.0, response.getBody().getValue());

        verify(baseService, times(1)).create(newDto);
    }

    @Test
    void testCreate_ServiceThrowsException() throws BaseException {
        String errorMessage = "Entity already exists";
        when(baseService.create(any(TestDto.class)))
                .thenThrow(new BaseException(errorMessage, HttpStatus.BAD_REQUEST));

        BaseException exception = assertThrows(BaseException.class,
                () -> testBaseController.create(testDto));

        assertEquals(errorMessage, exception.getMessage());
        verify(baseService, times(1)).create(testDto);
    }

    @Test
    void testGetById_Success() throws BaseException {
        long entityId = 1L;
        when(baseService.getById(entityId))
                .thenReturn(ResponseEntity.ok(testDto));

        ResponseEntity<TestDto> response = testBaseController.getById(entityId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testDto.getId(), response.getBody().getId());
        assertEquals(testDto.getName(), response.getBody().getName());

        verify(baseService, times(1)).getById(entityId);
    }

    @Test
    void testGetById_EntityNotFound() throws BaseException {
        long entityId = 999L;
        String errorMessage = "Entity not found";
        when(baseService.getById(entityId))
                .thenThrow(new BaseException(errorMessage, HttpStatus.BAD_REQUEST));

        BaseException exception = assertThrows(BaseException.class,
                () -> testBaseController.getById(entityId));

        assertEquals(errorMessage, exception.getMessage());
        verify(baseService, times(1)).getById(entityId);
    }

    @Test
    void testGetById_WithZeroId() throws BaseException {
        long entityId = 0L;
        String errorMessage = "Invalid entity ID";
        when(baseService.getById(entityId))
                .thenThrow(new BaseException(errorMessage, HttpStatus.BAD_REQUEST));

        BaseException exception = assertThrows(BaseException.class,
                () -> testBaseController.getById(entityId));

        assertEquals(errorMessage, exception.getMessage());
        verify(baseService, times(1)).getById(entityId);
    }

    @Test
    void testGetById_WithNegativeId() throws BaseException {
        long entityId = -1L;
        String errorMessage = "Invalid entity ID";
        when(baseService.getById(entityId))
                .thenThrow(new BaseException(errorMessage, HttpStatus.BAD_REQUEST));

        BaseException exception = assertThrows(BaseException.class,
                () -> testBaseController.getById(entityId));

        assertEquals(errorMessage, exception.getMessage());
        verify(baseService, times(1)).getById(entityId);
    }

    @Test
    void testGetAll_Success() {
        when(baseService.getAll())
                .thenReturn(ResponseEntity.ok(testDtos));

        ResponseEntity<List<TestDto>> response = testBaseController.getAll();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(testDto.getId(), response.getBody().get(0).getId());
        assertEquals("Second Entity", response.getBody().get(1).getName());

        verify(baseService, times(1)).getAll();
    }

    @Test
    void testGetAll_EmptyList() {
        List<TestDto> emptyList = Arrays.asList();
        when(baseService.getAll())
                .thenReturn(ResponseEntity.ok(emptyList));

        ResponseEntity<List<TestDto>> response = testBaseController.getAll();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(baseService, times(1)).getAll();
    }

    @Test
    void testGetPage_Success() {
        int page = 0;
        int size = 10;
        when(baseService.getPage(page, size))
                .thenReturn(ResponseEntity.ok(testPageDto));

        ResponseEntity<PageDto<TestDto>> response = testBaseController.getPage(page, size);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2L, response.getBody().getTotalElements());
        assertEquals(1, response.getBody().getTotalPages());
        assertEquals(0, response.getBody().getNumber());
        assertEquals(10, response.getBody().getSize());
        assertEquals(2, response.getBody().getContent().size());

        verify(baseService, times(1)).getPage(page, size);
    }

    @Test
    void testGetPage_WithDifferentPagination() {
        int page = 1;
        int size = 5;
        PageDto<TestDto> customPageDto = new PageDto<>();
        customPageDto.setContent(Arrays.asList(testDto));
        customPageDto.setTotalElements(1L);
        customPageDto.setTotalPages(2);
        customPageDto.setNumber(1);
        customPageDto.setSize(5);

        when(baseService.getPage(page, size))
                .thenReturn(ResponseEntity.ok(customPageDto));

        ResponseEntity<PageDto<TestDto>> response = testBaseController.getPage(page, size);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getTotalElements());
        assertEquals(2, response.getBody().getTotalPages());
        assertEquals(1, response.getBody().getNumber());
        assertEquals(5, response.getBody().getSize());

        verify(baseService, times(1)).getPage(page, size);
    }

    @Test
    void testGetPage_WithNegativePage() {
        int page = -1;
        int size = 10;
        when(baseService.getPage(page, size))
                .thenReturn(ResponseEntity.ok(testPageDto));

        ResponseEntity<PageDto<TestDto>> response = testBaseController.getPage(page, size);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(baseService, times(1)).getPage(page, size);
    }

    @Test
    void testGetPage_WithZeroSize() {
        int page = 0;
        int size = 0;
        when(baseService.getPage(page, size))
                .thenReturn(ResponseEntity.ok(testPageDto));

        ResponseEntity<PageDto<TestDto>> response = testBaseController.getPage(page, size);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(baseService, times(1)).getPage(page, size);
    }

    @Test
    void testUpdate_Success() throws BaseException {
        long entityId = 1L;
        TestDto updatedDto = new TestDto();
        updatedDto.setId(entityId);
        updatedDto.setName("Updated Entity");
        updatedDto.setValue(150.0);

        when(baseService.update(eq(entityId), any(TestDto.class)))
                .thenReturn(ResponseEntity.ok(updatedDto));

        ResponseEntity<TestDto> response = testBaseController.update(entityId, updatedDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(entityId, response.getBody().getId());
        assertEquals("Updated Entity", response.getBody().getName());
        assertEquals(150.0, response.getBody().getValue());

        verify(baseService, times(1)).update(entityId, updatedDto);
    }

    @Test
    void testUpdate_EntityNotFound() throws BaseException {
        long entityId = 999L;
        String errorMessage = "Entity not found";
        when(baseService.update(eq(entityId), any(TestDto.class)))
                .thenThrow(new BaseException(errorMessage, HttpStatus.BAD_REQUEST));

        BaseException exception = assertThrows(BaseException.class,
                () -> testBaseController.update(entityId, testDto));

        assertEquals(errorMessage, exception.getMessage());
        verify(baseService, times(1)).update(entityId, testDto);
    }

    @Test
    void testUpdate_WithInvalidData() throws BaseException {
        long entityId = 1L;
        testDto.setName(null);
        String errorMessage = "Name cannot be null";
        when(baseService.update(eq(entityId), any(TestDto.class)))
                .thenThrow(new BaseException(errorMessage, HttpStatus.BAD_REQUEST));

        BaseException exception = assertThrows(BaseException.class,
                () -> testBaseController.update(entityId, testDto));

        assertEquals(errorMessage, exception.getMessage());
        verify(baseService, times(1)).update(entityId, testDto);
    }

    @Test
    void testDeleteById_Success() throws BaseException {
        long entityId = 1L;
        when(baseService.deleteById(entityId))
                .thenReturn(ResponseEntity.ok(true));

        ResponseEntity<Boolean> response = testBaseController.deleteById(entityId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());

        verify(baseService, times(1)).deleteById(entityId);
    }

    @Test
    void testDeleteById_EntityNotFound() throws BaseException {
        long entityId = 999L;
        String errorMessage = "Entity not found";
        when(baseService.deleteById(entityId))
                .thenThrow(new BaseException(errorMessage, HttpStatus.BAD_REQUEST));

        BaseException exception = assertThrows(BaseException.class,
                () -> testBaseController.deleteById(entityId));

        assertEquals(errorMessage, exception.getMessage());
        verify(baseService, times(1)).deleteById(entityId);
    }

    @Test
    void testDeleteById_WithZeroId() throws BaseException {
        long entityId = 0L;
        String errorMessage = "Invalid entity ID";
        when(baseService.deleteById(entityId))
                .thenThrow(new BaseException(errorMessage, HttpStatus.BAD_REQUEST));

        BaseException exception = assertThrows(BaseException.class,
                () -> testBaseController.deleteById(entityId));

        assertEquals(errorMessage, exception.getMessage());
        verify(baseService, times(1)).deleteById(entityId);
    }

    private static class TestBaseController extends BaseController<TestDto, IBaseService<TestDto>> {
        public TestBaseController(IBaseService<TestDto> service) {
            super(service);
        }
    }

    private static class TestDto extends BaseDto {
        private String name;
        private double value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }
    }
}
