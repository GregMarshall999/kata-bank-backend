package com.exalt_company.kata_bank_api.service.impl;

import com.exalt_company.kata_bank_api.dto.FundDto;
import com.exalt_company.kata_bank_api.entity.Fund;
import com.exalt_company.kata_bank_api.mapper.FundMapper;
import com.exalt_company.kata_bank_api.repository.FundRepository;
import com.exalt_company.kata_bank_api.service.IFundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FundService extends BaseService<FundDto, Fund, FundMapper, FundRepository> implements IFundService {
    @Autowired
    public FundService(FundMapper mapper, FundRepository repository) {
        super(mapper, repository, Fund.class);
    }
}
