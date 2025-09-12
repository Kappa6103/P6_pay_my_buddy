package com.paymybuddy.controller;

import com.paymybuddy.model.dto.AddRelationDto;
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


@WebMvcTest(controllers = AddRelationController.class)
class AddRelationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    private final String USER_EMAIL_AUTH = "emailfor@auth.com";

    private final int APPUSER_ID = 23;
    private final String APPUSER_EMAIL = "appuser@gmail.com";
    private final String FRIEND_EMAIL = "friend@gmail.com";


    @Test
    void addRelation_shouldReturnProfileViewWithModel() throws Exception {
        //Arrange
        AddRelationDto addRelationDto = new AddRelationDto(APPUSER_ID, APPUSER_EMAIL);
        when(userService.loadAddRelationDto()).thenReturn(addRelationDto);

        //Act & Assert
        mockMvc.perform(get("/ajouter_relation")
                        .with(user(USER_EMAIL_AUTH))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("ajouter_relation"))
                .andExpect(model(). attributeExists("addRelationDto"))
                .andExpect(model().attribute("success", false)
                );
    }

    @Test
    void addingRelation_allFieldsValid_shouldCreateNewRelation() throws Exception {
        //Arrange
        when(userService.verifyPresenceOfEmailInDDB(FRIEND_EMAIL)).thenReturn(true);
        when(userService.verifyPresenceOfFriendToAddInUserFriendList(
                APPUSER_ID, FRIEND_EMAIL)).thenReturn(false);
        ArgumentCaptor<AddRelationDto> addRelationDtoArgumentCaptor =
                ArgumentCaptor.forClass(AddRelationDto.class);

        //Act & Assert
        mockMvc.perform(post("/ajouter_relation")
                        .with(user(USER_EMAIL_AUTH))
                        .with(csrf())
                        .param("email", FRIEND_EMAIL)
                        .param("currentUserId", String.valueOf(APPUSER_ID))
                        .param("currentUserEmail", APPUSER_EMAIL))
                .andExpect(status().isOk())
                .andExpect(view().name("ajouter_relation"))
                .andExpect(model().attribute("success", true))
                .andExpect(model().hasNoErrors());

        verify(userService, times(1)).createUserRelation(addRelationDtoArgumentCaptor.capture());
        AddRelationDto capturedDto = addRelationDtoArgumentCaptor.getValue();
        assertEquals(FRIEND_EMAIL, capturedDto.getEmail());
        assertEquals(APPUSER_ID, capturedDto.getCurrentUserId());
        assertEquals(APPUSER_EMAIL, capturedDto.getCurrentUserEmail());

    }

    @Test
    void addingRelation_FriendDoNotExist_shouldNotCreateNewRelation() throws Exception {
        //Arrange
        when(userService.verifyPresenceOfEmailInDDB(FRIEND_EMAIL)).thenReturn(false);
        when(userService.verifyPresenceOfFriendToAddInUserFriendList(
                APPUSER_ID, APPUSER_EMAIL)).thenReturn(false);

        //Act & Assert
        mockMvc.perform(post("/ajouter_relation")
                        .with(user(USER_EMAIL_AUTH))
                        .with(csrf())
                        .param("email", FRIEND_EMAIL)
                        .param("currentUserId", String.valueOf(APPUSER_ID))
                        .param("currentUserEmail", APPUSER_EMAIL))
                .andExpect(status().isOk())
                .andExpect(view().name("ajouter_relation"))
                .andExpect(model().attribute("success", false))
                .andExpect(model().attributeHasFieldErrors("addRelationDto", "email"));

        verify(userService, never()).createUserRelation(any(AddRelationDto.class));
    }

    @Test
    void addingRelation_emailIsThemAsSelf_shouldNotCreateNewRelation() throws Exception {
        //Arrange
        when(userService.verifyPresenceOfEmailInDDB(FRIEND_EMAIL)).thenReturn(true);
        when(userService.verifyPresenceOfFriendToAddInUserFriendList(
                APPUSER_ID, APPUSER_EMAIL)).thenReturn(false);

        //Act & Assert
        mockMvc.perform(post("/ajouter_relation")
                        .with(user(USER_EMAIL_AUTH))
                        .with(csrf())
                        .param("email", APPUSER_EMAIL)
                        .param("currentUserId", String.valueOf(APPUSER_ID))
                        .param("currentUserEmail", APPUSER_EMAIL))
                .andExpect(status().isOk())
                .andExpect(view().name("ajouter_relation"))
                .andExpect(model().attribute("success", false))
                .andExpect(model().attributeHasFieldErrors("addRelationDto", "email"));

        verify(userService, never()).createUserRelation(any(AddRelationDto.class));
    }

    @Test
    void addingRelation_friendAlreadyAdded_shouldNotCreateNewRelation() throws Exception {
        //Arrange
        when(userService.verifyPresenceOfEmailInDDB(FRIEND_EMAIL)).thenReturn(true);
        when(userService.verifyPresenceOfFriendToAddInUserFriendList(
                APPUSER_ID, FRIEND_EMAIL)).thenReturn(true);

        //Act & Assert
        mockMvc.perform(post("/ajouter_relation")
                        .with(user(USER_EMAIL_AUTH))
                        .with(csrf())
                        .param("email", FRIEND_EMAIL)
                        .param("currentUserId", String.valueOf(APPUSER_ID))
                        .param("currentUserEmail", APPUSER_EMAIL))
                .andExpect(status().isOk())
                .andExpect(view().name("ajouter_relation"))
                .andExpect(model().attribute("success", false))
                .andExpect(model().attributeHasFieldErrors("addRelationDto", "email"));

        verify(userService, never()).createUserRelation(any(AddRelationDto.class));
    }
}