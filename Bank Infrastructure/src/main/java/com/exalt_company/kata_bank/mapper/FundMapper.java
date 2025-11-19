package com.exalt_company.kata_bank.mapper;

import com.exalt_company.fund_domain.api.resource.FundResource;
import com.exalt_company.fund_domain.domain.Fund;
import com.exalt_company.kata_bank.adapter.v1.resource.FundRequest;
import com.exalt_company.kata_bank.entity.BankFund;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Mapper utility class for converting between fund-related objects.
 * Handles mapping between API resources, domain objects, and JPA entities.
 */
public class FundMapper {
    private FundMapper() {}

    /**
     * Converts a FundRequest to a domain FundResource using reflection.
     *
     * @param <F> the type of FundResource to create
     * @param request the fund request from the API layer
     * @param fundResourceType the class type of the FundResource to create
     * @return a new instance of the specified FundResource type
     * @throws NoSuchMethodException if the constructor is not found
     * @throws InvocationTargetException if the constructor invocation fails
     * @throws InstantiationException if the class cannot be instantiated
     * @throws IllegalAccessException if the constructor is not accessible
     */
    public static <F extends FundResource> F toDomain(FundRequest request, Class<F> fundResourceType)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        Constructor<F> constructor = fundResourceType.getDeclaredConstructor(UUID.class, UUID.class, BigDecimal.class);
        return constructor.newInstance(request.fundId(), request.fundOwnerId(), request.amount());
    }

    /**
     * Converts a BankFund JPA entity to a domain Fund object.
     *
     * @param bankFund the JPA entity to convert
     * @return a domain Fund object
     */
    public static Fund toDomain(BankFund bankFund) {
        return new Fund(bankFund.getId(), bankFund.getBalance(), bankFund.getOwner().getId());
    }
}
