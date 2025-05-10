package client.staff;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class StaffClient {
    // holds the network socket connected to the server
    private Socket socket;
    // reads incoming messages from the server
    private BufferedReader in;
    // sends messages to the server
    private PrintWriter out;
    // the gui for the staff user
    private StaffGUI gui;
    // stores the currently logged-in user's id
    private String userId;

    // constructor connects to the server and initializes gui and background listener
    public StaffClient(String serverIP, int port) {
        try {
            socket = new Socket(serverIP, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            gui = new StaffGUI(this);
            listenForResponses();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "connection error: " + e.getMessage());
        }
    }

    // sets the id of the currently logged-in user
    public void setUserId(String userId) {
        this.userId = userId;
    }

    // gets the currently logged-in user's id
    public String getUserId() {
        return userId;
    }

    // sends a command or data to the server
    public void sendMessage(String message) {
        out.println(message);
    }

    // listens for messages from the server and sends them to the gui
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

    // entry point for the staff client application
    public static void main(String[] args) {
        new StaffClient("localhost", 12345);
    }
}
