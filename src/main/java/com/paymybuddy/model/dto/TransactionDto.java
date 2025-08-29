package com.paymybuddy.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public class TransactionDto {

    private List<AppUserNameAndId> relationList;

    private AppUserNameAndId receiver;

    private String description;

    @NotNull(message = "Transaction amount cannot be null")
    @Positive(message = "Transaction amount must be at least one Euro")
    private Integer transactionAmount;

    public List<AppUserNameAndId> getRelationList() {
        return relationList;
    }

    public void setRelationList(List<AppUserNameAndId> relationList) {
        this.relationList = relationList;
    }

    public AppUserNameAndId getReceiver() {
        return receiver;
    }

    public void setReceiver(AppUserNameAndId receiver) {
        this.receiver = receiver;
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
