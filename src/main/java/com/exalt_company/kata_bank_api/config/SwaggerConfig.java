package com.exalt_company.kata_bank_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        System.out.println("SwaggerConfig: Initializing OpenAPI configuration..."); //TODO: add logger
        return new OpenAPI()
                .info(new Info()
                        .title("Bank API")
                        .description("Banking API for managing bank accounts, users, and funds")
                        .version("0.0.1-SNAPSHOT")
                        .contact(new Contact()
                                .name("Exalt")
                                .email("contact@exalt-company.com")
                                .url("https://www.exalt-company.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Development server")
                        //new Server().url("https://api.exalt.com").description("Production server") Add for production deployment
                ))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter JWT token")));
    }
}
