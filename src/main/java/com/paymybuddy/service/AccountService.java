package com.paymybuddy.service;

import com.paymybuddy.model.Account;
import com.paymybuddy.model.AppUser;
import com.paymybuddy.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    AccountRepository accountRepository;

    public Account addAccount(Account account) {
        return accountRepository.save(account);
    }

    public Optional<Account> getAccountByUser(AppUser user) {
        return accountRepository.findByUser(user);
    }

    public void deleteAccount(Account account) {
        accountRepository.delete(account);
    }

}
