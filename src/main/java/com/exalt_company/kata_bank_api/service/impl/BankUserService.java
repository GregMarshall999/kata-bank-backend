package com.exalt_company.kata_bank_api.service.impl;

import com.exalt_company.kata_bank_api.dto.BankUserDto;
import com.exalt_company.kata_bank_api.entity.BankUser;
import com.exalt_company.kata_bank_api.mapper.BankUserMapper;
import com.exalt_company.kata_bank_api.repository.BankUserRepository;
import com.exalt_company.kata_bank_api.service.IBankUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BankUserService extends BaseService<BankUserDto, BankUser, BankUserMapper, BankUserRepository>
        implements IBankUserService {
    @Autowired
    public BankUserService(BankUserMapper mapper, BankUserRepository repository) {
        super(mapper, repository, BankUser.class);
    }
}
