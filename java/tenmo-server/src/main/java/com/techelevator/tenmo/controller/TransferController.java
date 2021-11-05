package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountBalanceDAO;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {

    @Autowired
    TransferDAO transferDAO;
    @Autowired
    AccountBalanceDAO accountBalanceDAO;

    @PostMapping(path = "sendtransfer/")
    public void sendTransfer(@RequestBody Transfer transfer) {
        Account userFrom = accountBalanceDAO.findUserById(transfer.getAccountFrom());
        transfer.setAccountFrom(userFrom.getAccountID());
        Account userTo = accountBalanceDAO.findUserById(transfer.getAccountTo());
        transfer.setAccountTo(userTo.getAccountID());
        //TODO custom user not found exception?

        transferDAO.sendTransfer(transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
    }

    @GetMapping(path = "transferslist/")
    public List<Transfer> getListOfTransfers(Principal user) {
        return transferDAO.getAllTransfers(user);
    }

}
