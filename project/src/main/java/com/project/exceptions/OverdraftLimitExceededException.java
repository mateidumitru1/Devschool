package com.project.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Exceeded overdraft limit")
public class OverdraftLimitExceededException extends RuntimeException{

        public OverdraftLimitExceededException(String message) {

            super(message);
        }
}
