package com.paymybuddy.controller;

import com.paymybuddy.model.AppUser;
import com.paymybuddy.model.dto.ModifyProfileDto;
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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        AppUser currentUser = userService.getUserByEmail(currentUserEmail);

        if (currentUser == null) {
            return "profil";
        }

        if (modifyProfileDto.getUserName().isPresent()) {
            String newUserName = modifyProfileDto.getUserName().get();
            currentUser.setUserName(newUserName);
            hasAFieldBeenModified = true;
        }

        if (modifyProfileDto.getEmail().isPresent()) {
            String newEmail = modifyProfileDto.getEmail().get();
            currentUser.setEmail(newEmail);
            hasAFieldBeenModified = true;
        }

        if (modifyProfileDto.getPassword().isPresent()) {
            currentUser.setPassword(passwordEncoder.encode(
                    modifyProfileDto.getPassword().get()
            ));
        }

        if (hasAFieldBeenModified) {
            userService.saveUser(currentUser);
        }

        return "profil";
    }
}