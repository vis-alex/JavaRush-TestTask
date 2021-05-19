package com.game.exception_handling;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class Exception400 extends RuntimeException{
    public Exception400(String message) {
        super(message);
    }
}