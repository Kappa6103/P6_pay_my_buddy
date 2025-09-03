package com.paymybuddy.controller;

import com.paymybuddy.model.dto.AddRelationDto;
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

import java.util.Objects;

@Controller
public class AddRelationController {

    @Autowired
    UserService userService;

    //TODO: should add an attribute for succes/failure of the form
    @GetMapping("/ajouter_relation")
    public String addRelation(Model model) {
        AddRelationDto addRelationDto = userService.loadAddRelationDto();
        model.addAttribute(addRelationDto);
        return "ajouter_relation";
    }

    @PostMapping("/ajouter_relation")
    public String addingRelation(
            Model model,
            @Valid @ModelAttribute AddRelationDto addRelationDto,
            BindingResult result
    ) {

        boolean isFriendToAddPresentInDDB = userService.verifyPresenceOfEmailInDDB(addRelationDto.getEmail());

        if (!isFriendToAddPresentInDDB) {
            result.addError(
                    new FieldError("addRelationDto", "email",
                            "L'utilisateur n'existe pas")
            );
            return "ajouter_relation";
        }

        if (Objects.equals(addRelationDto.getCurrentUserEmail(), addRelationDto.getEmail())) {
            result.addError(
                    new FieldError(
                            "addRelationDto", "email",
                            "Vous ne pouvez pas vous ajouter vous-meme"
                    )
            );
            return "ajouter_relation";
        }

        boolean isFriendToAddAlreadyInFriendList = userService.verifyPresenceOfFriendToAddInUserFriendList(
                addRelationDto.getCurrentUserId(),
                addRelationDto.getEmail());

        if (isFriendToAddAlreadyInFriendList) {
            result.addError(
                    new FieldError("addRelationDto", "email",
                            "Cette personne est deja dans votre liste d'amis")
            );
            return "ajouter_relation";
        }

        if (result.hasErrors()) {
            return "ajouter_relation";
        }

        //TODO: inject new dto
        userService.createUserRelation(addRelationDto);
        model.addAttribute("success", true);

        return "ajouter_relation";
    }

}
