package com.filip.versu.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class FeedbackActionExistsException extends RuntimeException {

    public FeedbackActionExistsException(String msg) {
        super(msg);
    }

}
