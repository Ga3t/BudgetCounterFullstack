package com.budget.AuhtService.services.impl;

import com.budget.AuhtService.dto.AuthResponseDto;
import com.budget.AuhtService.dto.LoginDto;
import com.budget.AuhtService.dto.RegisterDto;
import com.budget.AuhtService.models.RefreshToken;
import com.budget.AuhtService.models.UserEntity;
import com.budget.AuhtService.repository.UserRepository;
import com.budget.AuhtService.services.AuthServices;
import com.budget.AuhtService.services.RefreshTokenService;
import com.budget.core.enums.Role;
import com.budget.core.security.JwtGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@Primary
public class AuthServiceImpl implements AuthServices {

    private AuthenticationManager authenticationManager;
    private UserRepository repository;
    private PasswordEncoder encoder;
    private JwtGenerator jwtGenerator;
    private RefreshTokenService refreshTokenService;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public AuthServiceImpl(RefreshTokenService refreshTokenService, JwtGenerator jwtGenerator, PasswordEncoder encoder, UserRepository repository, AuthenticationManager authenticationManager) {
        this.refreshTokenService = refreshTokenService;
        this.jwtGenerator = jwtGenerator;
        this.encoder = encoder;
        this.repository = repository;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthResponseDto authenticateUser(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserEntity user = repository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Role role = user.getRole();
        String token = jwtGenerator.generateAccessToken(authentication, user.getId(), role);
        String refreshToken = refreshTokenService.generateRefreshToken(user.getId());
        return new AuthResponseDto(token, refreshToken);
    }

    @Override
    public String signUpUser(RegisterDto registerDto) {
        if(repository.existsByUsername(registerDto.getUsername())) {
            return "Username already taken!";
        }
        UserEntity user =new UserEntity();
        user.setUsername(registerDto.getUsername());
        user.setPassword(encoder.encode((registerDto.getPassword())));
        user.setRole(Role.USER);
        repository.save(user);
        return "User registered successfully!";
    }
}
