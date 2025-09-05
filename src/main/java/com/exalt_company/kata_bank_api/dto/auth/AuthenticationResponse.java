package com.exalt_company.kata_bank_api.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object containing JWT token after successful authentication")
public class AuthenticationResponse {
    @Schema(description = "JWT token for authenticated user", 
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
