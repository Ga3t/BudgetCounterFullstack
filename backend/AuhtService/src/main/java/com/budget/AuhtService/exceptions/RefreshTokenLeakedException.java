package com.budget.AuhtService.exceptions;

public class RefreshTokenLeakedException extends RuntimeException{

    private final Long tokenId;

    public RefreshTokenLeakedException(Long tokenId) {
        super("Refresh token does not belong to this user. Possible leak.");
        this.tokenId = tokenId;
    }

    public Long getTokenId() {
        return tokenId;
    }

    public RefreshTokenLeakedException(String message, Long tokenId) {
        super(message);
        this.tokenId = tokenId;
    }

    public RefreshTokenLeakedException(String message, Throwable cause, Long tokenId) {
        super(message, cause);
        this.tokenId = tokenId;
    }
}
