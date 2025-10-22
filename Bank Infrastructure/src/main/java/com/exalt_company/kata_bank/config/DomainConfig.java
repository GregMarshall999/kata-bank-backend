package com.exalt_company.kata_bank.config;

import com.exalt_company.user_domain.ddd.UserDomainService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(
        basePackages = "com.exalt_company.user_domain",
        includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, value = UserDomainService.class)
)
public class DomainConfig {}
