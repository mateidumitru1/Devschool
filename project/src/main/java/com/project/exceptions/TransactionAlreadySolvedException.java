package com.project.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Transaction already resolved.")
public class TransactionAlreadySolvedException extends RuntimeException{

    public TransactionAlreadySolvedException(String message) {

        super(message);
    }
}
