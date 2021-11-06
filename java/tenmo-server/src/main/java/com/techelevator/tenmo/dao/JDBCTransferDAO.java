package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JDBCTransferDAO implements TransferDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AccountBalanceDAO accountBalanceDAO;
    @Autowired
    private UserDao userDao;

    @Override
    public Integer sendTransfer(int accountFrom, int accountTo, BigDecimal amount) {
        String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (2, 2, ?, ?, ?) RETURNING transfer_id"; // hard coding in the transfer type and status ID for now based on the README
        Integer newTransferID = -1;

        try {
            newTransferID = jdbcTemplate.queryForObject(sql, Integer.class, accountFrom, accountTo, amount);
            accountBalanceDAO.addToBalance(amount, accountTo);
            accountBalanceDAO.subtractFromBalance(amount, accountFrom);
        } catch (DataAccessException e) {
            System.out.println("Error sending transfer, please try again."); // TODO look at custom exception here
        }
        return newTransferID;
    }

    @Override
    public List<Transfer> getAllTransfers(Principal currentUser) {
        List<Transfer> listOfTransfers = new ArrayList<>();

        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount "
                + "FROM transfers WHERE account_from = ? OR account_to = ?";

        int accountID = accountBalanceDAO.findUserByUsername(currentUser).getAccountID();

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountID, accountID);

            while (results.next()) {
                Transfer transfer = mapRowToTransfer(results);

                // this part is to convert the account IDs back to the usernames for each person
                User user = userDao.findUserByAccountID(transfer.getAccountFrom());
                transfer.setFromUserName(user.getUsername());

                user = userDao.findUserByAccountID(transfer.getAccountTo());
                transfer.setToUserName(user.getUsername());

                listOfTransfers.add(transfer);
            }

        } catch (DataAccessException e) {
            System.out.println("There was an error getting the list of transfers, please check."); // TODO look at custom exception here
        }
        return listOfTransfers;
    }

    @Override
    public Transfer getTransferById(int transferID) {
        Transfer transfer = new Transfer();
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount "
                + "FROM transfers WHERE transfer_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferID);

            while (results.next()) {
                transfer = mapRowToTransfer(results);
                User user = userDao.findUserByAccountID(transfer.getAccountFrom());
                transfer.setFromUserName(user.getUsername());
                user = userDao.findUserByAccountID(transfer.getAccountTo());
                transfer.setToUserName(user.getUsername());
            }
        } catch (DataAccessException e) {
            System.out.println("There was an error getting the transfer object for that ID, please check."); // TODO look at custom exception here
        }
        return transfer;
    }

    private Transfer mapRowToTransfer(SqlRowSet results) {
        Transfer transfer = new Transfer();

        transfer.setTransferId(results.getInt("transfer_id"));
        transfer.setTransferTypeId(results.getInt("transfer_type_id"));
        transfer.setTransferStatusId(results.getInt("transfer_status_id"));
        transfer.setAccountFrom(results.getInt("account_from"));
        transfer.setAccountTo(results.getInt("account_to"));
        transfer.setAmount(results.getBigDecimal("amount"));

        return transfer;
    }
}
