package com.exalt_company.kata_bank_api.mapper;

import com.exalt_company.kata_bank_api.dto.fund.FundDto;
import com.exalt_company.kata_bank_api.dto.fund.FundOpDto;
import com.exalt_company.kata_bank_api.entity.Fund;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface FundMapper extends BaseMapper<FundDto, Fund> {
    @Override
    @Mapping(target = "owner.id", source = "ownerId")
    Fund toEntity(FundDto dto);

    @Mapping(target = "owner.id", source = "ownerId")
    Fund toEntity(FundOpDto dto);

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "owner.id", source = "ownerId")
    void updateEntityFromDto(FundDto dto, @MappingTarget Fund entity);

    @Override
    @Mapping(target = "ownerId", source = "owner.id")
    FundDto toDto(Fund entity);
}
