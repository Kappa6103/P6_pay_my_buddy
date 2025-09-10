package com.paymybuddy.service;

import com.paymybuddy.model.Account;
import com.paymybuddy.model.AppUser;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.dto.AddRelationDto;
import com.paymybuddy.model.dto.ModifyProfileDto;
import com.paymybuddy.model.dto.RegisterDto;
import com.paymybuddy.model.dto.TransactionDto;
import com.paymybuddy.repository.AccountRepository;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    AccountRepository accountRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    Authentication authentication;

    @Mock
    SecurityContext securityContext;

    @InjectMocks
    UserService userService;

    private final String USER_USERNAME = "appUser username";
    private final String USER_EMAIL = "appUserTest@gmail.com";
    private final String USER_PWD = "testpwd";
    private final String USER_NAME_FRIEND_ONE = "usernameFriendOne";
    private final String USER_NAME_FRIEND_TWO = "usernameFriendTwo";
    private final String EMAIL_FRIEND_ONE = "email@friendone.com";
    private final String TRANSACTION_ONE_DESCRIPTION = "transaction one description";
    private final String TRANSACTION_TWO_DESCRIPTION = "transaction two description";
    private final int USER_ID = 23;
    private final int USER_ID_FRIEND_ONE = 1;
    private final int USER_ID_FRIEND_TWO = 2;
    private final BigDecimal TRANSACTION_ONE_AMOUNT = new BigDecimal("5");
    private final BigDecimal TRANSACTION_TWO_AMOUNT = new BigDecimal("3");
    private AddRelationDto addRelationDto;
    private ModifyProfileDto modifyProfileDto;
    private AppUser appUser;
    private AppUser friendOne;
    private AppUser friendTwo;
    private Transaction transactionOne;
    private Transaction transactionTwo;
    private List<AppUser> friendsList;
    private List<Transaction> transactionsList;

    @BeforeEach
    void setUpPerTest() {
        appUser = new AppUser();
        appUser.setEmail(USER_EMAIL);
        appUser.setPassword(USER_PWD);
        appUser.setUserId(USER_ID);
        appUser.setUserName(USER_USERNAME);

        friendOne = new AppUser();
        friendOne.setUserId(USER_ID_FRIEND_ONE);
        friendOne.setUserName(USER_NAME_FRIEND_ONE);

        friendTwo = new AppUser();
        friendTwo.setUserId(USER_ID_FRIEND_TWO);
        friendTwo.setUserName(USER_NAME_FRIEND_TWO);

        friendsList = List.of(friendOne, friendTwo);

        transactionOne = new Transaction();
        transactionOne.setReceiver(friendOne);
        transactionOne.setDescription(TRANSACTION_ONE_DESCRIPTION);
        transactionOne.setAmount(TRANSACTION_ONE_AMOUNT);

        transactionTwo = new Transaction();
        transactionTwo.setReceiver(friendTwo);
        transactionTwo.setDescription(TRANSACTION_TWO_DESCRIPTION);
        transactionTwo.setAmount(TRANSACTION_TWO_AMOUNT);

        transactionsList = List.of(transactionOne, transactionTwo);

        addRelationDto = new AddRelationDto(USER_ID, USER_EMAIL);
        addRelationDto.setEmail(EMAIL_FRIEND_ONE);

        modifyProfileDto = new ModifyProfileDto(USER_ID);

    }

    @Test
    void loadRegisterDto_successfulLoading() {
        //Act
        RegisterDto result = userService.loadRegisterDto();

        //Assert
        assertNotNull(result);
        assertTrue(result instanceof RegisterDto);
    }

    @Test
    void loadUserByUsername_successful() {
        //Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(appUser));

        //Act
        UserDetails result = userService.loadUserByUsername(USER_EMAIL);

        //Assert
        assertNotNull(result);
        assertEquals(appUser.getEmail(), result.getUsername());
        assertEquals(appUser.getPassword(), result.getPassword());
        assertTrue(result.getAuthorities().contains(
                new SimpleGrantedAuthority("ROLE_USER")));

        verify(userRepository).findByEmail(USER_EMAIL);
    }

    @Test
    void loadUserByUsername_failedDBFetch() {
        //Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(USER_EMAIL);
        });
    }

    @Test
    void getCurrentUser_successful() {
        //Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getName()).thenReturn(USER_EMAIL);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(appUser));

        //Act
        AppUser result = userService.getCurrentUser();

        //Assert
        assertNotNull(result);
        assertEquals(USER_EMAIL, result.getEmail());
        assertEquals(USER_PWD, result.getPassword());

        //Clean up
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUser_failedDBFetch() {
        //Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getName()).thenReturn(USER_EMAIL);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.getCurrentUser();
        });

        //Clean up
        SecurityContextHolder.clearContext();
    }

    @Test
    void loadTransactionDto_friendListIsEmpty_transactionListIsEmpty_successful() {
        //Arrange
        appUser.setFriendsList(Collections.emptyList());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(USER_EMAIL);
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(appUser));

        when(transactionRepository.findAllBySender(appUser)).thenReturn(Collections.emptyList());

        //Act
        TransactionDto result = userService.loadTransactionDto();

        //Assert
        assertNotNull(result);
        assertTrue(result.getRelationList().isEmpty());
        assertTrue(result.getTransactionHistory().isEmpty());
        assertEquals(USER_ID, result.getSenderId());
    }

    @Test
    void loadTransactionDto_friendListIsNotEmpty_transactionListIsEmpty_successful() {
        //Arrange
        appUser.setFriendsList(friendsList);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(USER_EMAIL);
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(appUser));

        when(transactionRepository.findAllBySender(appUser)).thenReturn(Collections.emptyList());

        //Act
        TransactionDto result = userService.loadTransactionDto();

        //Assert
        assertNotNull(result);
        assertFalse(result.getRelationList().isEmpty());
        assertEquals(friendsList.size(), result.getRelationList().size());
        assertEquals(USER_ID_FRIEND_ONE,result.getRelationList().getFirst().userId());
        assertEquals(USER_ID_FRIEND_TWO, result.getRelationList().getLast().userId());
        assertEquals(USER_NAME_FRIEND_ONE, result.getRelationList().getFirst().userName());
        assertEquals(USER_NAME_FRIEND_TWO, result.getRelationList().getLast().userName());
        assertTrue(result.getTransactionHistory().isEmpty());
        assertEquals(USER_ID, result.getSenderId());
    }

    @Test
    void loadTransactionDto_friendListIsEmpty_transactionListIsNotEmpty_successful() {
        //Arrange
        appUser.setFriendsList(Collections.emptyList());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(USER_EMAIL);
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(appUser));

        when(transactionRepository.findAllBySender(appUser)).thenReturn(transactionsList);

        //Act
        TransactionDto result = userService.loadTransactionDto();

        //Assert
        assertNotNull(result);
        assertFalse(result.getTransactionHistory().isEmpty());
        assertEquals(transactionsList.size(), result.getTransactionHistory().size());
        assertEquals(USER_NAME_FRIEND_ONE, result.getTransactionHistory().getFirst().userName());
        assertEquals(USER_NAME_FRIEND_TWO, result.getTransactionHistory().getLast().userName());
        assertEquals(TRANSACTION_ONE_DESCRIPTION, result.getTransactionHistory().getFirst().description());
        assertEquals(TRANSACTION_TWO_DESCRIPTION, result.getTransactionHistory().getLast().description());
        assertEquals(TRANSACTION_ONE_AMOUNT.intValue(), result.getTransactionHistory().getFirst().transactionAmount());
        assertEquals(TRANSACTION_TWO_AMOUNT.intValue(), result.getTransactionHistory().getLast().transactionAmount());
        assertTrue(result.getRelationList().isEmpty());
        assertEquals(USER_ID, result.getSenderId());
    }

    @Test
    void verifyPresenceOfEmailInDDB_emailIsPresent() {
        //Arrange
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(appUser));

        //Act
        boolean result = userService.verifyPresenceOfEmailInDDB(USER_EMAIL);

        //Assert
        assertTrue(result);
    }

    @Test
    void verifyPresenceOfEmailInDDB_emailIsNotPresent() {
        //Arrange
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

        //Act
        boolean result = userService.verifyPresenceOfEmailInDDB(USER_EMAIL);

        //Assert
        assertFalse(result);
    }

    @Test
    void createNewUser_successful() {
        //Arrange
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUserName(USER_USERNAME);
        registerDto.setEmail(USER_EMAIL);
        registerDto.setPassword(USER_PWD);
        final String encodedPwd = "encoded pwd";

        when(passwordEncoder.encode(registerDto.getPassword())).thenReturn(encodedPwd);
        when(userRepository.save(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser savedUser = invocation.getArgument(0);
            savedUser.setUserId(USER_ID);
            return savedUser;
        });

        //Act
        userService.createNewUser(registerDto);

        // Assert
        ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);

        verify(userRepository, times(2)).save(userCaptor.capture());
        verify(accountRepository).save(accountCaptor.capture());
        verify(passwordEncoder).encode(registerDto.getPassword());

        // Verify user properties
        AppUser capturedUser = userCaptor.getValue();
        assertEquals(registerDto.getUserName(), capturedUser.getUserName());
        assertEquals(registerDto.getEmail(), capturedUser.getEmail());
        assertEquals(encodedPwd, capturedUser.getPassword());

        // Verify account properties
        Account capturedAccount = accountCaptor.getValue();
        assertEquals(BigDecimal.valueOf(100.0), capturedAccount.getBalance());
        assertEquals(capturedUser, capturedAccount.getUser());
    }

    @Test
    void loadAddRelationDto() {
        //Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(USER_EMAIL);
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(appUser));

        //Act
        AddRelationDto result = userService.loadAddRelationDto();

        //Assert
        assertNotNull(result);
        assertNull(result.getEmail());
        assertEquals(USER_ID, result.getCurrentUserId());
        assertEquals(USER_EMAIL, result.getCurrentUserEmail());
    }

    @Test
    void verifyPresenceOfFriendToAddInUserFriendList_friendPresentInList() {
        //Arrange
        appUser.setFriendsList(friendsList);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(appUser));
        when(userRepository.findByEmail(EMAIL_FRIEND_ONE)).thenReturn(Optional.of(friendOne));

        //Act
        boolean result = userService.verifyPresenceOfFriendToAddInUserFriendList(USER_ID, EMAIL_FRIEND_ONE);

        //Assert
        assertTrue(result);
    }

    @Test
    void verifyPresenceOfFriendToAddInUserFriendList_friendNotPresentInList() {
        //Arrange
        AppUser friendThree = new AppUser();
        appUser.setFriendsList(friendsList);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(appUser));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(friendThree));

        //Act
        boolean result = userService.verifyPresenceOfFriendToAddInUserFriendList(USER_ID, EMAIL_FRIEND_ONE);

        //Assert
        assertFalse(result);
    }

    @Test
    void verifyPresenceOfFriendToAddInUserFriendList_friendPresentInList_failedDBFetch_currentUser() {
        //Arrange
        appUser.setFriendsList(friendsList);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(EMAIL_FRIEND_ONE)).thenReturn(Optional.of(friendOne));

        //Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.verifyPresenceOfFriendToAddInUserFriendList(USER_ID, EMAIL_FRIEND_ONE);
        });
    }

    @Test
    void verifyPresenceOfFriendToAddInUserFriendList_friendPresentInList_failedDBFetch_friendToAdd() {
        //Arrange
        appUser.setFriendsList(friendsList);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(appUser));
        when(userRepository.findByEmail(EMAIL_FRIEND_ONE)).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.verifyPresenceOfFriendToAddInUserFriendList(USER_ID, EMAIL_FRIEND_ONE);
        });
    }

    @Test
    void createUserRelation_successful() {
        //Arrange
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(appUser));
        when(userRepository.findByEmail(EMAIL_FRIEND_ONE)).thenReturn(Optional.of(friendOne));
        ArgumentCaptor<AppUser> appUserArgumentCaptor = ArgumentCaptor.forClass(AppUser.class);

        //Act
        userService.createUserRelation(addRelationDto);

        //Assert
        verify(userRepository, times(2)).save(appUserArgumentCaptor.capture());

        List<AppUser> capturedUsers = appUserArgumentCaptor.getAllValues();
        assertEquals(2, capturedUsers.size());

        AppUser capturedCurrentUser = capturedUsers.getFirst();
        AppUser capturedFriendToAdd = capturedUsers.getLast();

        assertSame(appUser, capturedCurrentUser);
        assertSame(friendOne, capturedFriendToAdd);

        assertTrue(capturedCurrentUser.getFriendsList().contains(capturedFriendToAdd));
        assertTrue(capturedFriendToAdd.getFriendsList().contains(capturedCurrentUser));
    }

    @Test
    void createUserRelation_failedDBFetch_currentUser() {
        //Arrange
        appUser.setFriendsList(friendsList);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(EMAIL_FRIEND_ONE)).thenReturn(Optional.of(friendOne));

        //Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.createUserRelation(addRelationDto);
        });
    }

    @Test
    void createUserRelation_failedDBFetch_friendToAdd() {
        //Arrange
        appUser.setFriendsList(friendsList);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(appUser));
        when(userRepository.findByEmail(EMAIL_FRIEND_ONE)).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.createUserRelation(addRelationDto);
        });
    }

    @Test
    void loadModifyProfileDto() {
        //Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(USER_EMAIL);
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(appUser));

        //Act
        ModifyProfileDto result = userService.loadModifyProfileDto();

        //Assert
        assertNotNull(result);
        assertEquals(USER_ID, result.getCurrentUserId());
    }

    @Test
    void modifyUserProfile_successful_nothingToModify() {
        //Arrange
        when(userRepository.findById(modifyProfileDto.getCurrentUserId())).thenReturn(Optional.of(appUser));
        modifyProfileDto.setUserName(null);
        modifyProfileDto.setEmail(null);
        modifyProfileDto.setPassword(null);
        ArgumentCaptor<AppUser> appUserArgumentCaptor = ArgumentCaptor.forClass(AppUser.class);

        //Act
        userService.modifyUserProfile(modifyProfileDto);

        //Assert
        verify(userRepository).save(appUserArgumentCaptor.capture());

        AppUser capturedUser = appUserArgumentCaptor.getValue();

        assertEquals(USER_ID, capturedUser.getUserId());
        assertEquals(USER_USERNAME, capturedUser.getUserName());
        assertEquals(USER_EMAIL, capturedUser.getEmail());
        assertEquals(USER_PWD, capturedUser.getPassword());
    }

    @Test
    void modifyUserProfile_successful_usernameToModify() {
        //Arrange
        final String modifiedUsername = "usernameToChange";
        when(userRepository.findById(modifyProfileDto.getCurrentUserId())).thenReturn(Optional.of(appUser));
        modifyProfileDto.setUserName(modifiedUsername);
        modifyProfileDto.setEmail(null);
        modifyProfileDto.setPassword(null);
        ArgumentCaptor<AppUser> appUserArgumentCaptor = ArgumentCaptor.forClass(AppUser.class);

        //Act
        userService.modifyUserProfile(modifyProfileDto);

        //Assert
        verify(userRepository).save(appUserArgumentCaptor.capture());

        AppUser capturedUser = appUserArgumentCaptor.getValue();

        assertEquals(USER_ID, capturedUser.getUserId());
        assertEquals(modifiedUsername, capturedUser.getUserName());
        assertEquals(USER_EMAIL, capturedUser.getEmail());
        assertEquals(USER_PWD, capturedUser.getPassword());
    }

    @Test
    void modifyUserProfile_successful_emailToModify() {
        //Arrange
        final String modifiedEmail = "emailToChange";
        when(userRepository.findById(modifyProfileDto.getCurrentUserId())).thenReturn(Optional.of(appUser));
        modifyProfileDto.setUserName(null);
        modifyProfileDto.setEmail(modifiedEmail);
        modifyProfileDto.setPassword(null);
        ArgumentCaptor<AppUser> appUserArgumentCaptor = ArgumentCaptor.forClass(AppUser.class);

        //Act
        userService.modifyUserProfile(modifyProfileDto);

        //Assert
        verify(userRepository).save(appUserArgumentCaptor.capture());

        AppUser capturedUser = appUserArgumentCaptor.getValue();

        assertEquals(USER_ID, capturedUser.getUserId());
        assertEquals(USER_USERNAME, capturedUser.getUserName());
        assertEquals(modifiedEmail, capturedUser.getEmail());
        assertEquals(USER_PWD, capturedUser.getPassword());
    }

    @Test
    void modifyUserProfile_successful_passwordToModify() {
        //Arrange
        final String modifiedPassword = "passwordToChange";
        when(userRepository.findById(modifyProfileDto.getCurrentUserId())).thenReturn(Optional.of(appUser));
        modifyProfileDto.setUserName(null);
        modifyProfileDto.setEmail(null);
        modifyProfileDto.setPassword(modifiedPassword);
        when(passwordEncoder.encode(modifyProfileDto.getPassword())).thenReturn(modifiedPassword);
        ArgumentCaptor<AppUser> appUserArgumentCaptor = ArgumentCaptor.forClass(AppUser.class);

        //Act
        userService.modifyUserProfile(modifyProfileDto);

        //Assert
        verify(userRepository).save(appUserArgumentCaptor.capture());

        AppUser capturedUser = appUserArgumentCaptor.getValue();

        assertEquals(USER_ID, capturedUser.getUserId());
        assertEquals(USER_USERNAME, capturedUser.getUserName());
        assertEquals(USER_EMAIL, capturedUser.getEmail());
        assertEquals(modifiedPassword, capturedUser.getPassword());
    }

    @Test
    void modifyUserProfile_successful_allFieldsToModify() {
        //Arrange
        final String modifiedPassword = "passwordToChange";
        final String modifiedEmail = "emailToChange";
        final String modifiedUsername = "usernameToChange";
        when(userRepository.findById(modifyProfileDto.getCurrentUserId())).thenReturn(Optional.of(appUser));
        modifyProfileDto.setUserName(modifiedUsername);
        modifyProfileDto.setEmail(modifiedEmail);
        modifyProfileDto.setPassword(modifiedPassword);
        when(passwordEncoder.encode(modifyProfileDto.getPassword())).thenReturn(modifiedPassword);
        ArgumentCaptor<AppUser> appUserArgumentCaptor = ArgumentCaptor.forClass(AppUser.class);

        //Act
        userService.modifyUserProfile(modifyProfileDto);

        //Assert
        verify(userRepository).save(appUserArgumentCaptor.capture());

        AppUser capturedUser = appUserArgumentCaptor.getValue();

        assertEquals(USER_ID, capturedUser.getUserId());
        assertEquals(modifiedUsername, capturedUser.getUserName());
        assertEquals(modifiedEmail, capturedUser.getEmail());
        assertEquals(modifiedPassword, capturedUser.getPassword());
    }

    @Test
    void modifyUserProfile_failedDBFetch() {
        //Arrange
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.modifyUserProfile(modifyProfileDto);
        });
    }
}