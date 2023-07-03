package com.project.handlers;

import com.project.dtos.account.AccountDtoRequest;
import com.project.dtos.transaction.TransactionDtoRequest;
import com.project.model.Account;
import com.project.model.Transaction;
import kotlin.Pair;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@AllArgsConstructor
@Service
public class TransactionHandler {

    public Pair<AccountDtoRequest, AccountDtoRequest> executeTransaction(TransactionDtoRequest transactionDtoRequest,
                                                                         AccountDtoRequest senderAccount,
                                                                         AccountDtoRequest receiverAccount){

        var convertTo = receiverAccount.getCurrency();
        var convertFrom = senderAccount.getCurrency();
        var result = transactionDtoRequest.getAmount();
        if(!convertTo.equals(convertFrom)) {
            String url = "https://api.exchangerate.host/latest?base=" + convertFrom.toUpperCase();

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            String stringResponse = response.getBody();

            JSONObject jsonObject = new JSONObject(stringResponse);
            JSONObject ratesObject = jsonObject.getJSONObject("rates");
            var rate = ratesObject.getBigDecimal(convertTo.toUpperCase());

            result = rate.multiply(BigDecimal.valueOf(transactionDtoRequest.getAmount())).doubleValue();
        }
        senderAccount.setBalance(senderAccount.getBalance() - transactionDtoRequest.getAmount());
        receiverAccount.setBalance(receiverAccount.getBalance() + result);
        return new Pair<>(senderAccount, receiverAccount);
    }

    public boolean exceedsOverdraftLimit(Account senderAccount, Account receiverAccount, Transaction transaction)
    {
        var convertTo = receiverAccount.getCurrency();
        var convertFrom = senderAccount.getCurrency();
        var overdraftLimit = senderAccount.getOverdraftLimit();
        var amount = transaction.getAmount();
        if(!convertTo.equals(convertFrom))
        {
            String url = "https://api.exchangerate.host/latest?base=" + convertFrom.toUpperCase();

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            String stringResponse = response.getBody();

            JSONObject jsonObject = new JSONObject(stringResponse);
            JSONObject ratesObject = jsonObject.getJSONObject("rates");
            var rate = ratesObject.getBigDecimal(convertTo.toUpperCase());

            overdraftLimit = rate.multiply(BigDecimal.valueOf(senderAccount.getOverdraftLimit())).doubleValue();
            amount = rate.multiply(BigDecimal.valueOf(transaction.getAmount())).doubleValue();
        }

        if(overdraftLimit <= amount)
            return true;
        return false;
    }

}
