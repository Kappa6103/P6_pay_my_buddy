package com.paymybuddy.service;

import com.paymybuddy.model.Account;
import com.paymybuddy.model.AppUser;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.dto.TransactionDto;
import com.paymybuddy.model.dto.TransactionHistoryDto;
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
    AccountService accountService;

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

        transaction.setSender(transactionDto.getSenderId());
        transaction.setReceiver(receiver);

        if (!transactionDto.getDescription().isBlank()) {
            transaction.setDescription(transactionDto.getDescription());
        }

        transaction.setAmount(BigDecimal.valueOf(transactionDto.getTransactionAmount()));

        transaction = addTransaction(transaction);

        return transaction;
    }

    public void processTransaction(Transaction transaction) {
        Account senderAccount = transaction.getSender().getAccount();
        Account receiverAccount = transaction.getReceiver().getAccount();

        BigDecimal transactionValue = transaction.getAmount();

        BigDecimal initialValueOfSenderAccount = senderAccount.getBalance();
        BigDecimal initialValueOfReceiverAccount = receiverAccount.getBalance();

        senderAccount.setBalance(initialValueOfSenderAccount.subtract(transactionValue));
        receiverAccount.setBalance(initialValueOfReceiverAccount.add(transactionValue));

        accountService.addAccount(senderAccount);
        accountService.addAccount(receiverAccount);
    }

    public List<TransactionHistoryDto> getTransactionHistoryDtoByUser(AppUser appUser) {
    }
}
