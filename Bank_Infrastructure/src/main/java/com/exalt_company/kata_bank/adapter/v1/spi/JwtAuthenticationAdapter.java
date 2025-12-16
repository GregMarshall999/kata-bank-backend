package com.exalt_company.kata_bank.adapter.v1.spi;

import com.exalt_company.kata_bank.repository.BankUserRepository;
import com.exalt_company.kata_bank.service.JwtService;
import com.exalt_company.user_domain.domain.account.BankUserAccount;
import com.exalt_company.user_domain.shared.exception.AuthenticationException;
import com.exalt_company.user_domain.spi.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationAdapter implements Authentication<String> {
    private final BankUserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public JwtAuthenticationAdapter(
            BankUserRepository userRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String generateLoginUserToken(BankUserAccount userAccount) throws AuthenticationException {
        return userRepository.findByEmail(userAccount.getEmail())
                .filter(user -> passwordEncoder.matches(userAccount.getPassword(), user.getPassword()))
                .map(jwtService::generateAccessToken)
                .orElseThrow(() -> new AuthenticationException("Invalid credentials"));
    }

    @Override
    public String generateNewUserToken(BankUserAccount userAccount) throws AuthenticationException {
        return userRepository.findByEmail(userAccount.getEmail())
                .map(jwtService::generateAccessToken)
                .orElseThrow(() -> new AuthenticationException("User not found"));
    }
}

