package com.budget.AuhtService.services;


import com.budget.AuhtService.dto.AuthResponseDto;
import com.budget.AuhtService.dto.LoginDto;
import com.budget.AuhtService.dto.RegisterDto;
import org.springframework.stereotype.Service;

@Service
public interface AuthServices {
    AuthResponseDto authenticateUser(LoginDto loginDto);
    String signUpUser (RegisterDto registerDto);
}
