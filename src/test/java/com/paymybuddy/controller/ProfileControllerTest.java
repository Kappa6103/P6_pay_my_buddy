package com.paymybuddy.controller;

import com.paymybuddy.model.dto.ModifyProfileDto;
import com.paymybuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;


@WebMvcTest(controllers = ProfileController.class)
class ProfileControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    private final String USER_EMAIL = "emailfor@auth.com";

    private final int CURRENT_USER_ID_FIELD = 24;
    private final String USERNAME_FIELD = "USERNAME FIELD";
    private final String EMAIL_FIELD = "test@example.com";
    private final String PASSWORD_FIELD = "password";
    private final String EMPTY_FIELD = "";

    @Test
    void profile_shouldReturnProfileViewWithModel() throws Exception {
        //Arrange
        ModifyProfileDto modifyProfileDto = new ModifyProfileDto(CURRENT_USER_ID_FIELD);
        when(userService.loadModifyProfileDto()).thenReturn(modifyProfileDto);

        //Act & Assert
        mockMvc.perform(get("/profil")
                .with(user(USER_EMAIL))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("profil"))
                .andExpect(model().attributeExists("modifyProfileDto"))
                .andExpect(model().attribute("success", false));
    }

    @Test
    void changeProfileInfo_withAllFieldsEmpty_shouldNotUpdateProfile() throws Exception {
        //Act & Assert
        MvcResult result = mockMvc.perform(post("/profil")
                        .with(user(USER_EMAIL))
                        .with(csrf())
                        .param("currentUserId", String.valueOf(CURRENT_USER_ID_FIELD))
                        .param("userName", EMPTY_FIELD)
                        .param("email", EMPTY_FIELD)
                        .param("password", EMPTY_FIELD))
                .andExpect(status().isOk())
                .andExpect(view().name("profil"))
                .andExpect(model().attribute("success", false))
                .andExpect(model().attributeHasErrors("modifyProfileDto"))
                .andReturn();

        verify(userService, never()).modifyUserProfile(any(ModifyProfileDto.class));

        ModifyProfileDto processedDto = (ModifyProfileDto) result.getModelAndView()
                .getModel().get("modifyProfileDto");

        assertNull(processedDto.getUserName());
        assertNull(processedDto.getEmail());
        assertNull(processedDto.getPassword());
    }

    @Test
    void changeProfileInfo_withInvalidEmail_shouldReturnValidationError() throws Exception {
        mockMvc.perform(post("/profil")
                        .with(user(USER_EMAIL))
                        .with(csrf())
                        .param("currentUserId", String.valueOf(CURRENT_USER_ID_FIELD))
                        .param("userName", EMPTY_FIELD)
                        .param("email", "invalid-email")
                        .param("password", EMPTY_FIELD))
                .andExpect(status().isOk())
                .andExpect(view().name("profil"))
                .andExpect(model().hasErrors());

        verify(userService,never()).modifyUserProfile(any(ModifyProfileDto.class));
    }

    @Test
    void changeProfileInfo_withShortPassword_shouldReturnError() throws Exception {
        mockMvc.perform(post("/profil")
                        .with(user(USER_EMAIL))
                        .with(csrf())
                        .param("currentUserId", String.valueOf(CURRENT_USER_ID_FIELD))
                        .param("userName",EMPTY_FIELD)
                        .param("email",EMAIL_FIELD)
                        .param("password", "12345"))
                .andExpect(status().isOk())
                .andExpect(view().name("profil"))
                .andExpect(model().attributeHasFieldErrors("modifyProfileDto", "password"));
        verify(userService,never()).modifyUserProfile(any(ModifyProfileDto.class));
    }

    @Test
    void changeProfileInfo_withAllFieldsValid_shouldUpdateProfile() throws Exception {
        //Arrange
        ArgumentCaptor<ModifyProfileDto> modifyProfileDtoArgumentCaptor = ArgumentCaptor
                .forClass(ModifyProfileDto.class);

        //Act & Assert
        mockMvc.perform(post("/profil")
                        .with(user(USER_EMAIL))
                        .with(csrf())
                        .param("currentUserId", String.valueOf(CURRENT_USER_ID_FIELD))
                        .param("userName", USERNAME_FIELD)
                        .param("email", EMAIL_FIELD)
                        .param("password", PASSWORD_FIELD))
                .andExpect(status().isOk())
                .andExpect(view().name("profil"))
                .andExpect(model().attribute("success", true));

        verify(userService,times(1)).modifyUserProfile(modifyProfileDtoArgumentCaptor.capture());

        ModifyProfileDto capturedModifyProfileDto = modifyProfileDtoArgumentCaptor.getValue();
        assertEquals(CURRENT_USER_ID_FIELD, capturedModifyProfileDto.getCurrentUserId());
        assertEquals(USERNAME_FIELD, capturedModifyProfileDto.getUserName());
        assertEquals(EMAIL_FIELD, capturedModifyProfileDto.getEmail());
        assertEquals(PASSWORD_FIELD, capturedModifyProfileDto.getPassword());
    }

}