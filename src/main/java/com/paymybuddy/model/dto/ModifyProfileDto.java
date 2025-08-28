package com.paymybuddy.model.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.Optional;

public class ModifyProfileDto {

    @Nullable
    private String userName;

    @Nullable
    @Email
    private String email;

    @Nullable
    @Size(min = 6, message = "Minimum Password length is 6 characters")
    private String password;

    public Optional<String> getUserName() {
        return Optional.ofNullable(userName);
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Optional<String> getPassword() {
        return Optional.ofNullable(password);
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
