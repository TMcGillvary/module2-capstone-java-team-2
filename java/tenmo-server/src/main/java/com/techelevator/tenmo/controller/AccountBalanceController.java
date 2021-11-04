package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountBalanceDAO;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")

public class AccountBalanceController {
    @Autowired
    private AccountBalanceDAO accountBalanceDAO;

    @Autowired
    private UserDao userDao;

    public AccountBalanceController(AccountBalanceDAO accountBalanceDAO, UserDao userDao) {
        this.accountBalanceDAO = accountBalanceDAO;
        this.userDao = userDao;
    }

    @GetMapping(path = "balance/{userId}")
    public BigDecimal getBalance(@PathVariable int userId) {
        BigDecimal balance = accountBalanceDAO.getBalance(userId);
        return balance;
    }

    @GetMapping(path = "userlist/")
    public List<User> userList() {
        List<User> userList = userDao.findAll();
        return userList;
    }

}
