package com.paymybuddy.repository;

import com.paymybuddy.model.AppUser;
import com.paymybuddy.model.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Integer> {

    List<Transaction> findAllBySender(AppUser sender);
}
