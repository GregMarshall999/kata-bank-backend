package com.exalt_company.kata_bank.adapter.v1.api;

import com.exalt_company.kata_bank.adapter.v1.resource.AuthRequest;
import com.exalt_company.kata_bank.adapter.v1.resource.SignUpRequest;
import com.exalt_company.kata_bank.mapper.AuthMapper;
import com.exalt_company.user_domain.api.AccountAuthentication;
import com.exalt_company.user_domain.api.resource.authentication.AuthenticationResponse;
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

    public AuthController(AccountAuthentication<String> authentication, AuthMapper mapper) {
        this.authentication = authentication;
        this.mapper = mapper;
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticate(@RequestBody AuthRequest request) throws AuthenticationException {
        AuthenticationResponse<String> response = authentication.signInRequest(mapper.toDomain(request));

        return ResponseEntity.ok(response.authenticationToken());
    }

    @PostMapping("/signUp")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequest request) throws AuthenticationException {
        AuthenticationResponse<String> response = authentication.signUpRequest(mapper.toDomain(request));

        return ResponseEntity.ok(response.authenticationToken());
    }
}
