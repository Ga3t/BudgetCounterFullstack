package com.budget.AuhtService.controllers;


import com.budget.AuhtService.exceptions.RefreshTokenExpiredException;
import com.budget.AuhtService.exceptions.RefreshTokenLeakedException;
import com.budget.core.exceptions.TokenNotFoundException;
import com.budget.core.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RefreshTokenExpiredException.class)
    public ResponseEntity<String> handleRefreshTokenExpired(RefreshTokenExpiredException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Refresh token expired: " + ex.getMessage());
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<String> handlerTokenNotFound(TokenNotFoundException ex){
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Token not found: " + ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handlerUserNotFound(UserNotFoundException ex){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("User not found:" + ex.getMessage());

    }

    @ExceptionHandler(RefreshTokenLeakedException.class)
    public ResponseEntity<String> handlerRefreshTokenLeaked(RefreshTokenLeakedException ex){
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("The token does not belong to this user" + ex.getMessage());
    }


}
