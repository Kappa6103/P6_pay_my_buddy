package com.paymybuddy.controller;


import com.paymybuddy.model.dto.ModifyProfileDto;
import com.paymybuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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

    private static final String USER_EMAIL = "test@example.com";

    @Test
    void profile_shouldReturnProfileViewWithModel() throws Exception {
        //Arrange
        ModifyProfileDto modifyProfileDto = new ModifyProfileDto(1);
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
    void changeProfileInfo_withValidData_shouldUpdateProfile() throws Exception {
        // Arrange
        ModifyProfileDto returnedDto = new ModifyProfileDto(1);
        when(userService.loadModifyProfileDto()).thenReturn(returnedDto);

        // Act & Assert
        mockMvc.perform(post("/profil")
                        .with(user(USER_EMAIL))
                        .with(csrf())
                        .param("currentUserId", "1")
                        .param("userName", "")
                        .param("email", "")
                        .param("password", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("profil"))
                .andExpect(model().attribute("success", true));

        verify(userService,times(0)).modifyUserProfile(any(ModifyProfileDto.class));
    }



}