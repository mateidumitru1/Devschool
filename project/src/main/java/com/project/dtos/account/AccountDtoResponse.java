package com.project.dtos.account;

import com.project.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDtoResponse {

    @NotBlank
    private double balance;

    private double overdraftLimit;

    @NotBlank
    private String currency;

    @NotBlank
    private String accountNumber;

    @NotBlank
    private String iban;

    private Set<Transaction> outgoingTransactions = null;

    private Set<Transaction> incomingTransactions = null;

}
