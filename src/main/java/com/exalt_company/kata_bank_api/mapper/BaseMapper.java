package com.exalt_company.kata_bank_api.mapper;

import com.exalt_company.kata_bank_api.dto.BaseDto;
import com.exalt_company.kata_bank_api.entity.BaseEntity;

import java.util.List;

public interface BaseMapper<D extends BaseDto, E extends BaseEntity> {
    D toDto(E entity);
    E toEntity(D dto);

    List<D> toDtos(List<E> entities);
}
