package com.hotel;

import com.hotel.ui.ConsoleUI;
import com.hotel.util.DatabaseConnection;

/**
 * Main class - Entry point for Hotel Reservation System
 */
public class Main {
    
    public static void main(String[] args) {
        // Test database connection
        System.out.println("Testing database connection...");
        
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();
        
        if (dbConnection.testConnection()) {
            System.out.println("✓ Database connected successfully!");
            
            // Start the application
            ConsoleUI ui = new ConsoleUI();
            ui.start();
            
        } else {
            System.err.println("✗ Failed to connect to database!");
            System.err.println("Please check:");
            System.err.println("1. MySQL is running");
            System.err.println("2. Database 'hotel_reservation_db' exists");
            System.err.println("3. Username and password in application.properties are correct");
            System.exit(1);
        }
    }
}