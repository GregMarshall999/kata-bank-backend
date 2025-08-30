package com.exalt_company.kata_bank_api.mapper;

import com.exalt_company.kata_bank_api.dto.BankUserDto;
import com.exalt_company.kata_bank_api.entity.BankUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mappers help us translate an entity to a dto with minimal code.
 * By default, the plugin will match the same fields together.
 * In our case we need to map embedded fields so a helping hand with the mapping marker is needed.
 * Implemented classes will be generated in target for the injection.
 * <p>
 * Since we also have an entity to id mapping, we complete the field linkage.
 */
@Mapper(componentModel = "spring")
public interface BankUserMapper extends BaseMapper<BankUserDto, BankUser> {
    @Override
    @Mapping(target = "identity.name", source = "name")
    @Mapping(target = "identity.surname", source = "surname")
    @Mapping(target = "credentials.email", source = "email")
    @Mapping(target = "advisor.id", source = "advisorId")
    BankUser toEntity(BankUserDto dto);

    @Override
    @Mapping(target = "name", source = "identity.name")
    @Mapping(target = "surname", source = "identity.surname")
    @Mapping(target = "email", source = "credentials.email")
    @Mapping(target = "advisorId", source = "advisor.id")
    BankUserDto toDto(BankUser entity);
}
