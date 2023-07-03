package com.project.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Iban already exists")
public class IbanAlreadyExistsException extends RuntimeException{

        public IbanAlreadyExistsException() {

            super("Iban already exists");
        }
}
