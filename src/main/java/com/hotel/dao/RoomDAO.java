package com.hotel.dao;

import com.hotel.model.Room;
import com.hotel.model.Room.RoomStatus;
import com.hotel.model.Room.RoomType;
import com.hotel.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Room entity
 */
public class RoomDAO {

    /**
     * Get all rooms
     */
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT * FROM rooms ORDER BY room_number";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                rooms.add(extractRoomFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all rooms: " + e.getMessage());
        }
        return rooms;
    }

    /**
     * Get room by ID
     */
    public Room getRoomById(int roomId) {
        String query = "SELECT * FROM rooms WHERE room_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, roomId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractRoomFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching room by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Get room by room number
     */
    public Room getRoomByNumber(String roomNumber) {
        String query = "SELECT * FROM rooms WHERE room_number = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, roomNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractRoomFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching room by number: " + e.getMessage());
        }
        return null;
    }

    /**
     * Get all available rooms
     */
    public List<Room> getAvailableRooms() {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT * FROM rooms WHERE status = 'AVAILABLE' ORDER BY room_number";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                rooms.add(extractRoomFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching available rooms: " + e.getMessage());
        }
        return rooms;
    }

    /**
     * Get available rooms by type
     */
    public List<Room> getAvailableRoomsByType(RoomType roomType) {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT * FROM rooms WHERE status = 'AVAILABLE' AND room_type = ? ORDER BY room_number";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, roomType.name());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                rooms.add(extractRoomFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching available rooms by type: " + e.getMessage());
        }
        return rooms;
    }

    /**
     * Update room status
     */
    public boolean updateRoomStatus(int roomId, RoomStatus status) {
        String query = "UPDATE rooms SET status = ? WHERE room_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, status.name());
            pstmt.setInt(2, roomId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating room status: " + e.getMessage());
            return false;
        }
    }

    /**
     * Add a new room
     */
    public boolean addRoom(Room room) {
        String query = "INSERT INTO rooms (room_number, room_type, price_per_night, status, floor_number, max_occupancy) " +
                      "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, room.getRoomNumber());
            pstmt.setString(2, room.getRoomType().name());
            pstmt.setDouble(3, room.getPricePerNight());
            pstmt.setString(4, room.getStatus().name());
            pstmt.setInt(5, room.getFloorNumber());
            pstmt.setInt(6, room.getMaxOccupancy());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error adding room: " + e.getMessage());
            return false;
        }
    }

    /**
     * Extract Room object from ResultSet
     */
    private Room extractRoomFromResultSet(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setRoomId(rs.getInt("room_id"));
        room.setRoomNumber(rs.getString("room_number"));
        room.setRoomType(RoomType.valueOf(rs.getString("room_type")));
        room.setPricePerNight(rs.getDouble("price_per_night"));
        room.setStatus(RoomStatus.valueOf(rs.getString("status")));
        room.setFloorNumber(rs.getInt("floor_number"));
        room.setMaxOccupancy(rs.getInt("max_occupancy"));
        return room;
    }
}