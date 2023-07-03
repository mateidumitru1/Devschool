package com.project.dtos.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDtoForTransaction {

    @NotBlank
    private UUID id;

    @NotBlank
    private double balance;

    private double overdraftLimit;

    @NotBlank
    private String currency;

    @NotBlank
    private String accountNumber;

    @NotBlank
    private String iban;

}
