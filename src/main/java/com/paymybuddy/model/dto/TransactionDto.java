package com.paymybuddy.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public class TransactionDto {

    private List<AppUserNameAndId> relationList;

    private List<TransactionHistoryDto> transactionHistory;

    @NotNull
    private Integer senderId;
    @NotNull
    private Integer receiverId;

    private String description;

    @NotNull(message = "Transaction amount cannot be null")
    private Integer transactionAmount;

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public List<TransactionHistoryDto> getTransactionHistory() {
        return transactionHistory;
    }

    public void setTransactionHistory(List<TransactionHistoryDto> transactionHistory) {
        this.transactionHistory = transactionHistory;
    }

    public List<AppUserNameAndId> getRelationList() {
        return relationList;
    }

    public void setRelationList(List<AppUserNameAndId> relationList) {
        this.relationList = relationList;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(Integer transactionAmount) {
        this.transactionAmount = transactionAmount;
    }
}
