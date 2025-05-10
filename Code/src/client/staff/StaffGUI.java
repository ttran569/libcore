package client.staff;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StaffGUI {
    // reference to the staff client
    private StaffClient client;
    // frame shown during login
    private JFrame loginFrame;
    // main dashboard window
    private JFrame mainFrame;
    // area to display server messages or user lists
    private JTextArea messageArea;
    // panel to display forms or tables
    private JPanel actionPanel;
    // model for resource table
    private DefaultTableModel resourceTableModel;
    // table to display resources
    private JTable resourceTable;
    // tracks whether viewing users or transactions
    private String currentDisplayMode = "";
 // model for user table
    private DefaultTableModel userTableModel;
 // model for transaction table
    private DefaultTableModel transactionTableModel;


    public StaffGUI(StaffClient client) {
        this.client = client;
        showLoginWindow();
    }

    // creates and shows the login interface
    private void showLoginWindow() {
        loginFrame = new JFrame("Staff Login - LIBcore");
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

    // creates and shows the main staff dashboard
    public void showMainWindow(String username) {
        loginFrame.dispose();

        mainFrame = new JFrame("LIBcore Staff Dashboard - Welcome " + username);
        mainFrame.setSize(1200, 500);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);

        JPanel topButtonPanel = new JPanel(new FlowLayout());
        JButton logoutButton = new JButton("Logout");
        JButton viewResourcesButton = new JButton("View Resources");
        JButton addResourceButton = new JButton("Add Resource");
        JButton viewUsersButton = new JButton("View Users");
        JButton viewTransactionHistoryButton = new JButton("View Transaction History");
        JButton returnResourceButton = new JButton("Return Resource");

        topButtonPanel.add(viewResourcesButton);
        topButtonPanel.add(addResourceButton);
        topButtonPanel.add(viewUsersButton);
        topButtonPanel.add(viewTransactionHistoryButton);
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

        addResourceButton.addActionListener(e -> showAddResourceForm());

        viewUsersButton.addActionListener(e -> {
            clearActionPanel();
            messageArea.setText("");
            client.sendMessage("VIEW_USERS");
        });

        viewTransactionHistoryButton.addActionListener(e -> {
            clearActionPanel();
            messageArea.setText("");
            client.sendMessage("VIEW_TRANSACTIONS");
        });

        returnResourceButton.addActionListener(e -> showReturnResourceForm());

        mainFrame.add(topButtonPanel, BorderLayout.NORTH);
        mainFrame.add(scrollPane, BorderLayout.CENTER);
        mainFrame.add(actionPanel, BorderLayout.SOUTH);
        mainFrame.setVisible(true);
    }

    // removes any form or table from the action panel
    private void clearActionPanel() {
        actionPanel.removeAll();
        actionPanel.revalidate();
        actionPanel.repaint();
    }

    // prepares the resource table and adds click behavior
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

                        // update ui to mark resource unavailable
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

    // displays the form to add a new resource
    private void showAddResourceForm() {
        clearActionPanel();

        JTextField idField = new JTextField(10);
        JTextField titleField = new JTextField(10);
        JTextField authorField = new JTextField(10);
        JTextField categoryField = new JTextField(10);
        JButton submitButton = new JButton("Add Resource");

        JPanel formPanel = new JPanel(new GridLayout(5, 2));
        formPanel.add(new JLabel("Resource id:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Author:"));
        formPanel.add(authorField);
        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryField);
        formPanel.add(new JLabel(""));
        formPanel.add(submitButton);

        submitButton.addActionListener(e -> {
            String id = idField.getText();
            String title = titleField.getText();
            String author = authorField.getText();
            String category = categoryField.getText();

            if (!id.isEmpty() && !title.isEmpty() && !author.isEmpty() && !category.isEmpty()) {
                client.sendMessage("ADD_RESOURCE," + id + "," + title + "," + author + "," + category);
                messageArea.setText("Resource added: " + title);
                clearActionPanel();
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Please fill all fields.");
            }
        });

        actionPanel.add(formPanel);
        actionPanel.revalidate();
        actionPanel.repaint();
    }

    // displays the form to return a borrowed resource
    private void showReturnResourceForm() {
        clearActionPanel();

        JTextField userIdField = new JTextField(10);
        JTextField resourceIdField = new JTextField(10);
        JTextField timeField = new JTextField(10);
        JButton submitButton = new JButton("Return Resource");

        JPanel formPanel = new JPanel(new GridLayout(4, 2));
        formPanel.add(new JLabel("User ID:"));
        formPanel.add(userIdField);
        formPanel.add(new JLabel("Resource ID:"));
        formPanel.add(resourceIdField);
        formPanel.add(new JLabel("Return Time (YYYY-MM-DD):"));
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
    
 // prepares the user table
    private void setupUserTable() {
        userTableModel = new DefaultTableModel(new String[]{"User ID", "Name", "Password", "Address", "Phone", "Role"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable userTable = new JTable(userTableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);

        clearActionPanel();
        actionPanel.add(scrollPane);
        actionPanel.revalidate();
        actionPanel.repaint();
    }
 // prepares the transaction table
    private void setupTransactionTable() {
        transactionTableModel = new DefaultTableModel(new String[]{"Txn ID", "User ID", "Resource ID", "Borrow", "Return", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable txnTable = new JTable(transactionTableModel);
        JScrollPane scrollPane = new JScrollPane(txnTable);

        clearActionPanel();
        actionPanel.add(scrollPane);
        actionPanel.revalidate();
        actionPanel.repaint();
    }


    // receives and handles messages sent by the server
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

            if (!"staff".equalsIgnoreCase(role)) {
                JOptionPane.showMessageDialog(null, "Access denied. This client is for staff users only.");
                return;
            }

            showMainWindow(username);
        } else if (message.equals("LOGIN_FAILED")) {
            JOptionPane.showMessageDialog(null, "Login failed. Invalid user id or password. Please try again.");
        } else if (message.equals("RESOURCE_LIST")) {
            currentDisplayMode = "RESOURCE_LIST";
            if (resourceTableModel != null) {
                resourceTableModel.setRowCount(0);
            }
        } else if (message.equals("USER_LIST")) {
            currentDisplayMode = "USER_LIST";
            setupUserTable();

        } else if (message.equals("TRANSACTION_LIST")) {
            currentDisplayMode = "TRANSACTION_LIST";
            setupTransactionTable();

        } else if (currentDisplayMode.equals("RESOURCE_LIST") && resourceTableModel != null && message.contains(",")) {
            String[] parts = message.split(",");
            if (parts.length >= 5) {
                resourceTableModel.addRow(new Object[]{parts[0], parts[1], parts[2], parts[3], parts[4]});
            }
        } else if (currentDisplayMode.equals("USER_LIST") && userTableModel != null && message.contains(",")) {
            String[] parts = message.split(",");
            if (parts.length == 6) {
                userTableModel.addRow(parts);
            }
        } else if (currentDisplayMode.equals("TRANSACTION_LIST") && transactionTableModel != null && message.contains(",")) {
            String[] parts = message.split(",");
            if (parts.length >= 4) { // support flexible txn format
                transactionTableModel.addRow(parts);
            }
  
        } else {
            messageArea.append(message + "\n");
        }
    }
}
