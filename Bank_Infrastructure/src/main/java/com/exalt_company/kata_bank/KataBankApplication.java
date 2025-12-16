package com.exalt_company.kata_bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.exalt_company.kata_bank.entity")
@EnableJpaRepositories(basePackages = "com.exalt_company.kata_bank.repository")
public class KataBankApplication {
    public static void main(String[] args) {
        SpringApplication.run(KataBankApplication.class, args);
    }
}
