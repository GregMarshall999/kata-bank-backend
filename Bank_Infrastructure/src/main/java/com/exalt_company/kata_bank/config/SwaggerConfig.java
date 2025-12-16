package com.exalt_company.kata_bank.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Kata Bank API",
                version = "v1",
                description = "Banking endpoints for authentication and account operations",
                contact = @Contact(
                        name = "Kata Bank Team",
                        email = "support@kata-bank.example"
                ),
                license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0.html")
        ),
        servers = {
                @Server(url = "/", description = "Default Server")
        },
        security = {
                @SecurityRequirement(name = SwaggerConfig.BEARER_SCHEME)
        }
)
@SecurityScheme(
        name = SwaggerConfig.BEARER_SCHEME,
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER,
        description = "Standard Authorization header using the Bearer scheme. Example: \"Bearer {token}\""
)
public class SwaggerConfig {
    public static final String BEARER_SCHEME = "BearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME,
                                new io.swagger.v3.oas.models.security.SecurityScheme()
                                        .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(io.swagger.v3.oas.models.security.SecurityScheme.In.HEADER)
                                        .name("Authorization")
                                        .description("Access token issued by the authentication endpoints")))
                .addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement().addList(BEARER_SCHEME))
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Kata Bank API")
                        .version("v1")
                        .description("Banking endpoints for authentication and account operations")
                        .license(new io.swagger.v3.oas.models.info.License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}

