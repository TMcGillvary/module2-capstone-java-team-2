package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class AccountBalanceService {
    private String API_BASE_URL;
    private AuthenticatedUser currentUser;
    private RestTemplate restTemplate = new RestTemplate();

    public AccountBalanceService(String url, AuthenticatedUser currentUser) {
        this.currentUser = currentUser;
        API_BASE_URL = url;
    }

    private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }

    public BigDecimal getBalance() {
        BigDecimal balance = new BigDecimal(0.00);
      balance = restTemplate.exchange(API_BASE_URL + "balance/" + currentUser.getUser().getId(),
              HttpMethod.GET, makeAuthEntity(), BigDecimal.class).getBody();
      return balance;
    }

}

