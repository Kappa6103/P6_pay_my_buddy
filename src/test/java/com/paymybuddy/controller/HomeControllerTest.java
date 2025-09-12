package com.paymybuddy.controller;

import com.paymybuddy.configuration.SpringSecurityConfig;
import com.paymybuddy.model.dto.AddRelationDto;
import com.paymybuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest(controllers = HomeController.class)
@Import(SpringSecurityConfig.class)
class HomeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    @MockitoBean
    PasswordEncoder passwordEncoder;

    private final String USER_EMAIL_AUTH = "emailfor@auth.com";

    @Test
    void home_userLoggedIn_firstUri_shouldReturnHomePage() throws Exception {
        //Act & Assert
        mockMvc.perform(get("/")
                        .with(user(USER_EMAIL_AUTH))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void home_userLoggedIn_secondUri_shouldReturnHomePage() throws Exception {
        //Act & Assert
        mockMvc.perform(get("")
                        .with(user(USER_EMAIL_AUTH))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void home_userNotLoggedIn_firstUri_shouldReturnHomePage() throws Exception {
        //Act & Assert
        mockMvc.perform(get("/")
                        .with(csrf())
                        .with(anonymous()))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void home_userNotLoggedIn_secondUri_shouldReturnHomePage() throws Exception {
        //Act & Assert
        mockMvc.perform(get("")
                        .with(csrf())
                        .with(anonymous()))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }
    
}