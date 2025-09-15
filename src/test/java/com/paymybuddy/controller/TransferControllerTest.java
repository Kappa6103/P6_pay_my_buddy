package com.paymybuddy.controller;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.dto.AppUserNameAndId;
import com.paymybuddy.model.dto.TransactionDto;
import com.paymybuddy.model.dto.TransactionHistoryDto;
import com.paymybuddy.service.TransactionService;
import org.junit.jupiter.api.Test;

import com.paymybuddy.configuration.SpringSecurityConfig;
import com.paymybuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

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

    private final int SENDER_ID = 23;
    private final int RECEIVER_ID = 12;
    private final int TRANSACTION_AMOUNT = 15;
    private final int TRANSACTION_AMOUNT_ZERO = 0;
    private final String DESCRIPTION = "description test";
    private final String EMPTY_DESCRIPTION = "";
    private final List<AppUserNameAndId> APP_USERNAME_AND_ID_EMPTYLIST = Collections.emptyList();
    private final List<TransactionHistoryDto> TRANSACTION_HISTORY_DTO_EMPTYLIST = Collections.emptyList();


    @Test
    void transfer_userLoggedIn_shouldReturnProfileViewWithModel() throws Exception {
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
    void receiveTransaction_emptyAppUserList_emptyTransactionHistory_allFieldsValid() throws Exception {
        //Arrange
        TransactionDto initialDto = new TransactionDto();
        initialDto.setSenderId(SENDER_ID);
        initialDto.setRelationList(APP_USERNAME_AND_ID_EMPTYLIST);
        initialDto.setTransactionHistory(TRANSACTION_HISTORY_DTO_EMPTYLIST);
        when(userService.loadTransactionDto()).thenReturn(initialDto);

        TransactionDto expectedDto = new TransactionDto();
        expectedDto.setSenderId(SENDER_ID);
        expectedDto.setReceiverId(RECEIVER_ID);
        expectedDto.setDescription(DESCRIPTION);
        expectedDto.setTransactionAmount(TRANSACTION_AMOUNT);

        Transaction transaction = new Transaction();
        when(transactionService.createTransaction(any(TransactionDto.class))).thenReturn(transaction);

        //Act & Assert
        mockMvc.perform(post("/transfert")
                        .with(user(USER_EMAIL_AUTH))
                        .with(csrf())
                        .param("senderId", String.valueOf(SENDER_ID))
                        .param("receiverId", String.valueOf(RECEIVER_ID))
                        .param("description", DESCRIPTION)
                        .param("transactionAmount", String.valueOf(TRANSACTION_AMOUNT)))
                .andExpect(status().isOk())
                .andExpect(view().name("transfert"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attribute("success", true));

        verify(transactionService).createTransaction(argThat(dto -> 
        dto.getSenderId().equals(SENDER_ID) &&
        dto.getReceiverId().equals(RECEIVER_ID) &&
        dto.getDescription().equals(DESCRIPTION) &&
        dto.getTransactionAmount().equals(TRANSACTION_AMOUNT)
    ));

    verify(transactionService, times(1)).processTransaction(transaction);

    }

    @Test
    void receiveTransaction_emptyAppUserList_emptyTransactionHistory_transactionAmountEqualZero() throws Exception {
        //Arrange
        TransactionDto initialDto = new TransactionDto();
        initialDto.setSenderId(SENDER_ID);
        initialDto.setRelationList(APP_USERNAME_AND_ID_EMPTYLIST);
        initialDto.setTransactionHistory(TRANSACTION_HISTORY_DTO_EMPTYLIST);
        when(userService.loadTransactionDto()).thenReturn(initialDto);

        //Act & Assert
        mockMvc.perform(post("/transfert")
                        .with(user(USER_EMAIL_AUTH))
                        .with(csrf())
                        .param("senderId", String.valueOf(SENDER_ID))
                        .param("receiverId", String.valueOf(RECEIVER_ID))
                        .param("description", DESCRIPTION)
                        .param("transactionAmount", String.valueOf(TRANSACTION_AMOUNT_ZERO)))
                .andExpect(status().isOk())
                .andExpect(view().name("transfert"))
                .andExpect(model().attributeHasFieldErrors("transactionDto", "transactionAmount"))
                .andExpect(model().attribute("success", false));

        verify(transactionService, never()).createTransaction(any(TransactionDto.class));
        verify(transactionService, never()).processTransaction(any(Transaction.class));

    }

    @Test
    void receiveTransaction_emptyAppUserList_emptyTransactionHistory_allFieldsInvalid() throws Exception {
        //Arrange
        TransactionDto initialDto = new TransactionDto();
        initialDto.setSenderId(SENDER_ID);
        initialDto.setRelationList(APP_USERNAME_AND_ID_EMPTYLIST);
        initialDto.setTransactionHistory(TRANSACTION_HISTORY_DTO_EMPTYLIST);
        when(userService.loadTransactionDto()).thenReturn(initialDto);

        //Act & Assert
        mockMvc.perform(post("/transfert")
                        .with(user(USER_EMAIL_AUTH))
                        .with(csrf())
                        .param("senderId", "")
                        .param("receiverId", "")
                        .param("description", DESCRIPTION)
                        .param("transactionAmount", String.valueOf(TRANSACTION_AMOUNT)))
                .andExpect(status().isOk())
                .andExpect(view().name("transfert"))
                .andExpect(model().attributeHasErrors("transactionDto"))
                .andExpect(model().attribute("success", false));

        verify(transactionService, never()).createTransaction(any(TransactionDto.class));
        verify(transactionService, never()).processTransaction(any(Transaction.class));

    }
}