package com.budget.AuhtService.dto;

import lombok.Getter;

@Getter
public class AuthResponseDto {

    private String accessToken;
    private String tokenType = "Bearer";
    private String refreshToken;
    private String refreshType = "Refresh Token";


    public AuthResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken= refreshToken;
    }
}

