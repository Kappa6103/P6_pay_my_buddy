package com.paymybuddy.controller;

import com.paymybuddy.model.AppUser;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.dto.RegisterDto;
import com.paymybuddy.model.dto.TransactionDto;
import com.paymybuddy.model.dto.TransactionHistoryDto;
import com.paymybuddy.service.TransactionService;
import com.paymybuddy.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class TransferController {

    @Autowired
    UserService userService;

    @Autowired
    TransactionService transactionService;

    //TODO : ASK YANNICK, J'ENVOI AVEC SENDERID
    @GetMapping("/transfert")
    public String transfer(Model model) {
        TransactionDto transactionDto = userService.loadTransactionDto();
        model.addAttribute("transactionDto", transactionDto);

        return "/transfert";
    }

    @PostMapping("/transfert")
    public String receiveTransaction(
            Model model,
            @Valid @ModelAttribute TransactionDto transactionDto,
            BindingResult result
    ) {
        AppUser sender = userService.getUserById(transactionDto.getSenderId());
        AppUser receiver = userService.getUserById(transactionDto.getReceiverId());
        Transaction transaction = transactionService.createTransaction(transactionDto, sender, receiver);
        transactionService.processTransaction(transaction);
        //TODO : display should say when the transaction was successful

        model.addAttribute("transactionDto", userService.loadTransactionDto());
        model.addAttribute("success", true);
        return "transfert";
    }
}
