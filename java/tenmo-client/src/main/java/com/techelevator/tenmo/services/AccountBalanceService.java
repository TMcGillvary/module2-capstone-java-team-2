package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AccountBalanceService {
    private String API_BASE_URL;
    private RestTemplate restTemplate = new RestTemplate();

    public AccountBalanceService(String url) {
        API_BASE_URL = url;
    }

     private HttpEntity makeAuthEntity(AuthenticatedUser currentUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }

    public BigDecimal getBalance(AuthenticatedUser currentUser) {

        BigDecimal balance = new BigDecimal(0);
        try {
            balance = restTemplate.exchange(API_BASE_URL + "balance/", HttpMethod.GET, makeAuthEntity(currentUser), BigDecimal.class).getBody();
        } catch (RestClientException e) {
            System.out.println("Error getting balance");
        }
        return balance;
    }

}

