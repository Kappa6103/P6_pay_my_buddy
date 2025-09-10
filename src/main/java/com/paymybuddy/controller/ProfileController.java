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
        model.addAttribute(modifyProfileDto);
        model.addAttribute("success", false);
        return "profil";
    }

    //TODO : CHECK BLANK VALUE HERE, NOT IN SERVICE
    @PostMapping("/profil")
    public String changeProfileInfo(
            Model model,
            @Valid @ModelAttribute ModifyProfileDto modifyProfileDto,
            BindingResult result
    ) {
        //TODO : the probleme is here, have to set the fields value to null where appropriate
        boolean hasAFieldBeenModified = false;

        if (modifyProfileDto.getUserName() != null && !modifyProfileDto.getUserName().isBlank()) {
            hasAFieldBeenModified = true;
        }

        if (modifyProfileDto.getEmail() != null && !modifyProfileDto.getEmail().isBlank()) {
            hasAFieldBeenModified = true;
        }

        if (modifyProfileDto.getPassword() != null && !modifyProfileDto.getPassword().isBlank()) {
            if (modifyProfileDto.getPassword().length() < 6 ) {
                result.addError(
                        new FieldError("modifyProfileDto", "email",
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
            model.addAttribute("success", true);
        } else {
            result.addError(
                    new ObjectError("modifyProfileDto",
                            "No value entered")
            );
        }

        //model.addAttribute("modifyProfileDto", userService.loadModifyProfileDto());

        return "profil";
    }
}