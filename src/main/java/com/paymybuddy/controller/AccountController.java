package com.paymybuddy.controller;

import com.paymybuddy.model.dto.RegisterDto;
import com.paymybuddy.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class AccountController {

    @Autowired
    private UserService userService;

    @GetMapping("/inscription")
    public String register(Model model) {
        RegisterDto registerDto = userService.loadRegisterDto();
        model.addAttribute("registerDto", registerDto);
        model.addAttribute("success", false);
        return "inscription";
    }

    @PostMapping("/inscription")
    public String register(
            Model model,
            @Valid @ModelAttribute RegisterDto registerDto,
            BindingResult result
    ) {
        boolean isEmailAlreadyInDDB = userService.verifyPresenceOfEmailInDDB(registerDto.getEmail());

        if (isEmailAlreadyInDDB) {
            result.addError(
                    new FieldError("registerDto", "email",
                            "Email address is already used")
            );
        }

        if (result.hasErrors()) {
            model.addAttribute("success", false);
            return "inscription";
        }

        userService.createNewUser(registerDto);

        model.addAttribute("success", true);

        return "inscription";
    }
}
