# Pay My Buddy

Pay My Buddy is a Java Spring Boot web application that allows users to easily transfer money between friends. This project was developed as part of the OpenClassrooms Java Development course.

## 📚 Table of Contents

- [Features](#features)
- [Technologies](#technologies)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [Project Structure](#project-structure)
- [Pages](#pages)

---

## ✅ Features

- User Authentication and Management
    - Register new account
    - Login/Logout functionality
    - Profile management with email/password update
- Money Transfer System
    - Easy transfer interface
    - Transaction history tracking
- Connection Management
    - Add friends using their email
    - View friends list

---

## 🛠️ Technologies

- Java 17+
- Spring Boot
- Spring Security
- Spring Data JPA
- Thymeleaf
- MySQL
- Maven
- HTML/CSS

---

## 🚀 Installation

1. **Clone the project**
   ```bash
   git clone [repository-url]
   cd pay-my-buddy
   ```

2**Build the project**
   ```bash
   mvn clean install
   ```

---

## ▶️ Running the Application

1. **Start the application**
   ```bash
   mvn spring-boot:run
   ```

2. **Access the application**
```
    URL: http://localhost:8080
```
---

## 📁 Project Structure (MVC)
```
src
├── main
│   └── java
│   |   └── com.paymybuddy
│   |       ├── configuration
│   |       ├── controller
│   |       ├── model
│   |       │   ├── dto
│   |       ├── repository
│   |       └── service
│   └── resources
│       ├── static
│       │   └── css
│       └── templates
└── test
    └── java
        └── com.paymybuddy
            ├── controller
            └── service

```

---

## 📱 Pages

### 🔐 Authentication Pages
- **Login Page**: User authentication
- **Registration Page**: New user registration

### 💼 Main Features
- **Transfer Page**: Send money to friends
    - Select friend from dropdown
    - Enter amount and description
    - View transaction history
- **Profile Page**: Manage user information
    - Update username
    - Change email
    - Modify password
- **Connection Page**: Manage friends
    - Add new connections

---

## 🔐 Security Features

- Password encryption
- Session management
- CSRF protection
- Form validation
- Transaction security

---

## 📫 Educational Context

This project was developed as part of the OpenClassrooms Java Development course, focusing on:
- Spring Boot application development
- Database design and implementation
- Security implementation
- Transaction management
- MVC architecture
- Clean code principles

---

## 👤 Author

[Kevin Schade] - OpenClassrooms Student
