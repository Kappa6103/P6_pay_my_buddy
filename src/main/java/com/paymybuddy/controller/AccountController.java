package com.paymybuddy.controller;

import com.paymybuddy.model.Account;
import com.paymybuddy.model.AppUser;
import com.paymybuddy.model.dto.RegisterDto;
import com.paymybuddy.service.AccountService;
import com.paymybuddy.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.util.Date;

@Controller
public class AccountController {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/inscription")
    public String register(Model model) {
        RegisterDto registerDto = new RegisterDto();
        model.addAttribute(registerDto);
        model.addAttribute("success", false);
        return "inscription";
    }

    //TODO Pas de confirmation de mdp et a mettre dans une @Transactional service method
    @PostMapping("/inscription")
    @Transactional //jarkarta or spring framework ?
    public String register(
            Model model,
            @Valid @ModelAttribute RegisterDto registerDto,
            BindingResult result
    ) {
        AppUser appUser = userService.getUserByEmail(registerDto.getEmail());

        if (appUser != null) {
            result.addError(
                    new FieldError("registerDto", "email",
                            "Email address is already used")
            );
        }

        if (result.hasErrors()) {
            return "inscription";
        }

        try {
            //Create a new account
            AppUser newUser = new AppUser();
            newUser.setUserName(registerDto.getUserName());
            newUser.setEmail(registerDto.getEmail());
            newUser.setPassword(passwordEncoder.encode(registerDto.getPassword()));

            newUser = userService.addUser(newUser);

            Account newAccount = new Account();

            newAccount.setUser(newUser);

            userService.addUser(newUser);
            newAccount.setBalance(BigDecimal.valueOf(0.0));

            accountService.addAccount(newAccount);

            model.addAttribute("registerDto", new RegisterDto());
            model.addAttribute("success", true);

        } catch (Exception e) {
            result.addError(
                    new FieldError("registerDto", "userName", e.getMessage())
            );
        }
        return "inscription";
    }


}
