package com.example.service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Account registerAccount(Account account) {
        return accountRepository.save(account);
    }

    public Optional<Account> findByUsername(String username) {
        return Optional.ofNullable(accountRepository.findByUsername(username));
    }

    public Optional<Account> findById(Integer accountId) {
        return accountRepository.findById(accountId);
    }
}
