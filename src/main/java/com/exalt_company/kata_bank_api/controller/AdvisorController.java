package com.exalt_company.kata_bank_api.controller;

import com.exalt_company.kata_bank_api.dto.AdvisorDto;
import com.exalt_company.kata_bank_api.service.IAdvisorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Since we use generic CRUD operations, we have very little endpoints to add.
 * Autowired helps spring with dependency injection. It will find an implementation of the service interface and
 * instantiate it in the constructor.
 */
@RestController
@RequestMapping("/api/advisor")
public class AdvisorController extends BaseController<AdvisorDto, IAdvisorService> {
    @Autowired
    public AdvisorController(IAdvisorService service) {
        super(service);
    }
}
