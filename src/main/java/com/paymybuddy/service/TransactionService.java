package com.paymybuddy.service;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.dto.TransactionDto;
import com.paymybuddy.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    UserService userService;

    public Transaction addTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Optional<Transaction> getTransaction(int id) {
        return transactionRepository.findById(id);
    }

    public void deleteTransaction(Transaction transaction) {
        transactionRepository.delete(transaction);
    }

    @Transactional
    public boolean createTransaction(@Valid TransactionDto transactionDto) {
        Transaction transaction = new Transaction();
        //TODO : ASK YANNICK, IS IT THE CORRECT WAY TO DO IT ? OR SHOULD BE INCLUDED IN THE DTO ?
        transaction.setSender(userService.getCurrentUser());
        
        transaction.setReceiver(userService.getUserById(transactionDto.getReceiver().userId()).get());


        return false;
    }
}
