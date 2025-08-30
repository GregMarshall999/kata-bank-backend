package com.exalt_company.kata_bank_api.service.impl;

import com.exalt_company.kata_bank_api.dto.AdvisorDto;
import com.exalt_company.kata_bank_api.entity.Advisor;
import com.exalt_company.kata_bank_api.mapper.AdvisorMapper;
import com.exalt_company.kata_bank_api.repository.AdvisorRepository;
import com.exalt_company.kata_bank_api.service.IAdvisorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * We now have a service with extensive CRUD capabilities through minimal coding.
 * At any point we can override to our needs. Add more specific functionality all while maintaining a safe upscaling.
 */
@Service
public class AdvisorService extends BaseService<AdvisorDto, Advisor, AdvisorMapper, AdvisorRepository>
        implements IAdvisorService {
    @Autowired
    public AdvisorService(AdvisorMapper mapper, AdvisorRepository repository) {
        super(mapper, repository, Advisor.class);
    }
}
