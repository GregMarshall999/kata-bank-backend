package com.exalt_company.kata_bank_api.service;

import com.exalt_company.kata_bank_api.dto.auth.AuthenticationRequest;
import com.exalt_company.kata_bank_api.dto.auth.AuthenticationResponse;
import com.exalt_company.kata_bank_api.dto.auth.RegisterRequest;
import com.exalt_company.kata_bank_api.exception.AuthException;
import org.springframework.http.ResponseEntity;

public interface IAuthService {
    ResponseEntity<AuthenticationResponse> register(RegisterRequest request) throws AuthException;
    ResponseEntity<AuthenticationResponse> authenticate(AuthenticationRequest request) throws AuthException;
}
