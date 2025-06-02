package com.budget.core.exceptions;

public class TokenNotFoundException extends RuntimeException{
    public TokenNotFoundException() {
        super("Refresh token has expired");
    }

    public TokenNotFoundException(String message) {
        super(message);
    }

    public TokenNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
