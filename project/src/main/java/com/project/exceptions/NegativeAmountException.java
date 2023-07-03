package com.project.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid amount.")
public class NegativeAmountException extends RuntimeException{

    public NegativeAmountException(String message) {

        super(message);
    }
}
