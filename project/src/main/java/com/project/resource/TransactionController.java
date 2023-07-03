package com.project.resource;

import com.project.dtos.account.AccountDtoForTransaction;
import com.project.dtos.transaction.TransactionIncomingDto;
import com.project.dtos.transaction.TransactionOutgoingDto;
import com.project.dtos.transaction.TransactionDtoRequest;
import com.project.exceptions.InsufficientFundsException;
import com.project.handlers.JwtHandler;
import com.project.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.exceptions.AccountNotFoundException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/bank/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    private final JwtHandler jwtHandler;

    @PostMapping("/{senderIban}/{receiverIban}")
    public ResponseEntity<?> save(@RequestHeader("Authorization") String token, @RequestBody @Valid TransactionDtoRequest transactionDto,
                                               @PathVariable String senderIban,
                                               @PathVariable String receiverIban) throws AccountNotFoundException {

        var username = jwtHandler.getSubject(token);

        try {
            return new ResponseEntity<>(transactionService.save(username, transactionDto, senderIban, receiverIban), HttpStatus.CREATED);
        } catch (AccountNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }catch(InsufficientFundsException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/outgoingTransactions")
    public ResponseEntity<List<TransactionOutgoingDto>> getOutgoingTransactions(@RequestHeader("Authorization") String token) {

        String username = jwtHandler.getSubject(token);
        return new ResponseEntity<>(transactionService.getOutgoingTransactions(username), HttpStatus.OK);
    }

    @GetMapping("/incomingTransactions")
    public ResponseEntity<List<TransactionIncomingDto>> getIncomingTransactions(@RequestHeader("Authorization") String token) {

        String username = jwtHandler.getSubject(token);
        return new ResponseEntity<>(transactionService.getIncomingTransactions(username), HttpStatus.OK);
    }

    @PatchMapping("/accept/transfer")
    public ResponseEntity<AccountDtoForTransaction> acceptTransfer(@RequestHeader("Authorization") String token,
                                                                   @RequestBody UUID transactionId) {

        String username = jwtHandler.getSubject(token);
        return new ResponseEntity<>(transactionService.acceptTransfer(username, transactionId), HttpStatus.OK);
    }

    @PatchMapping("/accept/request")
    public ResponseEntity<AccountDtoForTransaction> acceptRequest(@RequestHeader("Authorization") String token,
                                                                  @RequestBody UUID transactionId){

        String username = jwtHandler.getSubject(token);
        return new ResponseEntity<>(transactionService.acceptRequest(username, transactionId), HttpStatus.OK);
    }

    @PatchMapping("/reject")
    public ResponseEntity<AccountDtoForTransaction> rejectIncomingTransaction(@RequestHeader("Authorization") String token,
                                                                              @RequestBody UUID transactionId){

        String username = jwtHandler.getSubject(token);
        return new ResponseEntity<>(transactionService.rejectTransaction(username, transactionId), HttpStatus.OK);
    }
}
