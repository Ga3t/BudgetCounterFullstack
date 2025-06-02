package com.budget.leger_service.controllers;


import com.budget.leger_service.exceptions.CreateTransactionExceptions;
import com.budget.leger_service.exceptions.DataStorageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {



    @ExceptionHandler(CreateTransactionExceptions.class)
    public ResponseEntity<String> createTransactionExHandler(CreateTransactionExceptions ex){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Bad request try again" + ex);
    }

    @ExceptionHandler(DataStorageException.class)
    public ResponseEntity<String> dataStorageExHandler(DataStorageException ex){
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Some problems with server. Will fix it" + ex);
    }
}
