package com.paymybuddy.service;

import com.paymybuddy.model.Account;
import com.paymybuddy.model.AppUser;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.dto.*;
import com.paymybuddy.repository.AccountRepository;
import com.paymybuddy.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionService transactionService;

    public RegisterDto loadRegisterDto() {
        return new RegisterDto();
    }

    public AppUser saveUser(AppUser user) {
        return userRepository.save(user);
    }

    public Optional<AppUser> getUserById(int id) {
        return userRepository.findById(id);
    }

    public void deleteUser(AppUser user) {
        userRepository.delete(user);
    }

    public void deleteUserById(int userId) {
        userRepository.deleteById(userId);
    }

    public AppUser getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = getUserByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles("USER") //TODO: Roles should be an Enum type
                .build();
    }

    public AppUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return getUserByEmail(authentication.getName());
    }


    public TransactionDto loadTransactionDto() {
        TransactionDto transactionDto = new TransactionDto();
        AppUser appUser = getCurrentUser();
        transactionDto.setSenderId(appUser.getUserId());

        //TODO : IS IT OK, ASK YANNICK, have to deal with null value for field receiverId when friendList is empty
        if (appUser.getFriendsList().isEmpty()) {
            transactionDto.setRelationList(Collections.emptyList());
        } else {
            List<AppUserNameAndId> appUserNameAndIdList = new ArrayList<>(appUser.getFriendsList().size());
            for (AppUser user : appUser.getFriendsList()) {
                AppUserNameAndId appUserNameAndId = new AppUserNameAndId(user.getUserName(), user.getUserId());
                appUserNameAndIdList.add(appUserNameAndId);
            }
            transactionDto.setRelationList(appUserNameAndIdList);
        }
        List<Transaction> transactionList = transactionService.getTransactionsByUser(appUser);

        List<TransactionHistoryDto> transactionHistoryDtoList = new ArrayList<>(transactionList.size());
      for (Transaction transaction : transactionList) {
          transactionHistoryDtoList.add(new TransactionHistoryDto(
                  transaction.getReceiver().getUserName(),
                  transaction.getDescription(),
                  transaction.getAmount().intValue()
                  )
          );
      }

      transactionDto.setTransactionHistory(transactionHistoryDtoList);

        return transactionDto;
    }

    public boolean verifyPresenceOfEmailInDDB(String email) {
        AppUser appUser = userRepository.findByEmail(email);
        if (appUser == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean verifyPresenceOfUserToAdd(String email) {
        AppUser appUser = userRepository.findByEmail(email);
        if (appUser == null) {
            return false;
        } else {
            return true;
        }
    }

    //TODO : refactor this method, too ugly to stay
    @Transactional
    public void createNewUser(@Valid RegisterDto registerDto) {
        AppUser newUser = new AppUser();
        newUser.setUserName(registerDto.getUserName());
        newUser.setEmail(registerDto.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        newUser = userRepository.save(newUser);

        Account newAccount = new Account();

        newAccount.setUser(newUser);

        userRepository.save(newUser);

        newAccount.setBalance(BigDecimal.valueOf(100.0));

        accountRepository.save(newAccount);
    }

    public AddRelationDto loadAddRelationDto() {
        AppUser appUser = getCurrentUser();
        return new AddRelationDto(appUser.getUserId(), appUser.getEmail());
    }

    //TODO : ASK YANNICK comment gere les nulls, isEmpty() here
    public boolean verifyPresenceOfFriendToAddInUserFriendList(int currentUserId, String email) {
        Optional<AppUser> optCurrentUser = userRepository.findById(currentUserId);
        Optional<AppUser> optFriendToAdd = userRepository.findByEmail(email);

        AppUser currentUser = null;
        AppUser friendToAdd = null;

        if (optCurrentUser.isPresent()) {
            currentUser = optCurrentUser.get();
        } else {
            throw new RuntimeException();
        }

        if (optFriendToAdd.isPresent()) {
            friendToAdd = optFriendToAdd.get();
        } else {
            throw new RuntimeException();
        }

        return currentUser.getFriendsList().contains(friendToAdd);
    }
    //TODO: NOT DRY
    @Transactional
    public void createUserRelation(@Valid AddRelationDto addRelationDto) {
        Optional<AppUser> optCurrentUser = userRepository.findById(addRelationDto.getCurrentUserId());
        Optional<AppUser> optFriendToAdd = userRepository.findByEmail(addRelationDto.getEmail());

        AppUser currentUser = null;
        AppUser friendToAdd = null;

        if (optCurrentUser.isPresent()) {
            currentUser = optCurrentUser.get();
        } else {
            throw new RuntimeException();
        }

        if (optFriendToAdd.isPresent()) {
            friendToAdd = optFriendToAdd.get();
        } else {
            throw new RuntimeException();
        }
        currentUser.getFriendsList().add(friendToAdd);
        userRepository.save(currentUser);

        friendToAdd.getFriendsList().add(currentUser);
        userRepository.save(friendToAdd);
    }

    public ModifyProfileDto loadModifyProfileDto() {
        return new ModifyProfileDto(getCurrentUser().getUserId());
    }

    public void modifyUserProfile(@Valid ModifyProfileDto modifyProfileDto) {
        Optional<AppUser> optCurrentUser = userRepository.findById(modifyProfileDto.getCurrentUserId());

        AppUser currentUser = null;

        if (optCurrentUser.isPresent()) {
            currentUser = optCurrentUser.get();
        } else {
            throw new RuntimeException();
        }

        if (modifyProfileDto.getUserName() != null
                && !modifyProfileDto.getUserName().isBlank()) {
            currentUser.setUserName(modifyProfileDto.getUserName());

        }

        if (modifyProfileDto.getEmail() != null
                && !modifyProfileDto.getEmail().isBlank()) {
            currentUser.setEmail(modifyProfileDto.getEmail());

        }

        if (modifyProfileDto.getPassword() != null
                && !modifyProfileDto.getPassword().isBlank()) {
            currentUser.setPassword(passwordEncoder.encode(modifyProfileDto.getPassword()));
        }

        userRepository.save(currentUser);

    }
}
