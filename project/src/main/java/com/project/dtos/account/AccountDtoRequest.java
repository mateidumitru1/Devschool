package com.project.dtos.account;

import com.project.dtos.user.UserDtoRequest;
import com.project.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDtoRequest {

    @NotBlank
    private UUID id;

    @NotBlank
    private double balance;

    private double overdraftLimit;

    @NotBlank
    private String currency;

    @NotBlank
    private UserDtoRequest user;

    @NotBlank
    private String accountNumber;

    @NotBlank
    private String iban;

    private Set<Transaction> outgoingTransactions;

    private Set<Transaction> incomingTransactions;
}
