package com.exalt_company.kata_bank_api.security;

import com.exalt_company.kata_bank_api.enums.BankRole;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final AuthenticationProvider provider;
    private final JwtAuthorizationFilter jwtAuthFilter;
    private final String frontEndApp;

    private static final String[] allowedEndPoints = {
            "/api/auth/**",
            "/api/health/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/api-docs/**",
            "/webjars/**"
    };

    private static final String[] clientEndpoints = {
            "/api/audit-account/statement/**",
            "/api/fund", "/api/fund/deposit", "/api/fund/withdraw", "/api/fund/request-overdraw",
            "/api/fund/cancel-overdraw", "/api/fund/getUserFund/**",
            "/api/saving/open", "/api/saving/close", "/api/saving/deposit", "/api/saving/withdraw",
            "/api/saving/getUserSaving/**"
    };

    private static final String[] adminEndpoints = {
            "/api/audit-account/**",
            "/api/bank-user/**",
            "/api/fund/**",
            "/api/saving/**"
    };

    @Autowired
    public SecurityConfig(
            AuthenticationProvider provider,
            JwtAuthorizationFilter jwtAuthFilter,
            @Value("${allowed.origin}") String frontEndApp) {
        this.provider = provider;
        this.jwtAuthFilter = jwtAuthFilter;
        this.frontEndApp = frontEndApp;
    }

    /**
     * We set up spring security filter chain.
     * Currently, CORS is set to default and CSRF off.
     * Public endpoints are whitelisted.
     * Admin endpoints are locked to other users.
     * Anything else needs a JWT in order to access the resource.
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();

                    config.setAllowedOrigins(List.of(frontEndApp));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);

                    return config;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        requests -> requests.requestMatchers(allowedEndPoints).permitAll()
                        .requestMatchers(clientEndpoints).hasAnyAuthority(BankRole.CLIENT.name(), BankRole.ADMIN.name())
                        .requestMatchers(adminEndpoints).hasAuthority(BankRole.ADMIN.name())
                        .anyRequest().authenticated()
                )
                .sessionManagement(
                        sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(provider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
