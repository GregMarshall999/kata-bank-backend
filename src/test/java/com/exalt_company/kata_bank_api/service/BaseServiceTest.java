package com.exalt_company.kata_bank_api.service;

import com.exalt_company.kata_bank_api.dto.BaseDto;
import com.exalt_company.kata_bank_api.dto.PageDto;
import com.exalt_company.kata_bank_api.entity.BaseEntity;
import com.exalt_company.kata_bank_api.exception.BaseException;
import com.exalt_company.kata_bank_api.mapper.BaseMapper;
import com.exalt_company.kata_bank_api.repository.BaseRepository;
import com.exalt_company.kata_bank_api.service.impl.BaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BaseServiceTest {
    @Mock
    private BaseMapper<TestDto, TestEntity> mapper;

    @Mock
    private BaseRepository<TestEntity> repository;

    private TestBaseService baseService;
    private TestDto testDto;
    private TestEntity testEntity;

    @BeforeEach
    void setUp() {
        baseService = new TestBaseService(mapper, repository);
        
        testDto = new TestDto();
        testDto.setId(1L);
        testDto.setName("Test Entity");

        testEntity = new TestEntity();
        testEntity.setId(1L);
        testEntity.setName("Test Entity");
    }

    @Test
    void testCreate_Success() throws BaseException {
        when(mapper.toEntity(testDto)).thenReturn(testEntity);
        when(repository.save(testEntity)).thenReturn(testEntity);
        when(mapper.toDto(testEntity)).thenReturn(testDto);

        ResponseEntity<TestDto> response = baseService.create(testDto);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Test Entity", response.getBody().getName());

        verify(mapper).toEntity(testDto);
        verify(repository).save(testEntity);
        verify(mapper).toDto(testEntity);
    }

    @Test
    void testCreate_WithNullDto() {
        BaseException exception = assertThrows(BaseException.class, () -> baseService.create(null));
        
        assertEquals("Could not create TestEntity: Nothing to create", exception.getMessage());
        verify(mapper, never()).toEntity(any());
        verify(repository, never()).save(any(TestEntity.class));
    }

    @Test
    void testGetById_Success() throws BaseException {
        when(repository.findById(1L)).thenReturn(Optional.of(testEntity));
        when(mapper.toDto(testEntity)).thenReturn(testDto);

        ResponseEntity<TestDto> response = baseService.getById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());

        verify(repository).findById(1L);
        verify(mapper).toDto(testEntity);
    }

    @Test
    void testGetById_NotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        BaseException exception = assertThrows(BaseException.class, () -> 
            baseService.getById(999L)
        );

        assertEquals("TestEntity not found", exception.getMessage());
        verify(repository).findById(999L);
        verify(mapper, never()).toDto(any(TestEntity.class));
    }

    @Test
    void testGetAll_Success() {
        List<TestEntity> entities = List.of(testEntity);
        List<TestDto> dtos = List.of(testDto);

        when(repository.findAll()).thenReturn(entities);
        when(mapper.toDtos(entities)).thenReturn(dtos);

        ResponseEntity<List<TestDto>> response = baseService.getAll();

        assertNotNull(response);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());

        verify(repository).findAll();
        verify(mapper).toDtos(entities);
    }

    @Test
    void testGetAll_EmptyList() {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        when(mapper.toDtos(Collections.emptyList())).thenReturn(Collections.emptyList());

        ResponseEntity<List<TestDto>> response = baseService.getAll();

        assertNotNull(response);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());

        verify(repository).findAll();
        verify(mapper).toDtos(Collections.emptyList());
    }

    @Test
    void testGetPage_Success() {
        Page<TestEntity> entityPage = new PageImpl<>(List.of(testEntity), PageRequest.of(0, 10), 1);
        List<TestDto> dtos = List.of(testDto);

        when(repository.findAll(PageRequest.of(0, 10))).thenReturn(entityPage);
        when(mapper.toDtos(List.of(testEntity))).thenReturn(dtos);

        ResponseEntity<PageDto<TestDto>> response = baseService.getPage(0, 10);

        assertNotNull(response);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals(0, response.getBody().getNumber());
        assertEquals(10, response.getBody().getSize());
        assertEquals(1, response.getBody().getTotalPages());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(1, response.getBody().getNumberOfElements());

        verify(repository).findAll(PageRequest.of(0, 10));
        verify(mapper).toDtos(List.of(testEntity));
    }

    @Test
    void testGetPage_EmptyPage() {
        Page<TestEntity> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);

        when(repository.findAll(PageRequest.of(0, 10))).thenReturn(emptyPage);
        when(mapper.toDtos(Collections.emptyList())).thenReturn(Collections.emptyList());

        ResponseEntity<PageDto<TestDto>> response = baseService.getPage(0, 10);

        assertNotNull(response);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getContent().size());
        assertEquals(0, response.getBody().getNumber());
        assertEquals(10, response.getBody().getSize());
        assertEquals(0, response.getBody().getTotalPages());
        assertEquals(0, response.getBody().getTotalElements());
        assertEquals(0, response.getBody().getNumberOfElements());
    }

    @Test
    void testUpdate_Success() throws BaseException {
        TestDto updatedDto = new TestDto();
        updatedDto.setId(1L);
        updatedDto.setName("Updated Entity");

        when(repository.findById(1L)).thenReturn(Optional.of(testEntity));
        when(repository.save(testEntity)).thenReturn(testEntity);
        when(mapper.toDto(testEntity)).thenReturn(updatedDto);

        ResponseEntity<TestDto> response = baseService.update(1L, updatedDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Entity", response.getBody().getName());

        verify(repository).findById(1L);
        verify(mapper).updateEntityFromDto(updatedDto, testEntity);
        verify(repository).save(testEntity);
        verify(mapper).toDto(testEntity);
    }

    @Test
    void testUpdate_NotFound() {
        TestDto updatedDto = new TestDto();
        updatedDto.setId(1L);

        when(repository.findById(999L)).thenReturn(Optional.empty());

        BaseException exception = assertThrows(BaseException.class, () -> 
            baseService.update(999L, updatedDto)
        );

        assertEquals("Could not update TestEntity: Please create first", exception.getMessage());
        verify(repository).findById(999L);
        verify(repository, never()).save(any(TestEntity.class));
    }

    @Test
    void testUpdate_WithNullDto() {
        assertThrows(BaseException.class, () -> baseService.update(1L, null));
    }

    @Test
    void testDeleteById_Success() throws BaseException {
        when(repository.findById(1L)).thenReturn(Optional.of(testEntity));

        ResponseEntity<Boolean> response = baseService.deleteById(1L);

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
            baseService.deleteById(999L)
        );

        assertEquals("Could not delete TestEntity: not found", exception.getMessage());
        verify(repository).findById(999L);
        verify(repository, never()).deleteById(anyLong());
    }

    private static class TestBaseService extends BaseService<TestDto, TestEntity, BaseMapper<TestDto, TestEntity>, BaseRepository<TestEntity>> {
        public TestBaseService(BaseMapper<TestDto, TestEntity> mapper, BaseRepository<TestEntity> repository) {
            super(mapper, repository, TestEntity.class);
        }
    }

    private static class TestDto extends BaseDto {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private static class TestEntity extends BaseEntity {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
