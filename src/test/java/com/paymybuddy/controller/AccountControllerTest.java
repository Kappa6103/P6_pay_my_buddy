package com.paymybuddy.controller;

import com.paymybuddy.model.dto.RegisterDto;
import org.junit.jupiter.api.Test;
import com.paymybuddy.service.UserService;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest(controllers = AccountController.class)
class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    private final String USER_EMAIL = "emailfor@auth.com";

    private final String USERNAME_FIELD = "USERNAME FIELD";
    private final String EMAIL_FIELD = "test@example.com";
    private final String PASSWORD_FIELD = "password";
    private final String EMPTY_FIELD = "";

    @Test
    void register_shouldReturnProfileViewWithModel() throws Exception {
        //Arrange
        RegisterDto registerDto = new RegisterDto();
        when(userService.loadRegisterDto()).thenReturn(registerDto);

        //Act & Assert
        mockMvc.perform(get("/inscription")
                .with(user(USER_EMAIL))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("inscription"))
                .andExpect(model(). attributeExists("registerDto"))
                .andExpect(model().attribute("success", false)
        );
    }

    @Test
    void testRegister_withAllFieldValid_shouldCreateNewUser() throws Exception{
        //Arrange
        when(userService.verifyPresenceOfEmailInDDB(EMAIL_FIELD)).thenReturn(false);
        ArgumentCaptor<RegisterDto> registerDtoArgumentCaptor =
                ArgumentCaptor.forClass(RegisterDto.class);

        //Act & Assert
        mockMvc.perform(post("/inscription")
                .with(user(USER_EMAIL))
                .with(csrf())
                .param("userName", USERNAME_FIELD)
                .param("email", EMAIL_FIELD)
                .param("password", PASSWORD_FIELD))
                .andExpect(status().isOk())
                .andExpect(view().name("inscription"))
                .andExpect(model().attribute("success", true))
                .andExpect(model().hasNoErrors());

        verify(userService).createNewUser(registerDtoArgumentCaptor.capture());
        RegisterDto capturedDto = registerDtoArgumentCaptor.getValue();

        assertEquals(USERNAME_FIELD, capturedDto.getUserName());
        assertEquals(EMAIL_FIELD,capturedDto.getEmail());
        assertEquals(PASSWORD_FIELD, capturedDto.getPassword());
    }

    @Test
    void testRegister_withEmailFieldAlreadyTaken_shouldNotCreateNewUser() throws Exception {
        //Arrange
        when(userService.verifyPresenceOfEmailInDDB(EMAIL_FIELD)).thenReturn(true);

        //Act & Assert
        mockMvc.perform(post("/inscription")
                        .with(user(USER_EMAIL))
                        .with(csrf())
                        .param("userName", USERNAME_FIELD)
                        .param("email", EMAIL_FIELD)
                        .param("password", PASSWORD_FIELD))
                .andExpect(status().isOk())
                .andExpect(view().name("inscription"))
                .andExpect(model().attribute("success", false))
                .andExpect(model().attributeHasFieldErrors("registerDto", "email"));

        verify(userService, never()).createNewUser(any(RegisterDto.class));
    }

    @Test
    void testRegister_withEmptyFieldsValues_shouldNotCreateNewUser() throws Exception {
        //Arrange
        when(userService.verifyPresenceOfEmailInDDB(anyString())).thenReturn(false);

        //Act & Assert
        mockMvc.perform(post("/inscription")
                        .with(user(USER_EMAIL))
                        .with(csrf())
                        .param("userName", EMPTY_FIELD)
                        .param("email", EMPTY_FIELD)
                        .param("password", EMPTY_FIELD))
                .andExpect(status().isOk())
                .andExpect(view().name("inscription"))
                .andExpect(model().hasErrors())
                .andExpect(model().attribute("success", false));

        verify(userService, never()).createNewUser(any(RegisterDto.class));
    }
}