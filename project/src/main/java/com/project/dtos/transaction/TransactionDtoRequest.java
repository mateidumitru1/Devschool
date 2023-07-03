package com.project.dtos.transaction;

import com.project.dtos.account.AccountDtoRequest;
import com.project.model.Status;
import com.project.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDtoRequest {

    @NotNull
    private Status status;

    @NotNull
    private TransactionType transactionType;

    private AccountDtoRequest sender;

    private AccountDtoRequest receiver;

    @NotNull
    private double amount;

    @NotNull
    private LocalDate date;

    @NotNull
    private String description;
}
