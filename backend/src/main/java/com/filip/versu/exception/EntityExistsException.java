package com.filip.versu.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception is thrown if there is a request to add an entity, which already exists (ID is already taken).
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class EntityExistsException extends RuntimeException {

    public EntityExistsException(String msg) {
        super(msg);
    }
}
