package com.paymybuddy.service;

import com.paymybuddy.model.Account;
import com.paymybuddy.model.AppUser;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.dto.TransactionDto;
import com.paymybuddy.model.dto.TransactionHistoryDto;
import com.paymybuddy.repository.AccountRepository;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AccountRepository accountRepository;

    //TODO : how can i make it fail ?
    @Transactional
    public Transaction createTransaction(@Valid TransactionDto transactionDto) {
        Transaction transaction = new Transaction();

        Optional<AppUser> optSender = userRepository.findById(transactionDto.getSenderId());
        Optional<AppUser> optReceiver = userRepository.findById(transactionDto.getReceiverId());

        AppUser sender = null;
        AppUser receiver = null;

        if (optSender.isPresent()) {
            sender = optSender.get();
        } else {
            throw new RuntimeException();
        }

        if (optReceiver.isPresent()) {
            receiver = optReceiver.get();
        } else {
            throw new RuntimeException();
        }

        transaction.setSender(sender);
        transaction.setReceiver(receiver);

        if (!transactionDto.getDescription().isBlank()) {
            transaction.setDescription(transactionDto.getDescription());
        }

        transaction.setAmount(BigDecimal.valueOf(transactionDto.getTransactionAmount()));

        transaction = transactionRepository.save(transaction);

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

        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);
    }

    //TODO: FINISH THIS
    public List<TransactionHistoryDto> getTransactionHistoryDtoByUser(AppUser appUser) {
        return Collections.emptyList();
    }
}
