package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransferService {

    private String API_BASE_URL;
    private AuthenticatedUser currentUser;
    private RestTemplate restTemplate = new RestTemplate();

    public TransferService(String url, AuthenticatedUser currentUser) {
        this.currentUser = currentUser;
        API_BASE_URL = url;
    }

    public List<User> getAllUsers() {
        User[] allUsersArray = null;
        List<User> newUsers = new ArrayList<>();

        allUsersArray = restTemplate.exchange(API_BASE_URL + "userlist/", HttpMethod.GET, makeAuthEntity(), User[].class).getBody();

        Collections.addAll(newUsers, allUsersArray); // might need to do exception

        return newUsers;
    }


    public void sendTransfer(Transfer transfer) {
        try {
            restTemplate.exchange(API_BASE_URL + "sendtransfer/", HttpMethod.POST, makeTransferEntity(transfer), Transfer.class);
        } catch (RestClientResponseException rcEx) {
            System.out.println(rcEx.getRawStatusCode() + " : " + rcEx.getResponseBodyAsString());
        }

    }


    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
        return entity;
    }

    private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }


}
