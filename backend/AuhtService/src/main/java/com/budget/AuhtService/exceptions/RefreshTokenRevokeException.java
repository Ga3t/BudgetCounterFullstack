package com.budget.AuhtService.exceptions;

public class RefreshTokenRevokeException extends RuntimeException{
    public RefreshTokenRevokeException() {
        super("Refresh token was revoked");
    }

    public RefreshTokenRevokeException(String message) {
        super(message);
    }

    public RefreshTokenRevokeException(String message, Throwable cause) {
        super(message, cause);
    }
}
