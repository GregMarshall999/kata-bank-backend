package com.exalt_company.kata_bank_api.mapper;

import com.exalt_company.kata_bank_api.dto.AdvisorDto;
import com.exalt_company.kata_bank_api.entity.Advisor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mappers help us translate an entity to a dto with minimal code.
 * By default, the plugin will match the same fields together.
 * In our case we need to map embedded fields so a helping hand with the mapping marker is needed.
 * Implemented classes will be generated in target for the injection.
 */
@Mapper(componentModel = "spring")
public interface AdvisorMapper extends BaseMapper<AdvisorDto, Advisor> {
    @Override
    @Mapping(target = "identity.name", source = "name")
    @Mapping(target = "identity.surname", source = "surname")
    @Mapping(target = "credentials.email", source = "email")
    Advisor toEntity(AdvisorDto dto);

    @Override
    @Mapping(target = "name", source = "identity.name")
    @Mapping(target = "surname", source = "identity.surname")
    @Mapping(target = "email", source = "credentials.email")
    AdvisorDto toDto(Advisor entity);
}
