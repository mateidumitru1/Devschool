package com.project.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid balance.")
public class InvalidBalanceException extends RuntimeException{

    public InvalidBalanceException(String message) {

        super(message);
    }
}
