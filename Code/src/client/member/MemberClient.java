package client.member;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class MemberClient {
    // holds the socket connected to the server
    private Socket socket;
    // reads messages from the server
    private BufferedReader in;
    // writes messages to the server
    private PrintWriter out;
    // reference to the gui for member users
    private MemberGUI gui;
    // stores the current logged-in user's id
    private String userId;

    // sets the user id after successful login
    public void setUserId(String userId) {
        this.userId = userId;
    }

    // returns the current user id
    public String getUserId() {
        return userId;
    }

    // connects to the server and initializes streams and gui
    public MemberClient(String serverIP, int port) {
        try {
            socket = new Socket(serverIP, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            gui = new MemberGUI(this);
            listenForResponses();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "connection error: " + e.getMessage());
        }
    }

    // sends a message to the server
    public void sendMessage(String message) {
        out.println(message);
    }

    // listens for server responses and passes them to the gui
    private void listenForResponses() {
        new Thread(() -> {
            try {
                String response;
                while ((response = in.readLine()) != null) {
                    gui.displayServerMessage(response);
                }
            } catch (IOException e) {
                gui.displayServerMessage("connection closed.");
            }
        }).start();
    }

    // main method to launch the member client
    public static void main(String[] args) {
        new MemberClient("localhost", 12345);
    }
}
