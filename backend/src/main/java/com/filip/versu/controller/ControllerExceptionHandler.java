package com.filip.versu.controller;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ControllerExceptionHandler {


    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "illegal arg exception")
    @ExceptionHandler(IllegalArgumentException.class)
    public void handling() {

    }

}
