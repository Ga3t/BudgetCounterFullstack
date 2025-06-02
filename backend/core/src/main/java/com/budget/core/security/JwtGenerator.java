package com.budget.core.security;


import com.budget.core.enums.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtGenerator {

    private final SecretKey secretKey;
    private final Long expAccessToken;


    public JwtGenerator(@Value("${jwt.secret}") String secret, @Value("${jwt.exp.access}") Long expAccessToken) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expAccessToken = expAccessToken;
    }


    public String generateAccessToken(Authentication authentication, Long userId, Role role) {
        Date currDate = new Date();
        Date expDate = new Date(currDate.getTime() + expAccessToken);
        String username = authentication.getName();
        return Jwts.builder()
                .setSubject(username)
                .claim("id", userId)
                .claim("role", role)
                .setIssuedAt(currDate)
                .setExpiration(expDate)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }


    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public String getUsernameFromJwt(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public Long getUserIdFromJwt(String token){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("id", Long.class);
    }
}
