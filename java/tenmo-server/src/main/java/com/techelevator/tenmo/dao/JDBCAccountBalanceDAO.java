package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.math.BigDecimal;

public class JDBCAccountBalanceDAO implements AccountBalanceDAO {
    private JdbcTemplate jdbcTemplate;

    public JDBCAccountBalanceDAO() {

    }

    public JDBCAccountBalanceDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Account findAccountByID(int id) {
        Account account = null;
        String sql = "SELECT account_id, user_id, balance FROM accounts WHERE account_id =?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if (results.next()) {
            account = mapRowToAccount(results);
        }
        return account;
    }

    private Account mapRowToAccount(SqlRowSet result) {
        Account account = new Account();
        account.setAccountID(result.getInt("account_id"));
        account.setUserID(result.getInt("user_id"));
        account.setBalance(result.getBigDecimal("balance"));
        return account;
    }

    @Override
    public Account findUserById(int userId) {
        Account account = null;
        String sql = "SELECT account_id, user_id, balance FROM accounts WHERE user_id =?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
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


}
