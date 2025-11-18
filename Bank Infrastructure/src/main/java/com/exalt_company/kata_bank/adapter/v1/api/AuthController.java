package com.exalt_company.kata_bank.adapter.v1.api;

import com.exalt_company.kata_bank.adapter.v1.resource.AuthRequest;
import com.exalt_company.kata_bank.adapter.v1.resource.RefreshTokenRequest;
import com.exalt_company.kata_bank.adapter.v1.resource.SignUpRequest;
import com.exalt_company.kata_bank.adapter.v1.resource.TokenResponse;
import com.exalt_company.kata_bank.entity.BankUser;
import com.exalt_company.kata_bank.entity.RefreshToken;
import com.exalt_company.kata_bank.mapper.AuthMapper;
import com.exalt_company.kata_bank.repository.BankUserRepository;
import com.exalt_company.kata_bank.service.JwtService;
import com.exalt_company.kata_bank.service.RefreshTokenService;
import com.exalt_company.user_domain.api.AccountAuthentication;
import com.exalt_company.user_domain.shared.exception.AuthenticationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AccountAuthentication<String> authentication;
    private final AuthMapper mapper;
    private final BankUserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(
            AccountAuthentication<String> authentication,
            AuthMapper mapper,
            BankUserRepository userRepository,
            JwtService jwtService,
            RefreshTokenService refreshTokenService
    ) {
        this.authentication = authentication;
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> authenticate(@RequestBody AuthRequest request) throws AuthenticationException {
        authentication.signInRequest(mapper.toDomain(request));

        BankUser user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AuthenticationException("User not found"));

        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        
        return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken.getToken()));
    }

    @PostMapping("/signUp")
    public ResponseEntity<TokenResponse> signUp(@RequestBody SignUpRequest request) throws AuthenticationException {
        authentication.signUpRequest(mapper.toDomain(request));
        
        BankUser user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AuthenticationException("User not found"));
        
        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        
        return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken.getToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.refreshToken())
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
        
        refreshToken = refreshTokenService.verifyExpiration(refreshToken);
        
        BankUser user = refreshToken.getUser();
        String accessToken = jwtService.generateAccessToken(user);
        
        // Optionally rotate refresh token
        refreshTokenService.deleteRefreshToken(refreshToken);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
        
        return ResponseEntity.ok(new TokenResponse(accessToken, newRefreshToken.getToken()));
    }
}
