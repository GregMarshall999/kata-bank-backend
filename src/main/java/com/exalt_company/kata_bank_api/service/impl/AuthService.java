package com.exalt_company.kata_bank_api.service.impl;

import com.exalt_company.kata_bank_api.dto.auth.AuthenticationRequest;
import com.exalt_company.kata_bank_api.dto.auth.AuthenticationResponse;
import com.exalt_company.kata_bank_api.dto.auth.RegisterRequest;
import com.exalt_company.kata_bank_api.entity.BankUser;
import com.exalt_company.kata_bank_api.entity.user_fields.Credentials;
import com.exalt_company.kata_bank_api.entity.user_fields.Identity;
import com.exalt_company.kata_bank_api.enums.BankRole;
import com.exalt_company.kata_bank_api.exception.AuthException;
import com.exalt_company.kata_bank_api.repository.BankUserRepository;
import com.exalt_company.kata_bank_api.security.JwtService;
import com.exalt_company.kata_bank_api.service.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements IAuthService {
    private final BankUserRepository repository;
    private final PasswordEncoder encoder;
    private final JwtService service;
    private final AuthenticationManager manager;

    @Autowired
    public AuthService(BankUserRepository repository, PasswordEncoder encoder, JwtService service, AuthenticationManager manager) {
        this.repository = repository;
        this.encoder = encoder;
        this.service = service;
        this.manager = manager;
    }

    /**
     * On sign-up, we create the user and provide a token.
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<AuthenticationResponse> register(RegisterRequest request) throws AuthException {
        if (repository.findByCredentialsEmail(request.getEmail()).isPresent()) {
            throw new AuthException("User with this email already exists");
        }

        BankUser user = new BankUser();

        Identity identity = new Identity();
        identity.setName(request.getName());
        identity.setSurname(request.getSurname());

        Credentials credentials = new Credentials();
        credentials.setEmail(request.getEmail());

        try {
            credentials.setPassword(encoder.encode(request.getPassword()));
        } catch (IllegalArgumentException e) {
            throw new AuthException("Password can not be more than 72 bytes");
        }

        user.setIdentity(identity);
        user.setCredentials(credentials);
        user.setBankRole(BankRole.CLIENT);
        if(repository.findAll().isEmpty())
            user.setBankRole(BankRole.ADMIN);

        BankUser saved = repository.save(user);

        String token = service.generateToken(user, saved.getId(), user.getBankRole());

        AuthenticationResponse response = new AuthenticationResponse();
        response.setToken(token);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authentication checks if the user passes with correct credentials
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<AuthenticationResponse> authenticate(AuthenticationRequest request) throws AuthException {
        try {
            manager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new AuthException("Bad credentials");
        }

        BankUser user = repository.findByCredentialsEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = service.generateToken(user, user.getId(), user.getBankRole());

        AuthenticationResponse response = new AuthenticationResponse();
        response.setToken(token);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
