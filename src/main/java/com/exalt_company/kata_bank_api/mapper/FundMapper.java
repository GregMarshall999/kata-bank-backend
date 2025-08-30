package com.exalt_company.kata_bank_api.mapper;

import com.exalt_company.kata_bank_api.dto.FundDto;
import com.exalt_company.kata_bank_api.entity.Fund;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FundMapper extends BaseMapper<FundDto, Fund> {
    @Override
    @Mapping(target = "owner.id", source = "ownerId")
    Fund toEntity(FundDto dto);

    @Override
    @Mapping(target = "ownerId", source = "owner.id")
    FundDto toDto(Fund entity);
}
