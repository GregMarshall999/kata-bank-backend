package com.exalt_company.kata_bank_api.mapper;

import com.exalt_company.kata_bank_api.dto.BaseDto;
import com.exalt_company.kata_bank_api.entity.BaseEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

public interface BaseMapper<D extends BaseDto, E extends BaseEntity> {
    D toDto(E entity);
    E toEntity(D dto);

    /**
     * This is to solve the full replace vs partial update problem.
     * Many entities may have non-nullable fields we wish to ignore during the update
     * @param dto
     * @param entity
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(D dto, @MappingTarget E entity);

    List<D> toDtos(List<E> entities);
}
