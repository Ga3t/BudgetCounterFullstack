package com.budget.AuhtService.services;


import com.budget.AuhtService.dto.AuthResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface RefreshTokenService {

    String generateRefreshToken(Long userId);
    AuthResponseDto refreshAccessToken(String refreshToken);
    void revokeRefreshToken(String refreshToken, Long userId);
}
