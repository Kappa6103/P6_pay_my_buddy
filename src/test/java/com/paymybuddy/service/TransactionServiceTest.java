package com.paymybuddy.service;

import com.paymybuddy.model.Account;
import com.paymybuddy.model.AppUser;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.dto.TransactionDto;
import com.paymybuddy.repository.AccountRepository;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    AccountRepository accountRepository;

    @Mock
    AppUser mockSender;

    @Mock
    AppUser mockReceiver;

    @InjectMocks
    TransactionService transactionService;

    private AppUser senderAppUser;
    private AppUser receiverAppUser;
    private Account senderAccount;
    private Account receiverAccount;
    private TransactionDto transactionDto;
    private Transaction transaction;

    private final int SENDER_ID = 1;
    private final int RECEIVER_ID = 2;

    @BeforeEach
    void setUpPerTest() {
        senderAppUser = new AppUser();
        senderAppUser.setUserId(SENDER_ID);
        senderAppUser.setEmail("sender@gmail.com");

        receiverAppUser = new AppUser();
        receiverAppUser.setUserId(RECEIVER_ID);
        receiverAppUser.setEmail("receiver@gmail.com");

        senderAccount = new Account();
        senderAccount.setUserId(SENDER_ID);
        senderAccount.setUser(senderAppUser);
        senderAccount.setBalance(new BigDecimal("500"));

        receiverAccount = new Account();
        receiverAccount.setUserId(RECEIVER_ID);
        receiverAccount.setUser(receiverAppUser);
        receiverAccount.setBalance(new BigDecimal("500"));

        transactionDto = new TransactionDto();
        transactionDto.setSenderId(SENDER_ID);
        transactionDto.setReceiverId(RECEIVER_ID);


        transaction = new Transaction();
        transaction.setSender(senderAppUser);
        transaction.setReceiver(receiverAppUser);

    }

    @Test
    void createTransaction_successfulCreation_withDescription() {
        //Arrange
        transactionDto.setDescription("Test transaction");
        transactionDto.setTransactionAmount(100);

        Transaction expectedTransaction = new Transaction();
        expectedTransaction.setTransactionId(1);
        expectedTransaction.setAmount(new BigDecimal("100"));
        expectedTransaction.setDescription("Test transaction");
        expectedTransaction.setSender(senderAppUser);
        expectedTransaction.setReceiver(receiverAppUser);

        when(userRepository.findById(SENDER_ID)).thenReturn(Optional.of(senderAppUser));
        when(userRepository.findById(RECEIVER_ID)).thenReturn(Optional.of(receiverAppUser));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(
                invocation -> {
            Transaction savedTransaction = invocation.getArgument(0);
            savedTransaction.setTransactionId(1);
            return savedTransaction;
        });


        //Act
        Transaction result = transactionService.createTransaction(transactionDto);

        //Assert
        assertNotNull(result);
        verify(transactionRepository).save(any(Transaction.class));
        assertEquals(expectedTransaction.getTransactionId(), result.getTransactionId());
        assertEquals(expectedTransaction.getAmount(), result.getAmount());
        assertEquals(expectedTransaction.getDescription(), result.getDescription());
        assertEquals(expectedTransaction.getSender(), result.getSender());
        assertEquals(expectedTransaction.getReceiver(), result.getReceiver());

    }

    @Test
    void createTransaction_successfulCreation_withoutDescription() {
        //Arrange
        transactionDto.setDescription("");
        transactionDto.setTransactionAmount(100);

        Transaction expectedTransaction = new Transaction();
        expectedTransaction.setTransactionId(1);
        expectedTransaction.setAmount(new BigDecimal("100"));

        expectedTransaction.setSender(senderAppUser);
        expectedTransaction.setReceiver(receiverAppUser);

        when(userRepository.findById(SENDER_ID)).thenReturn(Optional.of(senderAppUser));
        when(userRepository.findById(RECEIVER_ID)).thenReturn(Optional.of(receiverAppUser));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(
                invocation -> {
                    Transaction savedTransaction = invocation.getArgument(0);
                    savedTransaction.setTransactionId(1);
                    return savedTransaction;
                });


        //Act
        Transaction result = transactionService.createTransaction(transactionDto);

        //Assert
        assertNotNull(result);
        verify(transactionRepository).save(any(Transaction.class));
        assertEquals(expectedTransaction.getTransactionId(), result.getTransactionId());
        assertEquals(expectedTransaction.getAmount(), result.getAmount());
        assertEquals(expectedTransaction.getDescription(), result.getDescription());
        assertEquals(expectedTransaction.getSender(), result.getSender());
        assertEquals(expectedTransaction.getReceiver(), result.getReceiver());

    }

    @Test
    void createTransaction_shouldThrowException_withoutSender() {
        //Arrange
        when(userRepository.findById(SENDER_ID)).thenReturn(Optional.empty());
        when(userRepository.findById(RECEIVER_ID)).thenReturn(Optional.of(receiverAppUser));

        //Act & Assert
        assertThrows(RuntimeException.class, () -> {
            transactionService.createTransaction(transactionDto);
        });
    }

    @Test
    void createTransaction_shouldThrowException_withoutReceiver() {
        //Arrange
        when(userRepository.findById(SENDER_ID)).thenReturn(Optional.of(senderAppUser));
        when(userRepository.findById(RECEIVER_ID)).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(RuntimeException.class, () -> {
            transactionService.createTransaction(transactionDto);
        });
    }

    @Test
    void createTransaction_shouldThrowException_negativeTransactionAmount() {
        //Arrange
        transactionDto.setDescription("Test transaction");
        transactionDto.setTransactionAmount(-100);

        when(userRepository.findById(SENDER_ID)).thenReturn(Optional.of(senderAppUser));
        when(userRepository.findById(RECEIVER_ID)).thenReturn(Optional.of(receiverAppUser));


        //Act & Assert
        assertThrows(RuntimeException.class, () -> {
            transactionService.createTransaction(transactionDto);
        });


    }

    @Test
    void createTransaction_shouldThrowException_zeroTransactionAmount() {
        //Arrange
        transactionDto.setDescription("Test transaction");
        transactionDto.setTransactionAmount(0);

        when(userRepository.findById(SENDER_ID)).thenReturn(Optional.of(senderAppUser));
        when(userRepository.findById(RECEIVER_ID)).thenReturn(Optional.of(receiverAppUser));


        //Act & Assert
        assertThrows(RuntimeException.class, () -> {
            transactionService.createTransaction(transactionDto);
        });


    }

    @Test
    void processTransaction_successfulProcessing() {
        //Arrange
        transaction.setAmount(new BigDecimal("100"));
        senderAppUser.setAccount(senderAccount);
        receiverAppUser.setAccount(receiverAccount);
        when(accountRepository.save(senderAccount)).thenReturn(senderAccount);
        when(accountRepository.save(receiverAccount)).thenReturn(receiverAccount);

        //Act
        transactionService.processTransaction(transaction);

        //Assert
        verify(accountRepository, times(2)).save(any(Account.class));
        assertEquals(new BigDecimal("100"), transaction.getAmount());
        assertEquals(new BigDecimal("400"), senderAccount.getBalance());
        assertEquals(new BigDecimal("600"), receiverAccount.getBalance());
    }



    @Test
    void getTransactionHistoryDtoByUser() {
    }
}