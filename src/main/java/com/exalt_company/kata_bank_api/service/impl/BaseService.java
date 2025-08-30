package com.exalt_company.kata_bank_api.service.impl;

import com.exalt_company.kata_bank_api.dto.BaseDto;
import com.exalt_company.kata_bank_api.dto.PageDto;
import com.exalt_company.kata_bank_api.service.IBaseService;
import org.springframework.http.ResponseEntity;

import java.util.List;

public abstract class BaseService<D extends BaseDto> implements IBaseService<D> {
    @Override
    public ResponseEntity<D> create(D dto) {
        return null;
    }

    @Override
    public ResponseEntity<D> getById(long id) {
        return null;
    }

    @Override
    public ResponseEntity<List<D>> getAll() {
        return null;
    }

    @Override
    public ResponseEntity<PageDto<D>> getPage(int page, int size) {
        return null;
    }

    @Override
    public ResponseEntity<D> update(long id, D dto) {
        return null;
    }

    @Override
    public ResponseEntity<Boolean> deleteById(long id) {
        return null;
    }

    @Override
    public ResponseEntity<Boolean> delete(D dto) {
        return null;
    }
}
