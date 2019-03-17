package com.filip.versu.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception is thrown if there is a request to perform an operation on non-existing entity or
 * if a dependency entity does not exists (request to create a like on non-existing shopping item).
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotExistsException extends RuntimeException {

    public EntityNotExistsException(String msg) {
        super(msg);
    }
}
