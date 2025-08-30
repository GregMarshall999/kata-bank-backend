package com.exalt_company.kata_bank_api.service;

import com.exalt_company.kata_bank_api.dto.auth.AuthenticationRequest;
import com.exalt_company.kata_bank_api.dto.auth.AuthenticationResponse;
import org.springframework.http.ResponseEntity;

public interface IAuthService {
    ResponseEntity<AuthenticationResponse> register(AuthenticationRequest request);
    ResponseEntity<AuthenticationResponse> authenticate(AuthenticationRequest request);
}
