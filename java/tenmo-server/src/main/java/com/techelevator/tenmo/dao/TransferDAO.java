package com.techelevator.tenmo.dao;

import java.math.BigDecimal;

public interface TransferDAO {

    public Integer sendTransfer(int accountFrom, int accountTo, BigDecimal amount);

}

