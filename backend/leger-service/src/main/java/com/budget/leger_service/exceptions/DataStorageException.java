package com.budget.leger_service.exceptions;

public class DataStorageException extends RuntimeException{
    public DataStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataStorageException(String message) {
        super(message);
    }
}
