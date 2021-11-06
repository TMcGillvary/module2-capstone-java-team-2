package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

public interface TransferDAO {

    public Integer sendTransfer(int accountFrom, int accountTo, BigDecimal amount);

    public List<Transfer> getAllTransfers(Principal currentUser);

    public Transfer getTransferById(int transferId);

}

