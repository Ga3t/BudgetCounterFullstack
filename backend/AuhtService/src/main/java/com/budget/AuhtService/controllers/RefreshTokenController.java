package com.budget.AuhtService.controllers;


import com.budget.AuhtService.dto.AuthResponseDto;
import com.budget.AuhtService.models.RefreshToken;
import com.budget.AuhtService.services.RefreshTokenService;
import com.budget.core.security.JwtGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/refreshtoken/")
public class RefreshTokenController {


    private RefreshTokenService refreshTokenService;


    @Autowired
    public RefreshTokenController(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @GetMapping("/generateaccess")
    public ResponseEntity<AuthResponseDto> generateRefreshToken(@RequestHeader("X-Refresh-Token") String refreshToken){
        AuthResponseDto response = refreshTokenService.refreshAccessToken(refreshToken);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
