package com.paymybuddy.controller;

import com.paymybuddy.configuration.SpringSecurityConfig;
import com.paymybuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = LoginController.class)
@Import(SpringSecurityConfig.class)
class LoginControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    @MockitoBean
    PasswordEncoder passwordEncoder;

    @Test
    void login() throws Exception {
        //Act & Assert
        mockMvc.perform(get("/connexion")
                        .with(csrf())
                        .with(anonymous()))
                .andExpect(status().isOk())
                .andExpect(view().name("connexion"));
    }
}