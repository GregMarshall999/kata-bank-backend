package com.exalt_company.kata_bank_api.controller;

import com.exalt_company.kata_bank_api.dto.BaseDto;
import com.exalt_company.kata_bank_api.dto.PageDto;
import com.exalt_company.kata_bank_api.service.IBaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * We have the entry points for all CRUD operations for any entity of this project.
 * @param <D> BaseDto inheritors
 * @param <S> Any entity service with CRUD capabilities
 */
public abstract class BaseController<D extends BaseDto, S extends IBaseService<D>> {
    protected final S service;

    protected BaseController(S service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<D> create(@RequestBody D dto) {
        return service.create(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<D> getById(@PathVariable long id) {
        return service.getById(id);
    }

    @GetMapping
    public ResponseEntity<List<D>> getAll() {
        return service.getAll();
    }

    @GetMapping("/{page}/{size}")
    public ResponseEntity<PageDto<D>> getPage(@PathVariable int page, @PathVariable int size) {
        return service.getPage(page, size);
    }

    @PutMapping("/{id}")
    public ResponseEntity<D> update(@PathVariable long id, @RequestBody D dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteById(@PathVariable long id) {
        return service.deleteById(id);
    }

    @DeleteMapping
    public ResponseEntity<Boolean> delete(@RequestBody D dto) {
        return service.delete(dto);
    }
}
