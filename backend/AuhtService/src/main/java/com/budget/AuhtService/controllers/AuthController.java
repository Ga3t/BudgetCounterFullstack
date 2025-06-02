package com.budget.AuhtService.controllers;


import com.budget.AuhtService.dto.AuthResponseDto;
import com.budget.AuhtService.dto.LoginDto;
import com.budget.AuhtService.dto.RegisterDto;
import com.budget.AuhtService.repository.UserRepository;

import com.budget.AuhtService.services.AuthServices;
import com.budget.core.security.JwtGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/")
public class AuthController {

    private AuthenticationManager authenticationManager;
    private UserRepository repository;
    private PasswordEncoder encoder;
    private JwtGenerator jwtGenerator;
    private AuthServices authServices;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserRepository repository, PasswordEncoder encoder, JwtGenerator jwtGenerator, AuthServices authServices) {
        this.authenticationManager = authenticationManager;
        this.repository = repository;
        this.encoder = encoder;
        this.jwtGenerator = jwtGenerator;
        this.authServices = authServices;
    }


    @PostMapping("login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto) {
        System.out.println(loginDto);
        AuthResponseDto authResponseDto = authServices.authenticateUser(loginDto);
        return new ResponseEntity<>(authResponseDto, HttpStatus.OK);
    }

    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto){
        System.out.println(registerDto);
        String response = authServices.signUpUser(registerDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}

