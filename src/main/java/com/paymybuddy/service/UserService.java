package com.paymybuddy.service;

import com.paymybuddy.model.Account;
import com.paymybuddy.model.AppUser;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.dto.*;
import com.paymybuddy.repository.AccountRepository;
import com.paymybuddy.repository.TransactionRepository;
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
    private TransactionRepository transactionRepository;

    public RegisterDto loadRegisterDto() {
        return new RegisterDto();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<AppUser> optAppUser = userRepository.findByEmail(email);

        AppUser user = null;

        if (optAppUser.isPresent()) {
            user = optAppUser.get();
        } else {
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
        Optional<AppUser> optAppUser = userRepository.findByEmail(authentication.getName());

        AppUser appUser = null;

        if (optAppUser.isPresent()) {
            appUser = optAppUser.get();
            return appUser;
        } else {
            throw new RuntimeException();
        }
    }


    public TransactionDto loadTransactionDto() {
        TransactionDto transactionDto = new TransactionDto();
        AppUser appUser = getCurrentUser();
        transactionDto.setSenderId(appUser.getUserId());

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
        List<Transaction> transactionList = transactionRepository.findAllBySender(appUser);

        List<TransactionHistoryDto> transactionHistoryDtoList = getTransactionHistoryDtos(transactionList);

        transactionDto.setTransactionHistory(transactionHistoryDtoList);

        return transactionDto;
    }

    private List<TransactionHistoryDto> getTransactionHistoryDtos(List<Transaction> transactionList) {
        List<TransactionHistoryDto> transactionHistoryDtoList;

        if (transactionList.isEmpty()) {
            transactionHistoryDtoList = Collections.emptyList();
        } else {
            transactionHistoryDtoList = new ArrayList<>();

            for (Transaction transaction : transactionList) {
                transactionHistoryDtoList.add(new TransactionHistoryDto(
                                transaction.getReceiver().getUserName(),
                                transaction.getDescription(),
                                transaction.getAmount().intValue()
                        )
                );
            }
        }
        return transactionHistoryDtoList;
    }

    public boolean verifyPresenceOfEmailInDDB(String email) {
        Optional<AppUser> optAppUser = userRepository.findByEmail(email);
        AppUser appUser = null;

        if (optAppUser.isPresent()) {
            appUser = optAppUser.get();
        }

        if (appUser == null) {
            return false;
        } else {
            return true;
        }
    }

    //TODO: REMOVE METHOD BELOW

//    public boolean verifyPresenceOfUserToAdd(String email) {
//        Optional<AppUser> optAppUser = userRepository.findByEmail(email);
//        AppUser appUser = null;
//
//        if (optAppUser.isPresent()) {
//            appUser = optAppUser.get();
//        }
//
//        if (appUser == null) {
//            return false;
//        } else {
//            return true;
//        }
//    }

    @Transactional
    public void createNewUser(RegisterDto registerDto) {
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
            return currentUser.getFriendsList().contains(friendToAdd);
        } else {
            return false;
        }
    }

    @Transactional
    public void createUserRelation(AddRelationDto addRelationDto) {
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

    @Transactional
    public void modifyUserProfile(ModifyProfileDto modifyProfileDto) {
        Optional<AppUser> optCurrentUser = userRepository.findById(modifyProfileDto.getCurrentUserId());

        AppUser currentUser = null;

        if (optCurrentUser.isPresent()) {
            currentUser = optCurrentUser.get();
        } else {
            throw new RuntimeException();
        }

        if (modifyProfileDto.getUserName() != null) {
            currentUser.setUserName(modifyProfileDto.getUserName());

        }

        if (modifyProfileDto.getEmail() != null) {
            currentUser.setEmail(modifyProfileDto.getEmail());

        }

        if (modifyProfileDto.getPassword() != null) {
            currentUser.setPassword(passwordEncoder.encode(modifyProfileDto.getPassword()));
        }

        userRepository.save(currentUser);
    }
}
