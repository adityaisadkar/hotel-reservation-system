package com.hotel.ui;

import com.hotel.model.Room.RoomType;
import com.hotel.service.ReservationService;
import com.hotel.util.InputValidator;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Console-based user interface for the hotel reservation system
 */
public class ConsoleUI {
    
    private final Scanner scanner;
    private final ReservationService reservationService;
    private boolean running;

    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
        this.reservationService = new ReservationService();
        this.running = true;
    }

    /**
     * Start the application
     */
    public void start() {
        displayWelcome();
        
        while (running) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");
            System.out.println();
            
            handleMenuChoice(choice);
        }
        
        System.out.println("\nThank you for using Hotel Reservation System!");
        scanner.close();
    }

    /**
     * Display welcome message
     */
    private void displayWelcome() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("    WELCOME TO HOTEL RESERVATION MANAGEMENT SYSTEM");
        System.out.println("=".repeat(60));
    }

    /**
     * Display main menu
     */
    private void displayMenu() {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("MAIN MENU");
        System.out.println("-".repeat(60));
        System.out.println("1. View Available Rooms");
        System.out.println("2. View Available Rooms by Type");
        System.out.println("3. Create New Reservation");
        System.out.println("4. View All Reservations");
        System.out.println("5. View Reservation by ID");
        System.out.println("6. Cancel Reservation");
        System.out.println("7. Exit");
        System.out.println("-".repeat(60));
    }

    /**
     * Handle menu choice
     */
    private void handleMenuChoice(int choice) {
        switch (choice) {
            case 1:
                viewAvailableRooms();
                break;
            case 2:
                viewAvailableRoomsByType();
                break;
            case 3:
                createReservation();
                break;
            case 4:
                viewAllReservations();
                break;
            case 5:
                viewReservationById();
                break;
            case 6:
                cancelReservation();
                break;
            case 7:
                running = false;
                break;
            default:
                System.out.println("Invalid choice! Please enter a number between 1 and 7.");
        }
    }

    /**
     * View available rooms
     */
    private void viewAvailableRooms() {
        System.out.println("\n>>> AVAILABLE ROOMS <<<");
        reservationService.viewAvailableRooms();
    }

    /**
     * View available rooms by type
     */
    private void viewAvailableRoomsByType() {
        System.out.println("\n>>> AVAILABLE ROOMS BY TYPE <<<");
        System.out.println("\nRoom Types:");
        System.out.println("1. SINGLE");
        System.out.println("2. DOUBLE");
        System.out.println("3. SUITE");
        System.out.println("4. DELUXE");
        
        int typeChoice = getIntInput("\nSelect room type (1-4): ");
        
        RoomType roomType;
        switch (typeChoice) {
            case 1:
                roomType = RoomType.SINGLE;
                break;
            case 2:
                roomType = RoomType.DOUBLE;
                break;
            case 3:
                roomType = RoomType.SUITE;
                break;
            case 4:
                roomType = RoomType.DELUXE;
                break;
            default:
                System.out.println("Invalid room type!");
                return;
        }
        
        reservationService.viewAvailableRoomsByType(roomType);
    }

    /**
     * Create new reservation
     */
    private void createReservation() {
        System.out.println("\n>>> CREATE NEW RESERVATION <<<");
        
        // Show available rooms first
        reservationService.viewAvailableRooms();
        
        System.out.println("\n--- Customer Information ---");
        
        String firstName = getStringInput("First Name: ");
        String lastName = getStringInput("Last Name: ");
        
        String email;
        while (true) {
            email = getStringInput("Email: ");
            if (InputValidator.isValidEmail(email)) {
                break;
            }
            System.out.println("Invalid email format! Please try again.");
        }
        
        String phoneNumber;
        while (true) {
            phoneNumber = getStringInput("Phone Number (10 digits): ");
            if (InputValidator.isValidPhone(phoneNumber)) {
                break;
            }
            System.out.println("Invalid phone number! Must be 10 digits.");
        }
        
        String idProof = getStringInput("ID Proof (Aadhaar/PAN/Passport): ");
        
        System.out.println("\n--- Booking Information ---");
        
        int roomId = getIntInput("Enter Room ID: ");
        
        // Validate room exists
        if (reservationService.getRoomById(roomId) == null) {
            System.out.println("\nError: Room with ID " + roomId + " not found!");
            return;
        }
        
        LocalDate checkInDate;
        while (true) {
            String checkInStr = getStringInput("Check-In Date (YYYY-MM-DD): ");
            try {
                checkInDate = InputValidator.parseDate(checkInStr);
                if (checkInDate.isBefore(LocalDate.now())) {
                    System.out.println("Check-in date cannot be in the past!");
                    continue;
                }
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format! Use YYYY-MM-DD (e.g., 2024-12-25)");
            }
        }
        
        LocalDate checkOutDate;
        while (true) {
            String checkOutStr = getStringInput("Check-Out Date (YYYY-MM-DD): ");
            try {
                checkOutDate = InputValidator.parseDate(checkOutStr);
                if (!InputValidator.isValidDateRange(checkInDate, checkOutDate)) {
                    System.out.println("Check-out date must be after check-in date!");
                    continue;
                }
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format! Use YYYY-MM-DD (e.g., 2024-12-27)");
            }
        }
        
        // Create reservation
        System.out.println("\nProcessing reservation...");
        reservationService.createReservation(
            firstName, lastName, email, phoneNumber, idProof,
            roomId, checkInDate, checkOutDate
        );
    }

    /**
     * View all reservations
     */
    private void viewAllReservations() {
        System.out.println("\n>>> ALL RESERVATIONS <<<");
        reservationService.viewAllReservations();
    }

    /**
     * View reservation by ID
     */
    private void viewReservationById() {
        System.out.println("\n>>> VIEW RESERVATION <<<");
        int reservationId = getIntInput("Enter Reservation ID: ");
        reservationService.viewReservationById(reservationId);
    }

    /**
     * Cancel reservation
     */
    private void cancelReservation() {
        System.out.println("\n>>> CANCEL RESERVATION <<<");
        int reservationId = getIntInput("Enter Reservation ID to cancel: ");
        
        String confirmation = getStringInput("Are you sure you want to cancel? (yes/no): ");
        if (confirmation.equalsIgnoreCase("yes")) {
            reservationService.cancelReservation(reservationId);
        } else {
            System.out.println("Cancellation aborted.");
        }
    }

    /**
     * Get string input from user
     */
    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Get integer input from user
     */
    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid number.");
            }
        }
    }
}