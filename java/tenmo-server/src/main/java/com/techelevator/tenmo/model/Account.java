package com.techelevator.tenmo.model;
import java.math.BigDecimal;
public class Account {
    private int accountID;
    private int userID;
    private BigDecimal balance;


    public int getAccountID() {
        return accountID;
    }

    public int getUserID() {
        return userID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}


