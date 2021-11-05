package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;

@Service
public class JDBCAccountBalanceDAO implements AccountBalanceDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public JDBCAccountBalanceDAO() {
    }

    public JDBCAccountBalanceDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Account findAccountByID(int id) {
        Account account = null;
        String sql = "SELECT account_id, user_id, balance FROM accounts WHERE account_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if (results.next()) {
            account = mapRowToAccount(results);
        }
        return account;
    }

    @Override
    public Account findUserById(int userId) {
        Account account = null;
        String sql = "SELECT account_id, user_id, balance FROM accounts WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        if (results.next()) {
            account = mapRowToAccount(results);
        }
        return account;
    }

    @Override
    public Account findUserByUsername(Principal user) {
        Account account = null;
        String sql = "SELECT accounts.account_id, accounts.user_id, accounts.balance FROM accounts JOIN users ON accounts.user_id = users.user_id WHERE username = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, user.getName());
        if (results.next()) {
            account = mapRowToAccount(results);
        }
        return account;
    }

    @Override
    public BigDecimal getBalance(int userId) {
        BigDecimal balance = null;
        String sql = "SELECT balance FROM accounts WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        if (results.next()) {
            balance = results.getBigDecimal("balance");
        }
        return balance;
    }

    @Override
    public BigDecimal addToBalance(BigDecimal amountToAdd, int userID) {
        Account account = findAccountByID(userID);

        BigDecimal newBalance = account.getBalance().add(amountToAdd);

        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";

        try {
            jdbcTemplate.update(sql, newBalance, userID);
        } catch (DataAccessException e) {
            System.out.println("Error updating amounts, Unable to add to balance"); // custom exception?
        }

        return account.getBalance();
    }

    @Override
    public BigDecimal subtractFromBalance(BigDecimal amountToSubtract, int accountID) {
        Account account = findAccountByID(accountID);

        BigDecimal newBalance = account.getBalance().subtract(amountToSubtract);

        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";

        try {
            jdbcTemplate.update(sql, newBalance, accountID);
        } catch (DataAccessException e) {
            System.out.println("Error updating amounts, Unable to subtract from balance"); // custom exception?
        }

        return account.getBalance();
    }



    // helper method to map row from DB to an Account object
    private Account mapRowToAccount(SqlRowSet result) {
        Account account = new Account();
        account.setAccountID(result.getInt("account_id"));
        account.setUserID(result.getInt("user_id"));
        account.setBalance(result.getBigDecimal("balance"));
        return account;
    }
}
