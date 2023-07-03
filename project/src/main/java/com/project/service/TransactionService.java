package com.project.service;


import com.project.dtos.account.AccountDtoForTransaction;
import com.project.dtos.transaction.TransactionIncomingDto;
import com.project.dtos.transaction.TransactionOutgoingDto;
import com.project.dtos.transaction.TransactionDtoRequest;
import com.project.dtos.transaction.TransactionDtoResponse;
import com.project.exceptions.*;
import com.project.dtos.account.AccountDtoRequest;
import com.project.handlers.TransactionHandler;
import com.project.model.Account;
import com.project.model.Status;
import com.project.model.Transaction;
import com.project.model.TransactionType;
import com.project.repository.AccountRepository;
import com.project.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import kotlin.Pair;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@AllArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final TransactionHandler transactionHandler;
    private final AccountRepository accountRepository;

    @Transactional
    public TransactionDtoResponse save(String username, TransactionDtoRequest transactionDto,
                                       String senderAccountIban, String receiverAccountIban) throws AccountNotFoundException {

        if(transactionDto.getAmount() < 1) throw new NegativeAmountException("Invalid amount.");

        var accounts = accountService.findAllByUsername(username)
                .stream()
                .filter(accountDtoRequest -> accountDtoRequest.getIban().equals(senderAccountIban)
                        || accountDtoRequest.getIban().equals(receiverAccountIban))
                .toList();

        if(accounts.isEmpty()) throw new AccountNotFoundException("Account not found.");

        accounts.stream()
                .filter(account -> account.getIban().equals(senderAccountIban)
                        && account.getBalance() - transactionDto.getAmount() < 0)
                .findFirst()
                .ifPresent(a -> {throw new InsufficientFundsException("Insufficient funds.");});

        var transaction = modelMapper.map(transactionDto, Transaction.class);

        transaction.setDate(LocalDate.now());

        AccountDtoRequest myAccount;
        AccountDtoRequest senderAccount;
        AccountDtoRequest receiverAccount;

        try {
            senderAccount = accountService.getAccountByIban(senderAccountIban);
        }catch(AccountNotFoundException e)
        {
            throw new AccountNotFoundException("Sender account not found");
        }
        try{
            receiverAccount = accountService.getAccountByIban(receiverAccountIban);
        }catch (AccountNotFoundException e)
        {
            throw new AccountNotFoundException("Receiver account not found");
        }

        if(accounts.size() == 2) {
            transaction.setStatus(Status.ACCEPTED);
            transaction.setTransactionType(TransactionType.TRANSFER);

            Pair<AccountDtoRequest, AccountDtoRequest> transactionAccounts =
                    transactionHandler.executeTransaction(transactionDto, senderAccount, receiverAccount);

            senderAccount = transactionAccounts.component1();
            receiverAccount = transactionAccounts.component2();
        }
        else {
            transaction.setStatus(Status.PENDING);
            myAccount = accounts.get(0);
            if(myAccount.getIban().equals(senderAccountIban)) {

                transaction.setTransactionType(TransactionType.TRANSFER);
                if(transactionHandler.exceedsOverdraftLimit(modelMapper.map(senderAccount, Account.class),
                        modelMapper.map(receiverAccount, Account.class), transaction)) {
                    transaction.setStatus(Status.REJECTED);
                    transactionRepository.save(transaction);
                    throw new OverdraftLimitExceededException("Overdraft limit exceeded.");
                }


            }
            else transaction.setTransactionType(TransactionType.REQUEST);
        }

        var updatedSenderAccount = accountRepository.findByIban(senderAccountIban).get();
        updatedSenderAccount.setBalance(senderAccount.getBalance());

        var updatedReceiverAccount = accountRepository.findByIban(receiverAccountIban).get();
        updatedReceiverAccount.setBalance(receiverAccount.getBalance());

        updatedReceiverAccount = modelMapper.map(accountService.addIncomingTransaction(transactionDto,
                modelMapper.map(updatedReceiverAccount, AccountDtoRequest.class)), Account.class);
        updatedSenderAccount = modelMapper.map(accountService.addOutgoingTransaction(transactionDto,
                modelMapper.map(updatedSenderAccount, AccountDtoRequest.class)), Account.class);
        transaction.setSender(updatedSenderAccount);
        transaction.setReceiver(updatedReceiverAccount);

        var transactionDatabaseEntity = transactionRepository.save(transaction);

        return modelMapper.map(transactionDatabaseEntity, TransactionDtoResponse.class);
    }

    public List<TransactionOutgoingDto> getOutgoingTransactions(String username){

        var accounts = accountService.findAllByUsername(username)
                .stream()
                .map(accountDtoRequest -> modelMapper.map(accountDtoRequest, Account.class))
                .toList();

        Set<Transaction> transactions = Optional.of(accounts.stream()
                        .map(Account::getOutgoingTransactions)
                        .flatMap(Set::stream)
                        .collect(Collectors.toSet())
                )
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

        return transactions.stream()
                .map(transaction -> modelMapper.map(transaction, TransactionOutgoingDto.class))
                .toList();
    }

    public List<TransactionIncomingDto> getIncomingTransactions(String username){
        var accounts = accountService.findAllByUsername(username)
                .stream()
                .map(accountDtoRequest -> modelMapper.map(accountDtoRequest, Account.class))
                .toList();

        Set<Transaction> transactions = Optional.of(accounts.stream()
                .map(Account::getIncomingTransactions)
                .flatMap(Set::stream)
                .collect(Collectors.toSet())
                )
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

        return transactions.stream()
                .map(transaction -> modelMapper.map(transaction, TransactionIncomingDto.class))
                .toList();
    }

    public AccountDtoForTransaction acceptTransfer(String username, UUID transactionId){

        var accounts = accountService.findAllByUsername(username)
                .stream()
                .map(accountDtoRequest -> modelMapper.map(accountDtoRequest, Account.class))
                .toList();

        var transaction = accounts.stream()
                .map(Account::getIncomingTransactions)
                .flatMap(Set::stream)
                .filter(transaction1 -> transaction1.getId().equals(transactionId)
                        && transaction1.getTransactionType().equals(TransactionType.TRANSFER))
                .findFirst()
                .orElseThrow(() -> new TransactionNotFoundException("Incoming transaction not found."));

        if (!transaction.getStatus().equals(Status.PENDING))
            throw new TransactionAlreadySolvedException("Transaction already solved.");

        Account senderAccount = transaction.getSender();
        Account receiverAccount = transaction.getReceiver();

        Pair<AccountDtoRequest, AccountDtoRequest> transactionAccounts =
                transactionHandler.executeTransaction(modelMapper.map(transaction, TransactionDtoRequest.class),
                modelMapper.map(senderAccount, AccountDtoRequest.class),
                modelMapper.map(receiverAccount, AccountDtoRequest.class));

        senderAccount.setBalance(transactionAccounts.component1().getBalance());
        receiverAccount.setBalance(transactionAccounts.component2().getBalance());

        transaction.setStatus(Status.ACCEPTED);

        transactionRepository.save(transaction);

        return modelMapper.map(receiverAccount, AccountDtoForTransaction.class);
    }

    public AccountDtoForTransaction acceptRequest(String username, UUID transactionId){

        var accounts = accountService.findAllByUsername(username)
                .stream()
                .map(accountDtoRequest -> modelMapper.map(accountDtoRequest, Account.class))
                .toList();

        var transaction = accounts.stream()
                .map(Account::getOutgoingTransactions)
                .flatMap(Set::stream)
                .filter(transaction1 -> transaction1.getId().equals(transactionId)
                        && transaction1.getTransactionType().equals(TransactionType.REQUEST))
                .findFirst()
                .orElseThrow(() -> new TransactionNotFoundException("Incoming transaction not found."));

        if (!transaction.getStatus().equals(Status.PENDING))
            throw new TransactionAlreadySolvedException("Transaction already solved.");

        if(transaction.getSender().getBalance() - transaction.getAmount() < 0)
            throw new InsufficientFundsException("Insufficient funds.");

        Account senderAccount = transaction.getSender();
        Account receiverAccount = transaction.getReceiver();

        if(transactionHandler.exceedsOverdraftLimit(senderAccount, receiverAccount, transaction))
            {
                transaction.setStatus(Status.REJECTED);
                transactionRepository.save(transaction);
                throw new OverdraftLimitExceededException("Overdraft limit exceeded.");
            }

        Pair<AccountDtoRequest, AccountDtoRequest> transactionAccounts =
                transactionHandler.executeTransaction(modelMapper.map(transaction, TransactionDtoRequest.class),
                        modelMapper.map(senderAccount, AccountDtoRequest.class),
                        modelMapper.map(receiverAccount, AccountDtoRequest.class));

        senderAccount.setBalance(transactionAccounts.component1().getBalance());
        receiverAccount.setBalance(transactionAccounts.component2().getBalance());

        transaction.setStatus(Status.ACCEPTED);

        transactionRepository.save(transaction);

        return modelMapper.map(senderAccount, AccountDtoForTransaction.class);
    }

    public AccountDtoForTransaction rejectTransaction(String username, UUID transactionId) {

        var accounts = accountService.findAllByUsername(username)
                .stream()
                .map(accountDtoRequest -> modelMapper.map(accountDtoRequest, Account.class))
                .toList();

        Transaction transaction = accounts.stream()
                .flatMap(account -> Stream.concat(
                        account.getIncomingTransactions().stream(),
                        account.getOutgoingTransactions().stream()
                ))
                .filter(t -> t.getId().equals(transactionId))
                .findFirst()
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found."));

        if (!transaction.getStatus().equals(Status.PENDING))
            throw new TransactionAlreadySolvedException("Transaction already solved.");

        transaction.setStatus(Status.REJECTED);

        transactionRepository.save(transaction);

        if(transaction.getTransactionType().equals(TransactionType.REQUEST))
            return modelMapper.map(transaction.getReceiver(), AccountDtoForTransaction.class);

        return modelMapper.map(transaction.getSender(), AccountDtoForTransaction.class);
    }
}
