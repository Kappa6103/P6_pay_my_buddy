package com.paymybuddy.model.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class ModifyProfileDto {

    @Nullable
    private String userName;

    @Nullable
    @Email
    private String email;
    //TODO : sometime the form ask for an input, correct behavior is blank is ok
    @Nullable
    @Size(min = 6, message = "Minimum Password length is 6 characters")
    private String password;

    @Nullable
    public String getUserName() {
        return userName;
    }

    public void setUserName(@Nullable String userName) {
        this.userName = userName;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(@Nullable String email) {
        this.email = email;
    }

    @Nullable
    public String getPassword() {
        return password;
    }

    public void setPassword(@Nullable String password) {
        this.password = password;
    }
}
