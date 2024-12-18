package com.hexsoftwares.banking_system.terminal_api;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.Scanner;

import com.hexsoftwares.banking_system.terminal_api.XMLUtil;

public class BankingSystem {

    public void registerUser() {
        Scanner scanner = new Scanner(System.in);
        
        // Prompt for personal information
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();
        System.out.print("Enter date of birth (YYYY-MM-DD): ");
        String dob = scanner.nextLine();
        System.out.print("Enter gender: ");
        String gender = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter marital status (Married/Single/Other): ");
        String maritalStatus = scanner.nextLine();
        System.out.print("Enter address: ");
        String address = scanner.nextLine();
        System.out.print("Enter city: ");
        String city = scanner.nextLine();
        System.out.print("Enter zip code: ");
        String zipCode = scanner.nextLine();
        System.out.print("Enter state: ");
        String state = scanner.nextLine();
        
        // Prompt for additional information
        System.out.println("Additional Information:\n");
        System.out.print("Enter religion (Christian/Muslim/Hindu/Other): ");
        String religion = scanner.nextLine();
        System.out.print("Enter category (General/OBC/SC/ST/Other): ");
        String category = scanner.nextLine();
        System.out.print("Enter income: ");
        String income = scanner.nextLine();
        System.out.print("Enter occupation: ");
        String occupation = scanner.nextLine();
        
        // Prompt for account information
        System.out.print("Enter account type (Current/Savings/Fixed Deposit/Recurring): ");
        String accountType = scanner.nextLine();
        
        // Generate card number and PIN
        String cardNumber = generateCardNumber();
        String pin = generatePin();
        
        int accountId = 0;
		try (Connection conn = DriverManager.getConnection(XMLUtil.getUrl(), XMLUtil.getUsername(), XMLUtil.getPassword())) {
			// Insert new account entry
			String accountQuery = "INSERT INTO accounts (balance) VALUES (0.0)";
			PreparedStatement accountStmt = conn.prepareStatement(accountQuery, PreparedStatement.RETURN_GENERATED_KEYS);
			accountStmt.executeUpdate();

			// Retrieve generated account_id
			ResultSet accountKeys = accountStmt.getGeneratedKeys();
			if (accountKeys.next()) {
				accountId = accountKeys.getInt(1);
			}

			// Insert new user with the generated account_id
			String sql = "INSERT INTO users (account_id, first_name, last_name, dob, gender, email, marital_status, " +
				         "address, city, zip_code, state, religion, category, income, occupation, account_type, " +
				         "card_number, pin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, accountId);
			pstmt.setString(2, firstName);
			pstmt.setString(3, lastName);
			pstmt.setString(4, dob);
			pstmt.setString(5, gender);
			pstmt.setString(6, email);
			pstmt.setString(7, maritalStatus);
			pstmt.setString(8, address);
			pstmt.setString(9, city);
			pstmt.setString(10, zipCode);
			pstmt.setString(11, state);
			pstmt.setString(12, religion);
			pstmt.setString(13, category);
			pstmt.setString(14, income);
			pstmt.setString(15, occupation);
			pstmt.setString(16, accountType);
			pstmt.setString(17, cardNumber);
			pstmt.setString(18, pin);
            pstmt.executeUpdate();
            System.out.println("Registration successful! Your card number is: " + cardNumber);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Customer loginUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Card Number: ");
        String cardNumber = scanner.nextLine();
        System.out.print("Enter PIN: ");
        String pin = scanner.nextLine();

        String sql = "SELECT * FROM users WHERE card_number = ? AND pin = ?";
        try (Connection conn = DriverManager.getConnection(XMLUtil.getUrl(), XMLUtil.getUsername(), XMLUtil.getPassword());
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, cardNumber);
			pstmt.setString(2, pin);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				System.out.println("Login successful! Welcome, " + rs.getString("first_name") );
				Customer customer = new Customer(Integer.parseInt(rs.getString("id")), rs.getString("first_name"));
				return customer;
			} else {
				System.out.println("Invalid card number or PIN.");
				return null;
			}
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private String generateCardNumber() {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            cardNumber.append(random.nextInt(10));
        }
        return cardNumber.toString();
    }

    private String generatePin() {
        Random random = new Random();
        StringBuilder pin = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            pin.append(random.nextInt(10));
        }
        return pin.toString();
    }
}

