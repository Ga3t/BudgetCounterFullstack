package com.budget.core.exceptions;

public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException() {
        super("Refresh token has expired");
    }

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
