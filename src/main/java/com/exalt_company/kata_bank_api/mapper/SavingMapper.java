package com.exalt_company.kata_bank_api.mapper;

import com.exalt_company.kata_bank_api.dto.SavingDto;
import com.exalt_company.kata_bank_api.entity.Saving;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SavingMapper extends BaseMapper<SavingDto, Saving> {
    @Override
    @Mapping(target = "owner.id", source = "ownerId")
    Saving toEntity(SavingDto dto);

    @Override
    @Mapping(target = "ownerId", source = "owner.id")
    SavingDto toDto(Saving entity);
}
