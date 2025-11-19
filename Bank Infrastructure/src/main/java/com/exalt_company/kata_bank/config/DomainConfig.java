package com.exalt_company.kata_bank.config;

import com.exalt_company.fund_domain.ddd.FundDomainService;
import com.exalt_company.user_domain.ddd.UserDomainService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(
        basePackages = {
                "com.exalt_company.user_domain",
                "com.exalt_company.fund_domain"
        },
        includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, value = {
                UserDomainService.class,
                FundDomainService.class
        })
)
public class DomainConfig {}
