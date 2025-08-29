package com.paymybuddy.controller;

import com.paymybuddy.model.AppUser;
import com.paymybuddy.model.dto.ModifyProfileDto;
import com.paymybuddy.model.dto.RegisterDto;
import com.paymybuddy.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProfilController {

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/profil")
    public String profil(Model model) {
        ModifyProfileDto modifyProfileDto = new ModifyProfileDto();
        model.addAttribute(modifyProfileDto);
        model.addAttribute("success", false);
        return "profil";
    }

    @PostMapping("/profil")
    @Transactional
    public String changeProfileInfo(
            Model model,
            @Valid @ModelAttribute ModifyProfileDto modifyProfileDto,
            BindingResult result
    ) {

        boolean hasAFieldBeenModified = false;
        //TODO: TO REFACTOR TO USE THE SERVICE METHOD
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        AppUser currentUser = userService.getUserByEmail(currentUserEmail);

        if (currentUser == null) {
            return "profil";
        }

        if (modifyProfileDto.getUserName() != null && !modifyProfileDto.getUserName().isBlank()) {
            currentUser.setUserName(modifyProfileDto.getUserName());
            hasAFieldBeenModified = true;
        }

        if (modifyProfileDto.getEmail() != null && !modifyProfileDto.getEmail().isBlank()) {
            currentUser.setEmail(modifyProfileDto.getEmail());
            hasAFieldBeenModified = true;
        }

        if (modifyProfileDto.getPassword() != null && !modifyProfileDto.getPassword().isBlank()) {
            currentUser.setPassword(passwordEncoder.encode(modifyProfileDto.getPassword()));
            hasAFieldBeenModified = true;
        }

        if (hasAFieldBeenModified) {
            userService.saveUser(currentUser);
        }
        //TODO: clearing the DTO
        model.addAttribute("modifyProfileDto", new ModifyProfileDto());
        model.addAttribute("success", true);
        return "profil";
    }
}