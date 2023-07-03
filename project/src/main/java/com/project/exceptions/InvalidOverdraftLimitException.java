package com.project.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid overdraft limit.")
public class InvalidOverdraftLimitException extends RuntimeException{

    public InvalidOverdraftLimitException(String message) {

        super(message);
    }
}
