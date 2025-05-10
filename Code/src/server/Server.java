package server;

import server.services.AuthenticationService;
import server.services.SessionManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    // the port number used for server-client communication
    public static final int PORT = 12345;

    private static AuthenticationService authService;
    private static SessionManager sessionManager;

    public static void main(String[] args) {
        // initialize shared authentication and session managers
        authService = new AuthenticationService();
        sessionManager = new SessionManager();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[server] listening on port " + PORT);

            while (true) {
                // wait for a client connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("[server] connected to client: " + clientSocket.getInetAddress());

                // create a new handler thread for the connected client
                ClientHandler clientHandler = new ClientHandler(clientSocket, authService, sessionManager);
                clientHandler.start();
            }

        } catch (IOException e) {
            System.err.println("[server] error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
