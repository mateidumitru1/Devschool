package com.project.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Account number already exists")
public class AccountNumberAlreadyExistsException extends RuntimeException{

    public AccountNumberAlreadyExistsException(String message) {

        super(message);
    }
}
