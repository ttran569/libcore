package client.member;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MemberGUI {
    // reference to the member client
    private MemberClient client;
    // frame shown during login
    private JFrame loginFrame;
    // main dashboard window
    private JFrame mainFrame;
    // area to display server messages
    private JTextArea messageArea;
    // panel for resource table and forms
    private JPanel actionPanel;
    // data model for the resource table
    private DefaultTableModel resourceTableModel;
    // table to display resources
    private JTable resourceTable;

    public MemberGUI(MemberClient client) {
        this.client = client;
        showLoginWindow();
    }

    // shows the login window to the member
    private void showLoginWindow() {
        loginFrame = new JFrame("Member Login - LIBcore");
        loginFrame.setSize(350, 200);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLayout(new GridLayout(4, 2));

        JTextField userIdField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        loginFrame.add(new JLabel("User ID:"));
        loginFrame.add(userIdField);
        loginFrame.add(new JLabel("Password:"));
        loginFrame.add(passwordField);
        loginFrame.add(new JLabel(""));
        loginFrame.add(loginButton);

        loginButton.addActionListener(e -> {
            String userID = userIdField.getText();
            String password = new String(passwordField.getPassword());
            if (!userID.isEmpty() && !password.isEmpty()) {
                client.sendMessage("LOGIN," + userID + "," + password);
            }
        });

        loginFrame.setVisible(true);
    }

    // shows the main dashboard for member users
    public void showMainWindow(String username) {
        loginFrame.dispose();

        mainFrame = new JFrame("LIBcore Member Dashboard - Welcome " + username);
        mainFrame.setSize(1200, 500);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);

        JPanel topButtonPanel = new JPanel(new FlowLayout());
        JButton logoutButton = new JButton("Logout");
        JButton viewResourcesButton = new JButton("View Resources");
        JButton returnResourceButton = new JButton("Return Resource");

        topButtonPanel.add(viewResourcesButton);
        topButtonPanel.add(returnResourceButton);
        topButtonPanel.add(logoutButton);

        actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));

        logoutButton.addActionListener(e -> {
            client.sendMessage("LOGOUT");
            mainFrame.dispose();
            showLoginWindow();
        });

        viewResourcesButton.addActionListener(e -> {
            clearActionPanel();
            setupResourceTable();
            client.sendMessage("VIEW_RESOURCES");
        });

        returnResourceButton.addActionListener(e -> showReturnResourceForm());

        mainFrame.add(topButtonPanel, BorderLayout.NORTH);
        mainFrame.add(scrollPane, BorderLayout.CENTER);
        mainFrame.add(actionPanel, BorderLayout.SOUTH);
        mainFrame.setVisible(true);
    }

    // clears the action panel for new content
    private void clearActionPanel() {
        actionPanel.removeAll();
        actionPanel.revalidate();
        actionPanel.repaint();
    }

    // sets up and displays the resource table with double-click borrowing
    private void setupResourceTable() {
        resourceTableModel = new DefaultTableModel(new String[]{"Resource ID", "Title", "Author", "Category", "Available"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resourceTable = new JTable(resourceTableModel);
        JScrollPane tableScrollPane = new JScrollPane(resourceTable);

        JLabel instructionLabel = new JLabel("Double-click a resource to borrow it.");
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        actionPanel.removeAll();
        actionPanel.add(instructionLabel);
        actionPanel.add(tableScrollPane);

        resourceTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int selectedRow = resourceTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        String resourceId = (String) resourceTableModel.getValueAt(selectedRow, 0);
                        String availability = (String) resourceTableModel.getValueAt(selectedRow, 4);

                        if ("false".equalsIgnoreCase(availability)) {
                            JOptionPane.showMessageDialog(mainFrame, "This resource is not available for borrowing.", "Unavailable", JOptionPane.WARNING_MESSAGE);
                            return;
                        }

                        String userId = client.getUserId();
                        String borrowDate = java.time.LocalDate.now().toString();

                        resourceTableModel.setValueAt("false", selectedRow, 4);
                        client.sendMessage("BORROW_RESOURCE," + userId + "," + resourceId + "," + borrowDate);
                        JOptionPane.showMessageDialog(mainFrame, "Resource borrowed successfully: " + resourceId);
                    }
                }
            }
        });

        actionPanel.revalidate();
        actionPanel.repaint();
    }

    // shows the form to return a resource
    private void showReturnResourceForm() {
        clearActionPanel();

        JTextField userIdField = new JTextField(10);
        JTextField resourceIdField = new JTextField(10);
        JTextField timeField = new JTextField(10);
        JButton submitButton = new JButton("Return resource");

        JPanel formPanel = new JPanel(new GridLayout(4, 2));
        formPanel.add(new JLabel("User ID:"));
        formPanel.add(userIdField);
        formPanel.add(new JLabel("Resource ID:"));
        formPanel.add(resourceIdField);
        formPanel.add(new JLabel("Return time (YYYY-MM-DD):"));
        formPanel.add(timeField);
        formPanel.add(new JLabel(""));
        formPanel.add(submitButton);

        submitButton.addActionListener(e -> {
            String userId = userIdField.getText();
            String resourceId = resourceIdField.getText();
            String returnTime = timeField.getText();

            if (!userId.isEmpty() && !resourceId.isEmpty() && !returnTime.isEmpty()) {
                client.sendMessage("RETURN_RESOURCE," + userId + "," + resourceId + "," + returnTime);
                messageArea.setText("Resource returned successfully.");
                clearActionPanel();
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Please fill all fields.");
            }
        });

        actionPanel.add(formPanel);
        actionPanel.revalidate();
        actionPanel.repaint();
    }

    // handles incoming messages from the server
    public void displayServerMessage(String message) {
        if (message.equals("SESSION_EXPIRED")) {
            JOptionPane.showMessageDialog(null, "Session expired due to inactivity.");
            mainFrame.dispose();
            showLoginWindow();
            return;
        }

        if (message.startsWith("LOGIN_SUCCESS")) {
            String[] parts = message.split(",");
            String username = parts[1];
            String role = parts[2];

            if (!"member".equalsIgnoreCase(role)) {
                JOptionPane.showMessageDialog(null, "Access denied. This window is for member users only.");
                return;
            }

            showMainWindow(username);
        } else if (message.equals("LOGIN_FAILED")) {
            JOptionPane.showMessageDialog(null, "Login failed. Invalid user id or password. Please try again.");
        } else if (message.equals("RESOURCE_LIST")) {
            if (resourceTableModel != null) {
                resourceTableModel.setRowCount(0);
            }
        } else if (message.contains(",")) {
            String[] parts = message.split(",");
            if (parts.length >= 5) {
                resourceTableModel.addRow(new Object[]{parts[0], parts[1], parts[2], parts[3], parts[4]});
            }
        } else {
            messageArea.append(message + "\n");
        }
    }
}
