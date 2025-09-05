package com.exalt_company.kata_bank_api.service.impl;

import com.exalt_company.kata_bank_api.dto.BaseDto;
import com.exalt_company.kata_bank_api.dto.PageDto;
import com.exalt_company.kata_bank_api.entity.BaseEntity;
import com.exalt_company.kata_bank_api.exception.BaseException;
import com.exalt_company.kata_bank_api.mapper.BaseMapper;
import com.exalt_company.kata_bank_api.repository.BaseRepository;
import com.exalt_company.kata_bank_api.service.IBaseService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Base implementation of CRUD operations.
 * @param <D> BaseDto inheritors.
 * @param <E> BaseEntity inheritors.
 * @param <M> Mappers for entity - dto mapping.
 * @param <R> ORM repository for database communication.
 */
public abstract class BaseService<
        D extends BaseDto, E extends BaseEntity, M extends BaseMapper<D, E>, R extends BaseRepository<E>>
        implements IBaseService<D> {
    protected final M mapper;
    protected final R repository;
    protected final Class<E> entityClass;

    protected BaseService(M mapper, R repository, Class<E> entityClass) {
        this.mapper = mapper;
        this.repository = repository;
        this.entityClass = entityClass;
    }

    @Override
    public ResponseEntity<D> create(D dto) throws BaseException {
        if(dto == null) throw new BaseException("Could not create " + entityClass.getSimpleName() + ": Nothing to create");

        E saved = repository.save(mapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(saved));
    }

    @Override
    public ResponseEntity<D> getById(long id) throws BaseException {
        E found = repository.findById(id).orElseThrow(() -> new BaseException(entityClass.getSimpleName() + " not found"));
        return ResponseEntity.status(HttpStatus.FOUND).body(mapper.toDto(found));
    }

    @Override
    public ResponseEntity<List<D>> getAll() {
        List<E> all = repository.findAll();
        return ResponseEntity.status(HttpStatus.FOUND).body(mapper.toDtos(all));
    }

    @Override
    public ResponseEntity<PageDto<D>> getPage(int page, int size) {
        Page<E> result = repository.findAll(PageRequest.of(page, size));

        PageDto<D> pageDto = new PageDto<>();

        pageDto.setContent(mapper.toDtos(result.getContent()));
        pageDto.setNumber(result.getNumber());
        pageDto.setSize(result.getSize());
        pageDto.setTotalPages(result.getTotalPages());
        pageDto.setTotalElements(result.getTotalElements());
        pageDto.setNumberOfElements(result.getNumberOfElements());

        return ResponseEntity.status(HttpStatus.FOUND).body(pageDto);
    }

    @Override
    public ResponseEntity<D> update(long id, D dto) throws BaseException {
        if(dto == null) throw new BaseException("Could not update " + entityClass.getSimpleName() + ": Nothing to update.");

        E current = repository.findById(id).orElseThrow(() -> new BaseException(
                "Could not update " + entityClass.getSimpleName() + ": Please create first."));

        dto.setId(id);
        mapper.updateEntityFromDto(dto, current);

        E updated = repository.save(current);

        return ResponseEntity.status(HttpStatus.OK).body(mapper.toDto(updated));
    }

    @Override
    public ResponseEntity<Boolean> deleteById(long id) throws BaseException {
        repository.findById(id).orElseThrow(() -> new BaseException(
                "Could not delete " + entityClass.getSimpleName() + ": Nothing to delete."));

        repository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body(true);
    }

    @Override
    public ResponseEntity<Boolean> delete(D dto) throws BaseException {
        if(dto == null) throw new BaseException("Could not delete " + entityClass.getSimpleName() + ": Nothing to delete.");

        E toDelete = repository.findOne(Example.of(mapper.toEntity(dto))).orElseThrow(() -> new BaseException(
                "Could not delete " + entityClass.getSimpleName() + ": Nothing to delete."));

        repository.delete(toDelete);
        return ResponseEntity.status(HttpStatus.OK).body(true);
    }
}
