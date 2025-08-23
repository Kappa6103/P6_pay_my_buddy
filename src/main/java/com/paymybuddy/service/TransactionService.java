package com.paymybuddy.service;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    public Transaction addTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Optional<Transaction> getTransaction(int id) {
        return transactionRepository.findById(id);
    }

    public void deleteTransaction(Transaction transaction) {
        transactionRepository.delete(transaction);
    }
}
