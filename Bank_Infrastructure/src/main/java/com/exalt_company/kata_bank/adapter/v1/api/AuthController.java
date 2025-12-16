package com.exalt_company.kata_bank.adapter.v1.api;

import com.exalt_company.fund_domain.api.FundAction;
import com.exalt_company.fund_domain.shared.FundException;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Operations for user authentication and token management")
public class AuthController {
    private final AccountAuthentication<String> authentication;
    private final BankUserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final FundAction fundAction;

    public AuthController(
            AccountAuthentication<String> authentication,
            BankUserRepository userRepository,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            FundAction fundAction
    ) {
        this.authentication = authentication;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.fundAction = fundAction;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Authenticate user",
            description = "Validates credentials and issues a JWT access token with a refresh token"
    )
    @ApiResponse(responseCode = "200", description = "Authentication succeeded",
            content = @Content(schema = @Schema(implementation = TokenResponse.class)))
    @ApiResponse(responseCode = "401", description = "Invalid credentials",
            content = @Content(schema = @Schema(implementation = Void.class)))
    public ResponseEntity<TokenResponse> authenticate(@RequestBody AuthRequest request) throws AuthenticationException {
        authentication.signInRequest(AuthMapper.toDomain(request));

        BankUser user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AuthenticationException("User not found"));

        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        
        return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken.getToken(), user.getId().toString()));
    }

    @PostMapping("/signUp")
    @Operation(
            summary = "Register user",
            description = "Creates a new account and returns access and refresh tokens"
    )
    @ApiResponse(responseCode = "200", description = "Registration succeeded",
            content = @Content(schema = @Schema(implementation = TokenResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid data supplied",
            content = @Content(schema = @Schema(implementation = Void.class)))
    public ResponseEntity<TokenResponse> signUp(@RequestBody SignUpRequest request)
            throws AuthenticationException, FundException {
        authentication.signUpRequest(AuthMapper.toDomain(request));
        
        BankUser user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AuthenticationException("User not found"));

        fundAction.createUserFund(user.getId());

        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        
        return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken.getToken(), user.getId().toString()));
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh tokens",
            description = "Verifies the provided refresh token, rotates it and issues a new access token"
    )
    @ApiResponse(responseCode = "200", description = "Tokens refreshed",
            content = @Content(schema = @Schema(implementation = TokenResponse.class)))
    @ApiResponse(responseCode = "404", description = "Refresh token not found or expired",
            content = @Content(schema = @Schema(implementation = Void.class)))
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.refreshToken())
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
        
        refreshToken = refreshTokenService.verifyExpiration(refreshToken);
        
        BankUser user = refreshToken.getUser();
        String accessToken = jwtService.generateAccessToken(user);
        
        // Optionally rotate refresh token
        refreshTokenService.deleteRefreshToken(refreshToken);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
        
        return ResponseEntity.ok(new TokenResponse(accessToken, newRefreshToken.getToken(), user.getId().toString()));
    }
}
