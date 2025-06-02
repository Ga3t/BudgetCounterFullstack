package com.budget.investments_service.exceptions;


public class NotEnoughCryptoException extends RuntimeException{

    public NotEnoughCryptoException (String message){
        super(message);
    }
    public NotEnoughCryptoException(String message, Throwable cause) {
        super(message, cause);
    }
}
