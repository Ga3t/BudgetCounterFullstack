package com.budget.investments_service.exceptions;

public class TransactionIsBeforeLastUpdateException extends RuntimeException{
    public TransactionIsBeforeLastUpdateException (String message){
        super(message);
    }
    public TransactionIsBeforeLastUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

}
