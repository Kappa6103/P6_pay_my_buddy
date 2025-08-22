package com.paymybuddy.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "user_account")
public class Account {

    @Id
    @Column(name = "user_id")
    private int userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private BigDecimal balance;


    //Getters and setters


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
