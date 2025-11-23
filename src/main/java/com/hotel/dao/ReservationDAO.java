package com.hotel.dao;

import com.hotel.model.Reservation;
import com.hotel.model.Reservation.ReservationStatus;
import com.hotel.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Reservation entity
 */
public class ReservationDAO {

    /**
     * Create a new reservation
     */
    public int createReservation(Reservation reservation) {
        String query = "INSERT INTO reservations (customer_id, room_id, check_in_date, check_out_date, total_amount, status) " +
                      "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, reservation.getCustomerId());
            pstmt.setInt(2, reservation.getRoomId());
            pstmt.setDate(3, Date.valueOf(reservation.getCheckInDate()));
            pstmt.setDate(4, Date.valueOf(reservation.getCheckOutDate()));
            pstmt.setDouble(5, reservation.getTotalAmount());
            pstmt.setString(6, reservation.getStatus().name());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating reservation: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Get reservation by ID
     */
    public Reservation getReservationById(int reservationId) {
        String query = "SELECT r.*, c.first_name, c.last_name, rm.room_number " +
                      "FROM reservations r " +
                      "JOIN customers c ON r.customer_id = c.customer_id " +
                      "JOIN rooms rm ON r.room_id = rm.room_id " +
                      "WHERE r.reservation_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, reservationId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractReservationFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching reservation by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Get all reservations
     */
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT r.*, c.first_name, c.last_name, rm.room_number " +
                      "FROM reservations r " +
                      "JOIN customers c ON r.customer_id = c.customer_id " +
                      "JOIN rooms rm ON r.room_id = rm.room_id " +
                      "ORDER BY r.created_at DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                reservations.add(extractReservationFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all reservations: " + e.getMessage());
        }
        return reservations;
    }

    /**
     * Get reservations by customer ID
     */
    public List<Reservation> getReservationsByCustomer(int customerId) {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT r.*, c.first_name, c.last_name, rm.room_number " +
                      "FROM reservations r " +
                      "JOIN customers c ON r.customer_id = c.customer_id " +
                      "JOIN rooms rm ON r.room_id = rm.room_id " +
                      "WHERE r.customer_id = ? " +
                      "ORDER BY r.check_in_date DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                reservations.add(extractReservationFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching reservations by customer: " + e.getMessage());
        }
        return reservations;
    }

    /**
     * Get active reservations (CONFIRMED or CHECKED_IN)
     */
    public List<Reservation> getActiveReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT r.*, c.first_name, c.last_name, rm.room_number " +
                      "FROM reservations r " +
                      "JOIN customers c ON r.customer_id = c.customer_id " +
                      "JOIN rooms rm ON r.room_id = rm.room_id " +
                      "WHERE r.status IN ('CONFIRMED', 'CHECKED_IN') " +
                      "ORDER BY r.check_in_date";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                reservations.add(extractReservationFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching active reservations: " + e.getMessage());
        }
        return reservations;
    }

    /**
     * Check if room is available for given dates
     */
    public boolean isRoomAvailable(int roomId, LocalDate checkIn, LocalDate checkOut) {
        String query = "SELECT COUNT(*) FROM reservations " +
                      "WHERE room_id = ? " +
                      "AND status IN ('CONFIRMED', 'CHECKED_IN') " +
                      "AND ((check_in_date <= ? AND check_out_date > ?) " +
                      "OR (check_in_date < ? AND check_out_date >= ?) " +
                      "OR (check_in_date >= ? AND check_out_date <= ?))";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, roomId);
            pstmt.setDate(2, Date.valueOf(checkIn));
            pstmt.setDate(3, Date.valueOf(checkIn));
            pstmt.setDate(4, Date.valueOf(checkOut));
            pstmt.setDate(5, Date.valueOf(checkOut));
            pstmt.setDate(6, Date.valueOf(checkIn));
            pstmt.setDate(7, Date.valueOf(checkOut));

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0; // Room is available if count is 0
            }
        } catch (SQLException e) {
            System.err.println("Error checking room availability: " + e.getMessage());
        }
        return false;
    }

    /**
     * Update reservation status
     */
    public boolean updateReservationStatus(int reservationId, ReservationStatus status) {
        String query = "UPDATE reservations SET status = ? WHERE reservation_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, status.name());
            pstmt.setInt(2, reservationId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating reservation status: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete/Cancel reservation
     */
    public boolean deleteReservation(int reservationId) {
        String query = "UPDATE reservations SET status = 'CANCELLED' WHERE reservation_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, reservationId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error cancelling reservation: " + e.getMessage());
            return false;
        }
    }

    /**
     * Extract Reservation object from ResultSet
     */
    private Reservation extractReservationFromResultSet(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setReservationId(rs.getInt("reservation_id"));
        reservation.setCustomerId(rs.getInt("customer_id"));
        reservation.setRoomId(rs.getInt("room_id"));
        reservation.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
        reservation.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());
        reservation.setTotalAmount(rs.getDouble("total_amount"));
        reservation.setStatus(ReservationStatus.valueOf(rs.getString("status")));
        reservation.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        reservation.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        
        // Additional fields from JOIN
        reservation.setCustomerName(rs.getString("first_name") + " " + rs.getString("last_name"));
        reservation.setRoomNumber(rs.getString("room_number"));
        
        return reservation;
    }
}