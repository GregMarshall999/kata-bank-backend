package com.exalt_company.kata_bank_api.mapper;

import com.exalt_company.kata_bank_api.dto.fund.FundDto;
import com.exalt_company.kata_bank_api.dto.fund.FundOpDto;
import com.exalt_company.kata_bank_api.dto.fund.OverdrawDto;
import com.exalt_company.kata_bank_api.entity.Fund;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FundMapper extends BaseMapper<FundDto, Fund> {
    @Override
    @Mapping(target = "owner.id", source = "ownerId")
    Fund toEntity(FundDto dto);

    @Mapping(target = "owner.id", source = "ownerId")
    Fund toEntity(FundOpDto dto);

    @Override
    @Mapping(target = "ownerId", source = "owner.id")
    FundDto toDto(Fund entity);
}
