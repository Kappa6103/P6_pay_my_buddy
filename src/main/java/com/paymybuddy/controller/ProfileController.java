package com.paymybuddy.controller;

import com.paymybuddy.model.dto.ModifyProfileDto;
import com.paymybuddy.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProfileController {

    @Autowired
    UserService userService;

    @GetMapping("/profil")
    public String profile(Model model) {
        ModifyProfileDto modifyProfileDto = userService.loadModifyProfileDto();
        model.addAttribute("modifyProfileDto", modifyProfileDto);
        model.addAttribute("success", false);
        return "profil";
    }

    @PostMapping("/profil")
    public String changeProfileInfo(
            Model model,
            @Valid @ModelAttribute ModifyProfileDto modifyProfileDto,
            BindingResult result
    ) {
        boolean hasAFieldBeenModified = false;

        modifyProfileDto = inputFieldsNullSetter(modifyProfileDto);

        if (modifyProfileDto.getUserName() != null) {
            hasAFieldBeenModified = true;
        }

        if (modifyProfileDto.getEmail() != null) {
            hasAFieldBeenModified = true;
        }

        if (modifyProfileDto.getPassword() != null) {
            if (modifyProfileDto.getPassword().length() < 6 ) {
                result.addError(
                        new FieldError("modifyProfileDto", "password",
                                "Minimum Password length is 6 characters")
                );
            }
            hasAFieldBeenModified = true;
        }

        if (result.hasErrors()) {
            return "profil";
        }

        if (hasAFieldBeenModified) {
            userService.modifyUserProfile(modifyProfileDto);
            //model.addAttribute("modifyProfileDto", userService.loadModifyProfileDto());
            model.addAttribute("success", true);
        } else {
            result.addError(
                    new ObjectError("modifyProfileDto",
                            "No value entered")
            );
            model.addAttribute("success", false);
        }

        return "profil";
    }

    private ModifyProfileDto inputFieldsNullSetter(ModifyProfileDto modifyProfileDto) {
        final String userNameField = modifyProfileDto.getUserName().trim();
        final String emailField = modifyProfileDto.getEmail().trim();
        final String passwordField = modifyProfileDto.getPassword().trim();

        if (userNameField.isBlank()) {
            modifyProfileDto.setUserName(null);
        }
        if (emailField.isBlank()) {
            modifyProfileDto.setEmail(null);
        }
        if (passwordField.isBlank()) {
            modifyProfileDto.setPassword(null);
        }
        return modifyProfileDto;
    }
}