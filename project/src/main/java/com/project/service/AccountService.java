package com.project.service;

import com.project.dtos.account.AccountDtoRequest;
import com.project.dtos.transaction.TransactionDtoRequest;
import com.project.exceptions.AccountNotFoundException;
import com.project.exceptions.AccountNumberAlreadyExistsException;
import com.project.exceptions.IbanAlreadyExistsException;
import com.project.model.Account;
import com.project.model.Transaction;
import com.project.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private ModelMapper modelMapper;

    public void deleteByAccountNumber(String accountNumber) {

        var account = accountRepository.findByAccountNumber(accountNumber).orElseThrow();
        accountRepository.delete(account);
    }

    public AccountDtoRequest getAccountByIban(String iban) throws AccountNotFoundException {
        var account = accountRepository.findByIban(iban).orElseThrow(() -> new AccountNotFoundException("Account not found"));
        return modelMapper.map(account, AccountDtoRequest.class);
    }

    public void isValid(AccountDtoRequest accountDtoRequest) {

        if(accountRepository.findByAccountNumber(accountDtoRequest.getAccountNumber()).isPresent())
            throw new AccountNumberAlreadyExistsException("Account number already exists");

        if(accountRepository.findByIban(accountDtoRequest.getIban()).isPresent())
            throw new IbanAlreadyExistsException();
    }

    public AccountDtoRequest addOutgoingTransaction(TransactionDtoRequest transaction, AccountDtoRequest account) {

        var accountToUpdate = modelMapper.map(account, Account.class);
        var transactionToAdd = modelMapper.map(transaction, Transaction.class);
        accountToUpdate.addOutgoingTransaction(transactionToAdd);
        return modelMapper.map(accountToUpdate, AccountDtoRequest.class);
    }

    public AccountDtoRequest addIncomingTransaction(TransactionDtoRequest transaction, AccountDtoRequest account) {

        var accountToUpdate = modelMapper.map(account, Account.class);
        var transactionToAdd = modelMapper.map(transaction, Transaction.class);
        accountToUpdate.addIncomingTransaction(transactionToAdd);
        return modelMapper.map(accountToUpdate, AccountDtoRequest.class);
    }

    public List<AccountDtoRequest> findAllByUsername(String username) {

        return accountRepository.findAllByUsername(username)
                .orElseThrow(() -> new AccountNotFoundException("Account not found."))
                .stream()
                .map(a1 -> modelMapper.map(a1, AccountDtoRequest.class)).toList();
    }

}
