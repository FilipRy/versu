package com.filip.versu.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthenticationException extends RuntimeException {

    private static final String UNAUTHORIZED = "UNAUTHORIZED";

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException() {
        super(UNAUTHORIZED);
    }
}
