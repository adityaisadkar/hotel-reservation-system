package com.hotel.service;

import com.hotel.dao.CustomerDAO;
import com.hotel.dao.ReservationDAO;
import com.hotel.dao.RoomDAO;
import com.hotel.model.Customer;
import com.hotel.model.Reservation;
import com.hotel.model.Reservation.ReservationStatus;
import com.hotel.model.Room;
import com.hotel.model.Room.RoomStatus;
import com.hotel.model.Room.RoomType;
import com.hotel.util.InputValidator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Service layer for reservation business logic
 */
public class ReservationService {
    
    private final ReservationDAO reservationDAO;
    private final RoomDAO roomDAO;
    private final CustomerDAO customerDAO;

    public ReservationService() {
        this.reservationDAO = new ReservationDAO();
        this.roomDAO = new RoomDAO();
        this.customerDAO = new CustomerDAO();
    }

    /**
     * Create a new reservation
     */
    public int createReservation(String firstName, String lastName, String email, 
                                  String phoneNumber, String idProof,
                                  int roomId, LocalDate checkIn, LocalDate checkOut) {
        
        // Validate inputs
        if (!InputValidator.isNotEmpty(firstName) || !InputValidator.isNotEmpty(lastName)) {
            System.out.println("Error: Name cannot be empty");
            return -1;
        }

        if (!InputValidator.isValidEmail(email)) {
            System.out.println("Error: Invalid email format");
            return -1;
        }

        if (!InputValidator.isValidPhone(phoneNumber)) {
            System.out.println("Error: Invalid phone number (must be 10 digits)");
            return -1;
        }

        if (!InputValidator.isValidDateRange(checkIn, checkOut)) {
            System.out.println("Error: Check-out date must be after check-in date");
            return -1;
        }

        if (checkIn.isBefore(LocalDate.now())) {
            System.out.println("Error: Check-in date cannot be in the past");
            return -1;
        }

        // Check if room exists and is available
        Room room = roomDAO.getRoomById(roomId);
        if (room == null) {
            System.out.println("Error: Room not found");
            return -1;
        }

        if (room.getStatus() != RoomStatus.AVAILABLE) {
            System.out.println("Error: Room is not available");
            return -1;
        }

        // Check if room is available for the requested dates
        if (!reservationDAO.isRoomAvailable(roomId, checkIn, checkOut)) {
            System.out.println("Error: Room is already booked for selected dates");
            return -1;
        }

        // Check if customer exists (by email or phone)
        Customer customer = customerDAO.getCustomerByEmail(email);
        if (customer == null) {
            customer = customerDAO.getCustomerByPhone(phoneNumber);
        }

        // If customer doesn't exist, create new customer
        int customerId;
        if (customer == null) {
            Customer newCustomer = new Customer(firstName, lastName, email, phoneNumber, idProof);
            customerId = customerDAO.addCustomer(newCustomer);
            
            if (customerId == -1) {
                System.out.println("Error: Failed to create customer");
                return -1;
            }
            System.out.println("New customer created with ID: " + customerId);
        } else {
            customerId = customer.getCustomerId();
            System.out.println("Existing customer found: " + customer.getFullName());
        }

        // Calculate total amount
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        double totalAmount = nights * room.getPricePerNight();

        // Create reservation
        Reservation reservation = new Reservation(customerId, roomId, checkIn, checkOut, totalAmount);
        int reservationId = reservationDAO.createReservation(reservation);

        if (reservationId > 0) {
            // Update room status to OCCUPIED
            roomDAO.updateRoomStatus(roomId, RoomStatus.OCCUPIED);
            System.out.println("\n✓ Reservation created successfully!");
            System.out.println("Reservation ID: " + reservationId);
            System.out.println("Room Number: " + room.getRoomNumber());
            System.out.println("Total Amount: ₹" + String.format("%.2f", totalAmount) + " for " + nights + " night(s)");
            return reservationId;
        } else {
            System.out.println("Error: Failed to create reservation");
            return -1;
        }
    }

    /**
     * View all reservations
     */
    public void viewAllReservations() {
        List<Reservation> reservations = reservationDAO.getAllReservations();
        
        if (reservations.isEmpty()) {
            System.out.println("\nNo reservations found.");
            return;
        }

        System.out.println("\n" + "=".repeat(120));
        System.out.printf("%-8s %-20s %-12s %-12s %-12s %-12s %-15s%n",
                "ID", "Customer", "Room", "Check-In", "Check-Out", "Amount", "Status");
        System.out.println("=".repeat(120));

        for (Reservation r : reservations) {
            System.out.printf("%-8d %-20s %-12s %-12s %-12s ₹%-11.2f %-15s%n",
                    r.getReservationId(),
                    truncate(r.getCustomerName(), 20),
                    r.getRoomNumber(),
                    r.getCheckInDate(),
                    r.getCheckOutDate(),
                    r.getTotalAmount(),
                    r.getStatus());
        }
        System.out.println("=".repeat(120));
    }

    /**
     * View reservation by ID
     */
    public void viewReservationById(int reservationId) {
        Reservation reservation = reservationDAO.getReservationById(reservationId);
        
        if (reservation == null) {
            System.out.println("\nReservation not found with ID: " + reservationId);
            return;
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("RESERVATION DETAILS");
        System.out.println("=".repeat(60));
        System.out.println("Reservation ID    : " + reservation.getReservationId());
        System.out.println("Customer Name     : " + reservation.getCustomerName());
        System.out.println("Room Number       : " + reservation.getRoomNumber());
        System.out.println("Check-In Date     : " + reservation.getCheckInDate());
        System.out.println("Check-Out Date    : " + reservation.getCheckOutDate());
        System.out.println("Number of Nights  : " + reservation.getNumberOfNights());
        System.out.println("Total Amount      : ₹" + String.format("%.2f", reservation.getTotalAmount()));
        System.out.println("Status            : " + reservation.getStatus());
        System.out.println("Created At        : " + reservation.getCreatedAt());
        System.out.println("=".repeat(60));
    }

    /**
     * Cancel reservation
     */
    public void cancelReservation(int reservationId) {
        Reservation reservation = reservationDAO.getReservationById(reservationId);
        
        if (reservation == null) {
            System.out.println("\nReservation not found with ID: " + reservationId);
            return;
        }

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            System.out.println("\nReservation is already cancelled.");
            return;
        }

        if (reservation.getStatus() == ReservationStatus.CHECKED_OUT) {
            System.out.println("\nCannot cancel a completed reservation.");
            return;
        }

        // Cancel the reservation
        boolean success = reservationDAO.deleteReservation(reservationId);
        
        if (success) {
            // Update room status back to AVAILABLE
            roomDAO.updateRoomStatus(reservation.getRoomId(), RoomStatus.AVAILABLE);
            System.out.println("\n✓ Reservation cancelled successfully!");
            System.out.println("Reservation ID: " + reservationId);
            System.out.println("Room " + reservation.getRoomNumber() + " is now available.");
        } else {
            System.out.println("\nError: Failed to cancel reservation");
        }
    }

    /**
     * View all available rooms
     */
    public void viewAvailableRooms() {
        List<Room> rooms = roomDAO.getAvailableRooms();
        
        if (rooms.isEmpty()) {
            System.out.println("\nNo available rooms at the moment.");
            return;
        }

        System.out.println("\n" + "=".repeat(100));
        System.out.printf("%-8s %-12s %-12s %-15s %-10s %-12s%n",
                "ID", "Room No", "Type", "Price/Night", "Floor", "Capacity");
        System.out.println("=".repeat(100));

        for (Room room : rooms) {
            System.out.printf("%-8d %-12s %-12s ₹%-14.2f %-10d %-12d%n",
                    room.getRoomId(),
                    room.getRoomNumber(),
                    room.getRoomType(),
                    room.getPricePerNight(),
                    room.getFloorNumber(),
                    room.getMaxOccupancy());
        }
        System.out.println("=".repeat(100));
    }

    /**
     * View available rooms by type
     */
    public void viewAvailableRoomsByType(RoomType roomType) {
        List<Room> rooms = roomDAO.getAvailableRoomsByType(roomType);
        
        if (rooms.isEmpty()) {
            System.out.println("\nNo available " + roomType + " rooms at the moment.");
            return;
        }

        System.out.println("\nAvailable " + roomType + " Rooms:");
        System.out.println("=".repeat(100));
        System.out.printf("%-8s %-12s %-15s %-10s %-12s%n",
                "ID", "Room No", "Price/Night", "Floor", "Capacity");
        System.out.println("=".repeat(100));

        for (Room room : rooms) {
            System.out.printf("%-8d %-12s ₹%-14.2f %-10d %-12d%n",
                    room.getRoomId(),
                    room.getRoomNumber(),
                    room.getPricePerNight(),
                    room.getFloorNumber(),
                    room.getMaxOccupancy());
        }
        System.out.println("=".repeat(100));
    }

    /**
     * Get room by ID
     */
    public Room getRoomById(int roomId) {
        return roomDAO.getRoomById(roomId);
    }

    /**
     * Truncate string for display
     */
    private String truncate(String str, int length) {
        if (str == null) return "";
        return str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }
}