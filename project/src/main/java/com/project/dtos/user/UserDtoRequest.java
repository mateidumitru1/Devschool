package com.project.dtos.user;

import com.project.dtos.address.AddressDto;
import com.project.dtos.account.AccountDtoRequest;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoRequest {

    @NotBlank
    private UUID id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String email;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private AddressDto address;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private Set<AccountDtoRequest> accountSet = new HashSet<>();
}
