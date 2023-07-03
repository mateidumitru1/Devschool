package com.project.dtos.transaction;


import com.project.dtos.account.AccountDtoForTransaction;
import com.project.model.Status;
import com.project.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDtoResponse {

    @NotNull
    private UUID id;

    @NotNull
    private Status status;

    @NotNull
    private TransactionType transactionType;

    private AccountDtoForTransaction sender;

    private AccountDtoForTransaction receiver;

    @NotNull
    private double amount;

    @NotNull
    private LocalDate date;

    @NotNull
    private String description;
}
