package com.exalt_company.kata_bank_api.service;

import com.exalt_company.kata_bank_api.dto.BaseDto;
import com.exalt_company.kata_bank_api.dto.PageDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * This service enables all base CRUD functionality.
 * @param <D> must be a BaseDto inheritor.
 */
public interface IBaseService<D extends BaseDto> {
    ResponseEntity<D> create(D dto);
    ResponseEntity<D> getById(long id);
    ResponseEntity<List<D>> getAll();
    ResponseEntity<PageDto<D>> getPage(int page, int size);
    ResponseEntity<D> update(long id, D dto);
    ResponseEntity<Boolean> deleteById(long id);
    ResponseEntity<Boolean> delete(D dto);
}
