package com.example.split.exception;

public class TransactionNotFoundException extends RuntimeException{
    public TransactionNotFoundException(String message) { super(message);}
}
