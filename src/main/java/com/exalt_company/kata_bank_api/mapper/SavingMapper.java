package com.exalt_company.kata_bank_api.mapper;

import com.exalt_company.kata_bank_api.dto.SavingDto;
import com.exalt_company.kata_bank_api.entity.Saving;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface SavingMapper extends BaseMapper<SavingDto, Saving> {
    @Override
    @Mapping(target = "owner.id", source = "ownerId")
    Saving toEntity(SavingDto dto);

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "owner.id", source = "ownerId")
    void updateEntityFromDto(SavingDto dto, @MappingTarget Saving entity);

    @Override
    @Mapping(target = "ownerId", source = "owner.id")
    SavingDto toDto(Saving entity);
}
