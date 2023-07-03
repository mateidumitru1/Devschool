package com.project.handlers;

import com.project.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionHandler {

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Account not found")
    @org.springframework.web.bind.annotation.ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<String> handleAccountNotFoundException(AccountNotFoundException exception) {

        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "User already exists")
    @org.springframework.web.bind.annotation.ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException exception) {

        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "User not found")
    @org.springframework.web.bind.annotation.ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException exception) {

        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid currency")
    @org.springframework.web.bind.annotation.ExceptionHandler(InvalidCurrencyException.class)
    public ResponseEntity<String> handleInvalidCurrencyException(InvalidCurrencyException exception) {

        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid amount")
    @org.springframework.web.bind.annotation.ExceptionHandler(NegativeAmountException.class)
    public ResponseEntity<String> handleInvalidAmountException(NegativeAmountException exception) {

        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Invalid account number")
    @org.springframework.web.bind.annotation.ExceptionHandler(AccountNumberAlreadyExistsException.class)
    public ResponseEntity<String> handleInvalidAccountNumberException(AccountNumberAlreadyExistsException exception) {

        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Invalid account iban")
    @org.springframework.web.bind.annotation.ExceptionHandler(IbanAlreadyExistsException.class)
    public ResponseEntity<String> handleInvalidAccountIbanException(IbanAlreadyExistsException exception) {

        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Transaction not found")
    @org.springframework.web.bind.annotation.ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<String> handleTransactionNotFoundException(TransactionNotFoundException exception) {

        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Transaction already solved")
    @org.springframework.web.bind.annotation.ExceptionHandler(TransactionAlreadySolvedException.class)
    public ResponseEntity<String> handleTransactionAlreadySolvedException(TransactionAlreadySolvedException exception) {

        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Insufficient funds")
    @org.springframework.web.bind.annotation.ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<String> handleInsufficientFundsException(InsufficientFundsException exception) {

        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
