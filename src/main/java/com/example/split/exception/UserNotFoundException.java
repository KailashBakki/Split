package com.example.split.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class UserNotFoundException extends RuntimeException{
//    private static final long serialVersionUID = 1L;

//    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public UserNotFoundException(String message) {
        super(message);
    }
}