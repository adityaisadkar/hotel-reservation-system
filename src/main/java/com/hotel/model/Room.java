package com.hotel.model;

/**
 * Room entity representing a hotel room
 */
public class Room {
    private int roomId;
    private String roomNumber;
    private RoomType roomType;
    private double pricePerNight;
    private RoomStatus status;
    private int floorNumber;
    private int maxOccupancy;

    // Enums
    public enum RoomType {
        SINGLE, DOUBLE, SUITE, DELUXE
    }

    public enum RoomStatus {
        AVAILABLE, OCCUPIED, MAINTENANCE
    }

    // Constructors
    public Room() {}

    public Room(int roomId, String roomNumber, RoomType roomType, 
                double pricePerNight, RoomStatus status, int floorNumber, int maxOccupancy) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.status = status;
        this.floorNumber = floorNumber;
        this.maxOccupancy = maxOccupancy;
    }

    // Getters and Setters
    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public int getMaxOccupancy() {
        return maxOccupancy;
    }

    public void setMaxOccupancy(int maxOccupancy) {
        this.maxOccupancy = maxOccupancy;
    }

    @Override
    public String toString() {
        return String.format("Room[ID=%d, Number=%s, Type=%s, Price=₹%.2f, Status=%s, Floor=%d, MaxOccupancy=%d]",
                roomId, roomNumber, roomType, pricePerNight, status, floorNumber, maxOccupancy);
    }
}