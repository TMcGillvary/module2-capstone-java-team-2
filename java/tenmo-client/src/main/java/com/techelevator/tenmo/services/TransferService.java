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
    private RestTemplate restTemplate = new RestTemplate();

    public TransferService(String url) {
        API_BASE_URL = url;
    }

    public List<User> getAllUsers(AuthenticatedUser currentUser) {
        User[] allUsersArray = null;
        List<User> newUsers = new ArrayList<>();

        allUsersArray = restTemplate.exchange(API_BASE_URL + "userlist/", HttpMethod.GET, makeAuthEntity(currentUser), User[].class).getBody();

        Collections.addAll(newUsers, allUsersArray); // might need to do exception

        return newUsers;
    }


    public void sendTransfer(Transfer transfer, AuthenticatedUser currentUser) {
        try {
            restTemplate.exchange(API_BASE_URL + "sendtransfer/", HttpMethod.POST, makeTransferEntity(transfer, currentUser), Transfer.class);
        } catch (RestClientResponseException rcEx) {
            System.out.println(rcEx.getRawStatusCode() + " : " + rcEx.getResponseBodyAsString());
        }

    }
    public List<Transfer> getTransferHistory(AuthenticatedUser currentUser){
        List<Transfer> transferList = new ArrayList<Transfer>();
        Transfer[] allTransfersArray = null;
        allTransfersArray = restTemplate.exchange(API_BASE_URL + "transferslist/", HttpMethod.GET, makeAuthEntity(currentUser), Transfer[].class).getBody();
        Collections.addAll(transferList, allTransfersArray);
        return transferList;
    }


    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer, AuthenticatedUser currentUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
        return entity;
    }

    private HttpEntity makeAuthEntity(AuthenticatedUser currentUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }


}
