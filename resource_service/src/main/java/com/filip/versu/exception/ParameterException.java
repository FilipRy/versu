package com.filip.versu.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * This exception is thrown if some input parameter is not valid.
 */
@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
public class ParameterException extends RuntimeException {

    public ParameterException(String msg) {
        super(msg);
    }

}
