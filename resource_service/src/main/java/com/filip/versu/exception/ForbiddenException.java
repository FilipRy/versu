package com.filip.versu.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception is thrown if there is a request to perform an operation on bad state of an entity (create vote_yes yes on bought item).
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String msg){
        super(msg);
    }

}
