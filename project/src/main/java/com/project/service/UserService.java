package com.project.service;

import com.project.dtos.account.AccountDtoRequest;
import com.project.dtos.user.UserDtoRequest;
import com.project.dtos.account.AccountDtoResponse;
import com.project.dtos.user.UserDtoResponse;
import com.project.exceptions.*;
import com.project.handlers.CurrencyHandler;
import com.project.model.Account;
import com.project.model.Address;
import com.project.model.User;
import com.project.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    private final AccountService accountService;

    private final CurrencyHandler currencyHandler;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .authorities("app")
                .build();
    }

    public UserDtoResponse save(UserDtoRequest userDtoRequest) {

        userRepository.findByUsername(userDtoRequest.getUsername())
                .ifPresent(user -> {
                    throw new UserAlreadyExistsException("User already exists");
                });

        var user = modelMapper.map(userDtoRequest, User.class);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        var userDatabaseEntity = userRepository.save(user);

        return modelMapper.map(userDatabaseEntity, UserDtoResponse.class);
    }

    public boolean hasUser(String username) {

        if(userRepository.findByUsername(username).isEmpty()) throw new UserNotFoundException("User not found.");

        return userRepository.findByUsername(username).isPresent();
    }

    public UserDtoResponse updateUser(String username, UserDtoRequest updatedUserDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String authenticatedUsername = authentication.getName();

        if (!username.equals(authenticatedUsername)) {
            throw new AccessDeniedException("You are not authorized to update this user.");
        }

        User existingUser = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found."));

        updateFieldIfNotEmpty(updatedUserDto.getFirstName(), existingUser::setFirstName);
        updateFieldIfNotEmpty(updatedUserDto.getLastName(), existingUser::setLastName);
        updateFieldIfNotEmpty(updatedUserDto.getEmail(), existingUser::setEmail);
        updateFieldIfNotEmpty(updatedUserDto.getPhoneNumber(), existingUser::setPhoneNumber);
        updateFieldIfNotEmpty(updatedUserDto.getAddress(), e -> existingUser.setAddress(modelMapper.map(e, Address.class)));

        User updatedUser = userRepository.save(existingUser);

        return modelMapper.map(updatedUser, UserDtoResponse.class);
    }

    public void deleteUserByUsername(String username) {

        var user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found."));

        userRepository.delete(user);
    }

    public AccountDtoResponse addAccount(AccountDtoRequest accountDtoRequest, String username) {

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        if(!currencyHandler.isValidCurrency(accountDtoRequest.getCurrency()))
            throw new InvalidCurrencyException("Invalid currency.");

        accountService.isValid(accountDtoRequest);

        if(accountDtoRequest.getBalance() < 0)
            throw new InvalidBalanceException("Invalid balance.");

        if(accountDtoRequest.getOverdraftLimit() < 0)
            throw new InvalidOverdraftLimitException("Invalid overdraft limit.");

        var account = modelMapper.map(accountDtoRequest, Account.class);

        user.addAccount(account);

        userRepository.save(user);

        return modelMapper.map(account, AccountDtoResponse.class);
    }

    public Set<AccountDtoResponse> printAccounts(String username) {

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        Set<Account> accountSet = user.getAccounts();

        return accountSet
                .stream()
                .map(account -> modelMapper.map(account, AccountDtoResponse.class))
                .collect(Collectors.toSet());
    }

    public void deleteAccountByAccountNumber(String username, String accountNumber) throws UserNotFoundException, AccountNotFoundException {

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        if (user.getAccounts().stream().noneMatch(account -> account.getAccountNumber().equals(accountNumber))) {
            throw new AccountNotFoundException("Account not found");
        }

        user.removeAccount(accountNumber);

        accountService.deleteByAccountNumber(accountNumber);
    }

    private <T> void updateFieldIfNotEmpty(T value, Consumer<T> setter) {

        if (value != null && !value.toString().isEmpty()) {
            setter.accept(value);
        }
    }
}
