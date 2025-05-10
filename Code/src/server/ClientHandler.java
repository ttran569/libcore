package server;

import server.models.User;
import server.services.AuthenticationService;
import server.services.ResourceManager;
import server.services.SessionManager;
import server.services.TransactionManager;
import server.services.UserManager;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler extends Thread {
    // handles communication with a single connected client

    private final Socket clientSocket;
    private final AuthenticationService authService;
    private final SessionManager sessionManager;
    private BufferedReader in;
    private PrintWriter out;
    private String currentUserID = null;

    public ClientHandler(Socket socket, AuthenticationService authService, SessionManager sessionManager) {
        this.clientSocket = socket;
        this.authService = authService;
        this.sessionManager = sessionManager;
    }

    public void run() {
        try {
            // input and output streams for communicating with the client
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            // background thread to check for session timeout
            new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(10_000); // check every 10 seconds
                        if (currentUserID != null && !sessionManager.isSessionActive(currentUserID)) {
                            out.println("SESSION_EXPIRED");
                            clientSocket.close(); // disconnect client
                            break;
                        }
                    } catch (Exception e) {
                        break;
                    }
                }
            }).start();

            String inputLine;
            // main loop: process commands sent by the client
            while ((inputLine = in.readLine()) != null) {
                String[] tokens = inputLine.split(",");
                String command = tokens[0];

                switch (command.toUpperCase()) {
                    case "LOGIN":
                        handleLogin(tokens);
                        sessionManager.refreshSession(currentUserID);
                        break;
                    case "LOGOUT":
                        handleLogout();
                        sessionManager.refreshSession(currentUserID);
                        break;
                    case "ADD_RESOURCE":
                        handleAddResource(tokens);
                        sessionManager.refreshSession(currentUserID);
                        break;
                    case "VIEW_RESOURCES":
                        handleViewResources();
                        sessionManager.refreshSession(currentUserID);
                        break;
                    case "VIEW_USERS":
                        handleViewUsers();
                        sessionManager.refreshSession(currentUserID);
                        break;
                    case "VIEW_TRANSACTIONS":
                        handleViewTransactions();
                        sessionManager.refreshSession(currentUserID);
                        break;
                    case "RETURN_RESOURCE":
                        handleReturnResource(tokens);
                        sessionManager.refreshSession(currentUserID);
                        break;
                    case "BORROW_RESOURCE":
                        handleBorrowResource(tokens);
                        sessionManager.refreshSession(currentUserID);
                        break;
                    default:
                        out.println("ERROR: Unknown command");
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("[server] client disconnected: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("[server] error closing connection: " + e.getMessage());
            }
        }
    }

    // handles login by validating credentials and starting a session
    private void handleLogin(String[] tokens) {
        if (tokens.length < 3) {
            out.println("ERROR: LOGIN requires userID and password");
            return;
        }

        String userID = tokens[1];
        String password = tokens[2];

        if (authService.validateCredentials(userID, password)) {
            currentUserID = userID;
            sessionManager.createSession(userID);
            User user = authService.getUser(userID);
            out.println("LOGIN_SUCCESS," + user.getUsername() + "," + user.getRole());
        } else {
            out.println("LOGIN_FAILED");
        }
    }

    // handles logout and ends the user session
    private void handleLogout() {
        if (currentUserID != null) {
            sessionManager.endSession(currentUserID);
            out.println("LOGOUT_SUCCESS");
        } else {
            out.println("ERROR: No active session");
        }
    }

    // adds a new resource to the resource file
    private void handleAddResource(String[] tokens) {
        if (tokens.length < 5) {
            out.println("ERROR: ADD_RESOURCE requires 4 fields");
            return;
        }

        try {
            ResourceManager.addResource(tokens[1], tokens[2], tokens[3], tokens[4]);
            out.println("Resource added successfully: " + tokens[2]);
        } catch (IOException e) {
            out.println("ERROR: Unable to add resource");
        }
    }

    // sends all resource records to the client
    private void handleViewResources() {
        try {
            List<String> resources = ResourceManager.getAllResources();
            out.println("RESOURCE_LIST"); // signal gui to begin table update
            for (String r : resources) {
                out.println(r);
            }
        } catch (IOException e) {
            out.println("ERROR: Could not read resources");
        }
    }

    // sends all user records to the client
    private void handleViewUsers() {
        try {
            List<String> users = UserManager.getAllUsers();
            out.println("USER_LIST");
            for (String u : users) {
                out.println(u);
            }
        } catch (IOException e) {
            out.println("ERROR: Could not read users");
        }
    }

    // sends all transaction records to the client
    private void handleViewTransactions() {
        try {
            List<String> txns = TransactionManager.getAllTransactions();
            out.println("TRANSACTION_LIST");
            for (String t : txns) {
                out.println(t);
            }
        } catch (IOException e) {
            out.println("ERROR: Could not export transactions");
        }
    }

    // records a resource return and updates availability
    private void handleReturnResource(String[] tokens) {
        if (tokens.length < 4) {
            out.println("ERROR: RETURN_RESOURCE requires userID, resourceID, and return time");
            return;
        }

        String userId = tokens[1];
        String resourceId = tokens[2];
        String returnDate = tokens[3];

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/transactions.txt", true))) {
            writer.write("T" + System.currentTimeMillis() + "," + userId + "," + resourceId + ",,," + returnDate + ",RETURNED");
            writer.newLine();
            ResourceManager.updateAvailability(resourceId, true);
            out.println("Resource return recorded for resource ID: " + resourceId);
        } catch (IOException e) {
            out.println("ERROR: Could not record return");
        }
    }

    // records a resource borrowing and updates availability
    private void handleBorrowResource(String[] tokens) {
        if (tokens.length < 4) {
            out.println("ERROR: BORROW_RESOURCE requires userID, resourceID, and borrow time");
            return;
        }

        String userId = tokens[1];
        String resourceId = tokens[2];
        String borrowDate = tokens[3];

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/transactions.txt", true))) {
            writer.write("T" + System.currentTimeMillis() + "," + userId + "," + resourceId + "," + borrowDate + ",,CHECKED_OUT");
            writer.newLine();
            ResourceManager.updateAvailability(resourceId, false);
            out.println("Resource borrowed successfully for resource ID: " + resourceId);
        } catch (IOException e) {
            out.println("ERROR: Could not record borrowing");
        }
    }
}
