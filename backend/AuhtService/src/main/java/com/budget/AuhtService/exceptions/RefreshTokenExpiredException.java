package com.budget.AuhtService.exceptions;


public class RefreshTokenExpiredException extends RuntimeException{

    public RefreshTokenExpiredException() {
        super("Refresh token has expired");
    }

    public RefreshTokenExpiredException(String message) {
        super(message);
    }

    public RefreshTokenExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
