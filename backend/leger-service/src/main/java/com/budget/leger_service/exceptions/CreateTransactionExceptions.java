package com.budget.leger_service.exceptions;

public class CreateTransactionExceptions extends RuntimeException{
    public CreateTransactionExceptions (String message){
        super(message);
    }
    public CreateTransactionExceptions(String message, Throwable cause) {
        super(message, cause);
    }
}
