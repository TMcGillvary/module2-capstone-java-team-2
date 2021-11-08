package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AccountBalanceService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.tenmo.services.TransferService;
import com.techelevator.view.ConsoleService;

import java.math.BigDecimal;
import java.util.List;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
    private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
    private static final String[] LOGIN_MENU_OPTIONS = {LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};
    private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
    private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
    private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
    private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
    private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
    private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
    private static final String[] MAIN_MENU_OPTIONS = {MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};

    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private AccountBalanceService accountBalanceService;
    private TransferService transferService;


    public static void main(String[] args) {
        App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL));
        app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService) {
        this.console = console;
        this.authenticationService = authenticationService;
        this.accountBalanceService = new AccountBalanceService(API_BASE_URL);
        this.transferService = new TransferService(API_BASE_URL);
    }

    public void run() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");

        registerAndLogin();
        mainMenu();
    }

    private void mainMenu() {
        while (true) {
            String choice = (String) console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
            if (MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
                viewCurrentBalance(currentUser);
            } else if (MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
                viewTransferHistory(currentUser);
                viewTransferDetails(currentUser);
            } else if (MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
                viewPendingRequests();
            } else if (MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
                sendBucks(currentUser);
            } else if (MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
                requestBucks();
            } else if (MAIN_MENU_OPTION_LOGIN.equals(choice)) {
                login();
            } else {
                // the only other option on the main menu is to exit
                exitProgram();
            }
        }
    }

    private void viewCurrentBalance(AuthenticatedUser currentUser) {

        try {
            BigDecimal balance = accountBalanceService.getBalance(currentUser);
            System.out.println("Your current balance is: $" + balance);
        } catch (NullPointerException e) {
            System.out.println("No balance found");
        }

    }

    private void viewTransferHistory(AuthenticatedUser currentUser) {
        List<Transfer> transferList = transferService.getTransferHistory(currentUser);
        if (transferList.isEmpty()) {
            System.out.println("No transfer history to display");
        } else {
            for (Transfer transfer : transferList) {
                int id = transfer.getTransferId();
                String fromUser = transfer.getFromUserName();
                String toUser = transfer.getToUserName();
                BigDecimal amount = transfer.getAmount();

                String formattedTransferlist = String.format("ID: %-10d | From: %-10s | To: %-10s | Amount: $%-6.2f", id, fromUser, toUser, amount);
                System.out.println(formattedTransferlist);

            }

        }

    }

    private void viewTransferDetails(AuthenticatedUser currentUser) {
        int transferId = console.getUserInputInteger("Please enter ID to proceed");
        List<Transfer> transferList = transferService.getTransferHistory(currentUser);
        boolean validId = false;
        if (transferId == 0) {
            System.out.println("Not a valid entry");
            mainMenu();
        } else {
            for (Transfer transfer : transferList) {
                if (transfer.getTransferId() == transferId) {
                    validId = true;
                    String type = "";
                    String status = "";
                    if (transfer.getTransferTypeId() == 1) {
                        type = "Request";
                    } else if (transfer.getTransferTypeId() == 2) {
                        type = "Send";
                    }

                    if (transfer.getTransferStatusId() == 1) {
                        status = "Pending";
                    } else if (transfer.getTransferStatusId() == 2) {
                        status = "Approved";
                    } else if (transfer.getTransferStatusId() == 3) {
                        status = "Rejected";
                    }
                    String formattedTransfer = String.format("Transfer ID: %-10d | Transfer Type: %-10s | Transfer Status: %-10s\nFrom: %d %-20s | To: %d %-20s | Amount: $%-6.2f",
                            transfer.getTransferId(), type, status, transfer.getAccountFrom(), transfer.getFromUserName(), transfer.getAccountTo(), transfer.getToUserName(),
                            transfer.getAmount());
                    System.out.println(formattedTransfer);

                }
                if(validId == false) {
                    System.out.println("Enter a valid ID");
                    viewTransferDetails(currentUser);
                }
            }
        }


    }

    private void viewPendingRequests() {
        // TODO Auto-generated method stub

    }

    private void displayUserListCorrectly() {
        System.out.println("╔═════tr═════════════════════════════════════╗");
        System.out.println("                List of Users               ");
        System.out.println("╚══════════════════════════════════════════╝");
        for (User eachLine : transferService.getAllUsers(currentUser)) {
            System.out.println(eachLine);
        }
        System.out.println("══════════════════════════════════════════");
    }

    private void sendBucks(AuthenticatedUser currentUser) {

        displayUserListCorrectly();

        // TODO if userID is not a valid ID, say sorry they're not a user, try again
        int validUserID = checkForValidUserId(currentUser);

        BigDecimal transferAmount = new BigDecimal(console.getUserInputInteger("Enter amount to transfer"));

        try {
            if (accountBalanceService.getBalance(currentUser).compareTo(transferAmount) < 0) {
                System.out.println("Sorry, you're trying to transfer more money than you have");
                mainMenu();
            } else if (transferAmount.compareTo(BigDecimal.ZERO) < 0) {
                System.out.println("Sorry, please enter a valid amount to transfer");
                mainMenu();
            } else {

                Transfer transfer = new Transfer();
                transfer.setAccountFrom(currentUser.getUser().getId());
                transfer.setAccountTo(validUserID);
                transfer.setAmount(transferAmount);

                transferService.sendTransfer(transfer, currentUser);

                System.out.println("Successfully sent $" + transferAmount);
            }
        } catch (Exception e) {

            System.out.println("Something went wrong.");
        }
    }

    private int checkForValidUserId(AuthenticatedUser currentUser) {
        int userIDtoTransferTo = console.getUserInputInteger("Select user to send funds to");

        if (userIDtoTransferTo == currentUser.getUser().getId()) {
            System.out.println("Sorry, you can't send money to yourself");
            mainMenu();
        }

        boolean validUserId = false;

        for (User user : transferService.getAllUsers(currentUser)) {
            if (user.getId().equals(userIDtoTransferTo)) {
                validUserId = true;
                break;
            }
        }

        while (!validUserId) {
            System.out.println("That is not a valid User.");
            userIDtoTransferTo = console.getUserInputInteger("Select user to send funds to");

            for (User user : transferService.getAllUsers(currentUser)) {
                if (user.getId().equals(userIDtoTransferTo)) {
                    validUserId = true;
                    break;
                }
            }
        }
        return userIDtoTransferTo;
    }

    private void requestBucks() {
        // TODO Auto-generated method stub

    }

    private void exitProgram() {
        System.exit(0);
    }

    private void registerAndLogin() {
        while (!isAuthenticated()) {
            String choice = (String) console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
            if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
                login();
            } else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
                register();
            } else {
                // the only other option on the login menu is to exit
                exitProgram();
            }
        }
    }

    private boolean isAuthenticated() {
        return currentUser != null;
    }

    private void register() {
        System.out.println("Please register a new user account");
        boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
                authenticationService.register(credentials);
                isRegistered = true;
                System.out.println("Registration successful. You can now login.");
            } catch (AuthenticationServiceException e) {
                System.out.println("REGISTRATION ERROR: " + e.getMessage());
                System.out.println("Please attempt to register again.");
            }
        }
    }

    private void login() {
        System.out.println("Please log in");
        currentUser = null;
        while (currentUser == null) //will keep looping until user is logged in
        {
            UserCredentials credentials = collectUserCredentials();
            try {
                currentUser = authenticationService.login(credentials);
            } catch (AuthenticationServiceException e) {
                System.out.println("LOGIN ERROR: " + e.getMessage());
                System.out.println("Please attempt to login again.");
            }
        }
    }

    private UserCredentials collectUserCredentials() {
        String username = console.getUserInput("Username");
        String password = console.getUserInput("Password");
        return new UserCredentials(username, password);
    }


}
