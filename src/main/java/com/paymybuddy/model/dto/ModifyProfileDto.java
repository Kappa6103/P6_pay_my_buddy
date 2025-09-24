package com.paymybuddy.model.dto;

import jakarta.validation.constraints.Email;

public class ModifyProfileDto {

    private int currentUserId;

    private String currentUserName;

    private String currentEmail;

    private String userName;

    @Email
    private String email;

    private String password;

    public ModifyProfileDto(int currentUserId, String currentUserName, String currentEmail) {
        this.currentUserId = currentUserId;
        this.currentUserName = currentUserName;
        this.currentEmail = currentEmail;
    }

    public String getCurrentUserName() {
        return currentUserName;
    }

    public String getCurrentEmail() {
        return currentEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCurrentUserId() {
        return currentUserId;
    }
}
