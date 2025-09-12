package com.paymybuddy.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public class AddRelationDto {

    @Email
    @NotEmpty
    private String email;

    private int currentUserId;

    private String currentUserEmail;

    public  AddRelationDto(int currentUserId, String currentUserEmail) {
        this.currentUserId = currentUserId;
        this.currentUserEmail = currentUserEmail;
    }

    public void setCurrentUserId(int currentUserId) {
        this.currentUserId = currentUserId;
    }

    public void setCurrentUserEmail(String currentUserEmail) {
        this.currentUserEmail = currentUserEmail;
    }

    public String getCurrentUserEmail() {
        return currentUserEmail;
    }

    public int getCurrentUserId() {
        return currentUserId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
