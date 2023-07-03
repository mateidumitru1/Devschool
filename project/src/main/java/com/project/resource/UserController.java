package com.project.resource;

import com.project.dtos.account.AccountDtoRequest;
import com.project.dtos.user.UserDtoRequest;
import com.project.dtos.account.AccountDtoResponse;
import com.project.dtos.user.UserDtoResponse;
import com.project.handlers.JwtHandler;
import com.project.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/api/bank/users")
public class UserController {

    private JwtHandler jwtHandler;

    private final UserService userService;

    @PostMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<UserDtoResponse> save(@RequestBody @Valid UserDtoRequest userDto) {
        return new ResponseEntity<>(userService.save(userDto), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<UserDtoResponse> updateUser(@RequestHeader("Authorization") String token, @RequestBody UserDtoRequest updatedUserDto) {
        String username = jwtHandler.getSubject(token);
        return ResponseEntity.ok(userService.updateUser(username, updatedUserDto));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserByUsername(@RequestHeader("Authorization") String token) {
        String username = jwtHandler.getSubject(token);
        userService.deleteUserByUsername(username);
    }


    @PatchMapping("/account")
    public ResponseEntity<AccountDtoResponse> addAccount(@RequestHeader("Authorization") String token, @RequestBody AccountDtoRequest accountDto) {
        String username = jwtHandler.getSubject(token);
        return ResponseEntity.ok(userService.addAccount(accountDto, username));
    }

    @GetMapping("/accounts")
    public ResponseEntity<Set<AccountDtoResponse>> printAccounts(@RequestHeader("Authorization") String token) {
        String username = jwtHandler.getSubject(token);
        return ResponseEntity.ok(userService.printAccounts(username));
    }

    @DeleteMapping("/account/{accountNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccountByAccountNumber(@RequestHeader("Authorization") String token, @PathVariable String accountNumber) {
        String username = jwtHandler.getSubject(token);
        userService.deleteAccountByAccountNumber(username, accountNumber);
    }
}
