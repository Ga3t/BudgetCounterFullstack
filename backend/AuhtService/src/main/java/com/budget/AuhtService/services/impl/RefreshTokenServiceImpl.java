package com.budget.AuhtService.services.impl;


import com.budget.AuhtService.dto.AuthResponseDto;
import com.budget.AuhtService.exceptions.RefreshTokenExpiredException;
import com.budget.AuhtService.exceptions.RefreshTokenLeakedException;
import com.budget.AuhtService.exceptions.RefreshTokenRevokeException;
import com.budget.AuhtService.models.RefreshToken;
import com.budget.AuhtService.models.UserEntity;
import com.budget.AuhtService.repository.RefreshTokenRepository;
import com.budget.AuhtService.repository.UserRepository;
import com.budget.AuhtService.services.RefreshTokenService;
import com.budget.core.exceptions.TokenNotFoundException;
import com.budget.core.exceptions.UserNotFoundException;
import com.budget.core.security.JwtGenerator;
import jakarta.ws.rs.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

@Primary
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${app.jwt.refreshDuration}")
    private Duration refreshTokenDuration;


    private AuthenticationManager authenticationManager;
    private RefreshTokenRepository refreshTokenRepository;
    private UserRepository userRepository;
    private JwtGenerator jwtGenerator;

    @Autowired
    public RefreshTokenServiceImpl(AuthenticationManager authenticationManager, RefreshTokenRepository refreshTokenRepository, UserRepository userRepository, JwtGenerator jwtGenerator) {
        this.authenticationManager = authenticationManager;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtGenerator = jwtGenerator;
    }

    @Override
    @Transactional
    public String generateRefreshToken(Long userId) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User not found"));
        RefreshToken refreshTokenEntity =new RefreshToken();
        refreshTokenEntity.setCreatedAt(Instant.now());
        refreshTokenEntity.setExpiryDate(Instant.now().plus(refreshTokenDuration));
        refreshTokenEntity.setUser(user);

        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[64];
        random.nextBytes(bytes);
        String refreshToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        refreshTokenEntity.setToken(refreshToken);

        refreshTokenRepository.save(refreshTokenEntity);
        return refreshToken;
    }

    @Override
    @Transactional
    public AuthResponseDto refreshAccessToken(String refreshToken) {


        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(()->new TokenNotFoundException("No such  refresh token"));

        UserEntity user = token.getUser();
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        Long userId = user.getId();
        Instant expiryDate = token.getExpiryDate();

        if(token.isRevoked())
            throw new RefreshTokenRevokeException();

        if(expiryDate.isBefore(Instant.now()))
            throw new RefreshTokenExpiredException("Token was expired need to login again");

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                null,
                List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String newJwtToken = jwtGenerator.generateAccessToken(authentication, userId, user.getRole());
        String newRefreshToken= generateRefreshToken(userId);
        revokeRefreshToken(token.getToken(), userId);

        return new AuthResponseDto(newJwtToken, newRefreshToken);
    }

    @Override
    @Transactional
    public void revokeRefreshToken(String refreshToken, Long userId) {

        RefreshToken revokeToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(()->new TokenNotFoundException("No such refresh token"));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException("User not found"));
        if(!user.getId().equals(revokeToken.getUser().getId())){
            revokeToken.setSpecialNotes("This token was leaked."+ Instant.now().toString());
            refreshTokenRepository.save(revokeToken);
            throw new RefreshTokenLeakedException(revokeToken.getId());
        }

        revokeToken.setRevoked(true);
        refreshTokenRepository.save(revokeToken);
    }
}
