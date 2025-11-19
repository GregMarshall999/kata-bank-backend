package com.exalt_company.kata_bank.mapper;

import com.exalt_company.fund_domain.api.resource.FundResource;
import com.exalt_company.fund_domain.domain.Fund;
import com.exalt_company.kata_bank.adapter.v1.resource.FundRequest;
import com.exalt_company.kata_bank.entity.BankFund;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class FundMapper {
    private FundMapper() {}

    public static <F extends FundResource> F toDomain(FundRequest request, Class<F> fundResourceType)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        Constructor<F> constructor = fundResourceType.getDeclaredConstructor(UUID.class, UUID.class, double.class);
        return constructor.newInstance(request.fundId(), request.fundOwnerId(), request.amount());
    }

    public static Fund toDomain(BankFund bankFund) {
        return new Fund(bankFund.getId(), bankFund.getBalance(), bankFund.getOwner().getId());
    }
}
