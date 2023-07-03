package com.project.repository;

import com.project.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber")
    Optional<Account> findByAccountNumber(@Param("accountNumber") String accountNumber);
    Optional<Account> findByIban(String iban);

    @Query("SELECT a FROM Account a JOIN a.user u where u.username = :username")
    Optional<List<Account>> findAllByUsername(@Param("username") String username);
}
