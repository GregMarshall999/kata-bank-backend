package com.exalt_company.kata_bank.adapter.v1.api;

import com.exalt_company.kata_bank.adapter.v1.resource.AuthRequest;
import com.exalt_company.kata_bank.adapter.v1.resource.RefreshTokenRequest;
import com.exalt_company.kata_bank.adapter.v1.resource.SignUpRequest;
import com.exalt_company.kata_bank.entity.BankUser;
import com.exalt_company.kata_bank.entity.RefreshToken;
import com.exalt_company.kata_bank.repository.BankUserRepository;
import com.exalt_company.kata_bank.service.JwtService;
import com.exalt_company.kata_bank.service.RefreshTokenService;
import com.exalt_company.user_domain.api.AccountAuthentication;
import com.exalt_company.user_domain.api.resource.authentication.AuthenticationResponse;
import com.exalt_company.user_domain.shared.BankRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AccountAuthentication<String> authentication;

    @MockitoBean
    private BankUserRepository userRepository;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @Test
    void authenticate_shouldReturnAccessAndRefreshTokens() throws Exception {
        BankUser user = sampleUser();
        RefreshToken refreshToken = refreshToken(user, "refresh-token");

        AuthRequest request = new AuthRequest(user.getEmail(), "password");

        when(authentication.signInRequest(any())).thenReturn(new AuthenticationResponse<>("ignored-token"));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(user)).thenReturn(refreshToken);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));

        verify(authentication).signInRequest(any());
        verify(refreshTokenService).createRefreshToken(user);
    }

    @Test
    void signUp_shouldReturnAccessAndRefreshTokens() throws Exception {
        BankUser user = sampleUser();
        RefreshToken refreshToken = refreshToken(user, "new-refresh-token");

        SignUpRequest request = new SignUpRequest(user.getName(), user.getSurname(), user.getEmail(), "password");

        when(authentication.signUpRequest(any())).thenReturn(new AuthenticationResponse<>("ignored-token"));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("signup-access-token");
        when(refreshTokenService.createRefreshToken(user)).thenReturn(refreshToken);

        mockMvc.perform(post("/api/v1/auth/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("signup-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"));

        verify(authentication).signUpRequest(any());
        verify(refreshTokenService).createRefreshToken(user);
    }

    @Test
    void refreshToken_shouldRotateRefreshTokenAndReturnNewTokens() throws Exception {
        BankUser user = sampleUser();

        RefreshToken storedToken = refreshToken(user, "current-refresh-token");
        RefreshToken rotatedToken = refreshToken(user, "rotated-refresh-token");

        when(refreshTokenService.findByToken("current-refresh-token")).thenReturn(Optional.of(storedToken));
        when(refreshTokenService.verifyExpiration(storedToken)).thenReturn(storedToken);
        when(jwtService.generateAccessToken(user)).thenReturn("new-access-token");
        when(refreshTokenService.createRefreshToken(user)).thenReturn(rotatedToken);

        RefreshTokenRequest request = new RefreshTokenRequest("current-refresh-token");

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("rotated-refresh-token"));

        verify(refreshTokenService).deleteRefreshToken(storedToken);
        verify(refreshTokenService).createRefreshToken(user);
    }

    private BankUser sampleUser() {
        BankUser user = new BankUser();
        user.setId(UUID.randomUUID());
        user.setName("John");
        user.setSurname("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("encoded-password");
        user.setRole(BankRole.CLIENT);
        return user;
    }

    private RefreshToken refreshToken(BankUser user, String tokenValue) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(tokenValue);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(3600));
        return refreshToken;
    }
}

