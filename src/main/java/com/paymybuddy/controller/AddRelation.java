package com.paymybuddy.controller;

import com.paymybuddy.model.AppUser;
import com.paymybuddy.model.dto.AddRelationDto;
import com.paymybuddy.model.dto.RegisterDto;
import com.paymybuddy.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Objects;

@Controller
public class AddRelation {

    @Autowired
    UserService userService;

    @GetMapping("/ajouter_relation")
    public String addRelation(Model model) {
        AddRelationDto addRelationDto = new AddRelationDto();
        model.addAttribute(addRelationDto);
        return "ajouter_relation";
    }

    @PostMapping("/ajouter_relation")
    @Transactional
    public String addingRelation(
            Model model,
            @Valid @ModelAttribute AddRelationDto addRelationDto,
            BindingResult result
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        AppUser currentUser = userService.getUserByEmail(currentUserEmail);

        //Utiliser un Optional plutot ?
        AppUser friendToAdd = userService.getUserByEmail(addRelationDto.getEmail());

        if (friendToAdd == null) {
            result.addError(
                    new FieldError("addRelationDto", "email",
                            "L'utilisateur n'existe pas")
            );
            return "ajouter_relation";
        }

        if (Objects.equals(currentUserEmail, addRelationDto.getEmail())) {
            result.addError(
                    new FieldError(
                            "addRelationDto", "email",
                            "Vous ne pouvez pas vous ajouter vous-meme"
                    )
            );
            return "ajouter_relation";
        }

        if (currentUser.getFriendsList().contains(friendToAdd)) {
            result.addError(
                    new FieldError("addRelationDto", "email",
                            "Cette personne est deja dans votre liste d'amis")
            );
            return "ajouter_relation";
        }

        if (result.hasErrors()) {
            return "ajouter_relation";
        }

        try {
            currentUser.getFriendsList().add(friendToAdd);
            userService.saveUser(currentUser);

            friendToAdd.getFriendsList().add(currentUser);
            userService.saveUser(friendToAdd);

            model.addAttribute("success", true);


        } catch (Exception e) {
            result.addError(
                    new FieldError("addRelationDto", "email",
                            "Erreur lors de l'ajout" + e.getMessage())
            );
        }
        return "ajouter_relation";
    }

}
