package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;

public interface AccountBalanceDAO {
    public Account findAccountByID(int id);
     Account findUserById(int userId);
     BigDecimal getBalance(int userId);









}
