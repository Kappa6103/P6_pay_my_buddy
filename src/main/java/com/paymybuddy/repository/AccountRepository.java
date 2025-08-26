package com.paymybuddy.repository;

import com.paymybuddy.model.Account;
import com.paymybuddy.model.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends CrudRepository<Account, Integer> {

    public Optional<Account> findByUser(AppUser user);

}
