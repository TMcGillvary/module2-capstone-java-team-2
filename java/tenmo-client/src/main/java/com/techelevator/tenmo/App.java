package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AccountBalanceService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.tenmo.services.TransferService;
import com.techelevator.view.ConsoleService;

import java.math.BigDecimal;

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


    public static void main(String[] args) {
        App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL));
        app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService) {
        this.console = console;
        this.authenticationService = authenticationService;
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
            AccountBalanceService accountBalanceService = new AccountBalanceService(API_BASE_URL, currentUser);
            TransferService transferService = new TransferService(API_BASE_URL, currentUser);

            String choice = (String) console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
            if (MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
                viewCurrentBalance(accountBalanceService);
            } else if (MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
                viewTransferHistory();
            } else if (MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
                viewPendingRequests();
            } else if (MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
                sendBucks(accountBalanceService, transferService);
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

    private void viewCurrentBalance(AccountBalanceService accountBalanceService) {

        try {
            BigDecimal balance = accountBalanceService.getBalance();
            System.out.println("\nYour current balance is: $" + balance);
        } catch (NullPointerException e) {
            System.out.println("No balance found");
        }

    }

    private void viewTransferHistory() {
        // TODO Auto-generated method stub

    }

    private void viewPendingRequests() {
        // TODO Auto-generated method stub

    }

    private static void displayUserListCorrectly(TransferService transferService) {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("                List of Users               ");
        System.out.println("╚══════════════════════════════════════════╝");
        for (User eachLine : transferService.getAllUsers()) {
            System.out.println(eachLine);
        }
        System.out.println("══════════════════════════════════════════");
    }

    private void sendBucks(AccountBalanceService accountBalanceService, TransferService transferService) {

        displayUserListCorrectly(transferService);

        // TODO if userID is not a valid ID, say sorry they're not a user, try again
        int validUserID = checkForValidUserId(transferService);

        BigDecimal transferAmount = new BigDecimal(console.getUserInputInteger("Enter amount to transfer"));

        try {
			if (accountBalanceService.getBalance().compareTo(transferAmount) < 0) {
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

				transferService.sendTransfer(transfer);

				System.out.println("Successfully sent $" + transferAmount);
			}
		} catch (Exception e) {
        	// TODO gotta make a catch exception for if any of the try doesn't work so it doesn't just print successful! even as it breaks
			System.out.println("Something went wrong.");
		}
    }

    private int checkForValidUserId(TransferService transferService) {
		int userIDtoTransferTo = console.getUserInputInteger("Select user to send funds to");

		if (userIDtoTransferTo == currentUser.getUser().getId()) {
			System.out.println("Sorry, you can't send money to yourself");
			mainMenu();
		}

        boolean validUserId = false;

        for (User user : transferService.getAllUsers()) {
            if (user.getId().equals(userIDtoTransferTo)) {
                validUserId = true;
                break;
            }
        }

        while (!validUserId) {
            System.out.println("That is not a valid User.");
			userIDtoTransferTo = console.getUserInputInteger("Select user to send funds to");

            for (User user : transferService.getAllUsers()) {
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
