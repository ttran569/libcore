![Logo](https://i.postimg.cc/FK9Vj4sc/Screenshot-2025-01-22-at-7-37-34-PM.png)

# LIBcore Library Management System

## 📚 Purpose
The LIBcore Library Management System is designed to streamline the way libraries manage book lending, user activity, and inventory. It replaces manual systems and spreadsheets with an interactive, multi-user networked application that makes library management easy, efficient, and scalable.

This system is particularly beneficial to libraries because:
- It supports **multi-user concurrent access** via a client-server architecture.
- Users (members) and staff have **separate interfaces** tailored to their roles.
- It maintains organized **digital records** of users, books, and loan history using structured text files.
- It includes logic to track late returns and automatically update resource availability.

---

## ✨ Features

### ✅ Book Search
Users can view all available books in a clean tabular format with columns for ID, Title, Author, Category, and Availability.

### ✅ Check Book Status
Staff can see which books are currently checked out and which are in stock.

### ✅ Late Check
Automatically identifies books not returned by the due date and flags them as overdue.

### ✅ GUI Dashboard
- Built using `Swing`, the GUI is interactive and intuitive.
- Members can browse, borrow, and return books easily.
- Staff can manage resources, view user data, export transactions, and perform admin actions.

---

## 🚀 Usage & Setup

### 1. Clone the repository
```bash
git clone https://github.com/ttran569/libcore.git
cd libcore
git checkout ttran569-patch-1
```

### 2. Project Structure
```
libcore/
├── src/
│   ├── client/
│   │   ├── member/          # Member GUI and client logic
│   │   │   └── MemberGUI.java, MemberClient.java
│   │   └── staff/           # Staff GUI and client logic
│   │       └── StaffGUI.java, StaffClient.java
│   ├── server/              # Server code and networking
│   │   ├── Server.java, ClientHandler.java
│   └── server/services/     # Business logic
│       └── ResourceManager.java, TransactionManager.java, UserManager.java
├── data/
│   ├── users.txt
│   ├── resources.txt
│   └── transactions.txt
```

### 3. How to Run
1. Start the server:
```bash
Run Server.java (Eclipse or CLI)
```

2. Run Staff or Member client on separate machines or windows:
```bash
Run StaffClient.java or MemberClient.java
```
> Clients communicate with the server via sockets.

### 4. Text Files Used
- `users.txt`: contains all user information
- `resources.txt`: contains all book records
- `transactions.txt`: contains borrow/return history

---

## 🧠 Code Explanation

### `ClientHandler.java`
- Core server-side logic
- Handles login, logout, view resources, borrow, return, export transactions

### `ResourceManager.java`
- Adds, edits, deletes, and checks availability of resources
- Updates `resources.txt`

### `TransactionManager.java`
- Logs all transactions
- Exports and filters overdue books

### `UserManager.java`
- Displays user data from `users.txt`

### `StaffGUI.java`
- GUI for staff
- Options to view/add books, view users, return/borrow, export logs

### `MemberGUI.java`
- GUI for members
- Simplified to borrow, return, and view available books

### `SessionManager.java`
- Handles user sessions and session expiry (future-ready)

### `AuthenticationService.java`
- Validates user login credentials from `users.txt`

---

## 👥 User Roles
- `STAFF`: Full access to resources and logs
- `MEMBER`: Can only borrow/return/view books

---

## 📌 Notes
- Uses plain `.txt` files instead of a DB for simplicity
- Easily expandable to JDBC / MySQL / SQLite in the future
- Fully object-oriented, modular design using Java and Swing

---

## 📬 Contributions
This project was developed by Kartik Tripathi, Steven Le, Alejandro Fernandez and Thomas Tran. Contributions, forks, and feedback are welcome.

---

## 📄 License
This project is for academic and educational use.

