package com.paymybuddy.controller;

import com.paymybuddy.model.dto.ModifyProfileDto;
import com.paymybuddy.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProfileController {

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/profil")
    public String profile(Model model) {
        ModifyProfileDto modifyProfileDto = userService.loadModifyProfileDto();
        model.addAttribute(modifyProfileDto);
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

        if (modifyProfileDto.getUserName() != null && !modifyProfileDto.getUserName().isBlank()) {
            hasAFieldBeenModified = true;
        }

        if (modifyProfileDto.getEmail() != null && !modifyProfileDto.getEmail().isBlank()) {
            hasAFieldBeenModified = true;
        }

        if (modifyProfileDto.getPassword() != null && !modifyProfileDto.getPassword().isBlank()) {
            hasAFieldBeenModified = true;
        }

        if (hasAFieldBeenModified) {
            userService.modifyUserProfile(modifyProfileDto);
        }

        model.addAttribute("modifyProfileDto", userService.loadModifyProfileDto());
        model.addAttribute("success", true);
        return "profil";
    }
}