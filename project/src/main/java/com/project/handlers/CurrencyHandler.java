package com.project.handlers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Currency;

@AllArgsConstructor
@Service
public class CurrencyHandler {

    public boolean isValidCurrency(String currency) {

        try {
            Currency.getInstance(currency);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
