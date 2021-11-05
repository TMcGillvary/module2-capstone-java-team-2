package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;
import java.security.Principal;

public interface AccountBalanceDAO {

    public Account findAccountByID(int id);

    Account findUserById(int userId);

    Account findUserByUsername(Principal user);

    BigDecimal getBalance(int userId);

    BigDecimal addToBalance(BigDecimal amountToAdd, int accountID);

    BigDecimal subtractFromBalance(BigDecimal amountToSubtract, int accountID);

}
