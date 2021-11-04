package com.techelevator.tenmo.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class JDBCTransferDAO implements TransferDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AccountBalanceDAO accountBalanceDAO;

    @Override
    public Integer sendTransfer(int accountFrom, int accountTo, BigDecimal amount) {
        String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (2, 2, ?, ?, ?) RETURNING transfer_id"; // hard coding in the transfer type and status ID for now based on the README
        Integer newTransferID = null;

        try {
            newTransferID = jdbcTemplate.update(sql, accountFrom, accountTo, amount);
            accountBalanceDAO.addToBalance(amount, accountTo);
            accountBalanceDAO.subtractFromBalance(amount, accountFrom); // TODO fix this
        } catch (DataAccessException e) {
            System.out.println("Error sending transfer, please try again."); // custom exception?
        }
        return newTransferID;
    }
}
