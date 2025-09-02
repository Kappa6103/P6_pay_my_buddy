package com.paymybuddy.service;

import com.paymybuddy.model.AppUser;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.dto.TransactionDto;
import com.paymybuddy.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
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

    public List<Transaction> getTransactionsByUser(AppUser sender) {
        return transactionRepository.findAllBySender(sender);
    }

    public void deleteTransaction(Transaction transaction) {
        transactionRepository.delete(transaction);
    }

    //TODO : how can i make it fail ?
    @Transactional
    public Transaction createTransaction(@Valid TransactionDto transactionDto) {
        Transaction transaction = new Transaction();
        //TODO : ASK YANNICK, IS IT THE CORRECT WAY TO DO IT ? OR SHOULD BE INCLUDED IN THE DTO ?
        transaction.setSender(userService.getCurrentUser());

        Optional<AppUser> optReceiver = userService.getUserById(transactionDto.getReceiverId());
        if (optReceiver.isPresent()) {
            AppUser receiver = optReceiver.get();
            transaction.setReceiver(receiver);
        } else { //TODO : SAME HERE, IS IT THE RIGHT WAY TO DO IT ?
            throw new RuntimeException();
        }

        if (!transactionDto.getDescription().isBlank()) {
            transaction.setDescription(transactionDto.getDescription());
        }

        transaction.setAmount(BigDecimal.valueOf(transactionDto.getTransactionAmount()));

        transaction = addTransaction(transaction);

        return transaction;
    }

    public void processTransaction(Transaction transaction) {
        AppUser sender = transaction.getSender();
        AppUser receiver = transaction.getReceiver();

        BigDecimal transactionValue = transaction.getAmount();

        BigDecimal initialValueOfSenderAccount = sender.getAccount().getBalance();
        BigDecimal initialValueOfReceiverAccount = receiver.getAccount().getBalance();

        sender.getAccount().setBalance(initialValueOfSenderAccount.subtract(transactionValue));
        receiver.getAccount().setBalance(initialValueOfReceiverAccount.add(transactionValue));

        userService.saveUser(sender);
        userService.saveUser(receiver);
    }
}
