package com.paymybuddy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AddRelation {

    @GetMapping("/ajouter_relation")
    public String addRelation() {
        return "ajouter_relation";
    }
}
