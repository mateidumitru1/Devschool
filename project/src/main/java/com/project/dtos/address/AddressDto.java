package com.project.dtos.address;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {

    @NotBlank
    private String country;

    @NotBlank
    private String county;

    @NotBlank
    private String city;

    @NotBlank
    private String street;

    @NotBlank
    private int number;
}
