package com.paymybuddy.controller;

import com.paymybuddy.model.dto.TransactionDto;
import com.paymybuddy.service.TransactionService;
import org.junit.jupiter.api.Test;

import com.paymybuddy.configuration.SpringSecurityConfig;
import com.paymybuddy.model.dto.RegisterDto;
import org.junit.jupiter.api.Test;
import com.paymybuddy.service.UserService;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TransferController.class)
@Import(SpringSecurityConfig.class)
class TransferControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    @MockitoBean
    TransactionService transactionService;

    @MockitoBean
    PasswordEncoder passwordEncoder;

    private final String USER_EMAIL_AUTH = "emailfor@auth.com";

    @Test
    void transfer_shouldReturnProfileViewWithModel() throws Exception {
        //Arrange
        TransactionDto transactionDto = new TransactionDto();
        when(userService.loadTransactionDto()).thenReturn(transactionDto);

        //Act & Assert
        mockMvc.perform(get("/transfert")
                        .with(user(USER_EMAIL_AUTH))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("transfert"))
                .andExpect(model().attributeExists("transactionDto"))
                .andExpect(model().attribute("success", false))
        ;
    }

    @Test
    void transfer_userNotLoggedIn_shouldFail() throws Exception {
        //Act & Assert
        mockMvc.perform(get("/transfert")
                        .with(anonymous())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
        ;
    }

    @Test
    void receiveTransaction() {
    }
}