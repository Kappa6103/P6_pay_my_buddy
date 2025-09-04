package com.paymybuddy.controller;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.dto.TransactionDto;
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


@Controller
public class TransferController {

    @Autowired
    UserService userService;

    @Autowired
    TransactionService transactionService;

    @GetMapping("/transfert")
    public String transfer(Model model) {
        TransactionDto transactionDto = userService.loadTransactionDto();
        model.addAttribute("transactionDto", transactionDto);
        model.addAttribute("success", false);

        return "/transfert";
    }

    @PostMapping("/transfert")
    public String receiveTransaction(
            Model model,
            @Valid @ModelAttribute TransactionDto transactionDto,
            BindingResult result
    ) {
        Transaction transaction = transactionService.createTransaction(transactionDto);
        transactionService.processTransaction(transaction);

        model.addAttribute("transactionDto", userService.loadTransactionDto());
        model.addAttribute("success", true);
        return "transfert";
    }
}
