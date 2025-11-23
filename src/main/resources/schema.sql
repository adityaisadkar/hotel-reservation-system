-- Hotel Reservation System Database Schema
-- Author: Aditya Isadkar
-- Date: 2025-11-23

-- Create database
CREATE DATABASE IF NOT EXISTS hotel_reservation_db;
USE hotel_reservation_db;

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS rooms;

-- Create Rooms Table
CREATE TABLE rooms (
    room_id INT PRIMARY KEY AUTO_INCREMENT,
    room_number VARCHAR(10) UNIQUE NOT NULL,
    room_type ENUM('SINGLE', 'DOUBLE', 'SUITE', 'DELUXE') NOT NULL,
    price_per_night DECIMAL(10, 2) NOT NULL,
    status ENUM('AVAILABLE', 'OCCUPIED', 'MAINTENANCE') DEFAULT 'AVAILABLE',
    floor_number INT,
    max_occupancy INT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create Customers Table
CREATE TABLE customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone_number VARCHAR(15) NOT NULL,
    id_proof VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create Reservations Table
CREATE TABLE reservations (
    reservation_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    room_id INT NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    total_amount DECIMAL(10, 2),
    status ENUM('CONFIRMED', 'CHECKED_IN', 'CHECKED_OUT', 'CANCELLED') DEFAULT 'CONFIRMED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE CASCADE,
    CONSTRAINT check_dates CHECK (check_out_date > check_in_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create Indexes for better query performance
CREATE INDEX idx_room_status ON rooms(status);
CREATE INDEX idx_room_type ON rooms(room_type);
CREATE INDEX idx_customer_email ON customers(email);
CREATE INDEX idx_customer_phone ON customers(phone_number);
CREATE INDEX idx_reservation_dates ON reservations(check_in_date, check_out_date);
CREATE INDEX idx_reservation_status ON reservations(status);

-- Insert Sample Room Data
INSERT INTO rooms (room_number, room_type, price_per_night, floor_number, max_occupancy) VALUES
('101', 'SINGLE', 1500.00, 1, 1),
('102', 'DOUBLE', 2500.00, 1, 2),
('103', 'DOUBLE', 2500.00, 1, 2),
('201', 'SUITE', 5000.00, 2, 4),
('202', 'DELUXE', 7500.00, 2, 4),
('301', 'SINGLE', 1500.00, 3, 1),
('302', 'SUITE', 5000.00, 3, 4),
('303', 'DOUBLE', 2500.00, 3, 2),
('401', 'DELUXE', 7500.00, 4, 4),
('402', 'SUITE', 5000.00, 4, 4);

-- Insert Sample Customer Data (for testing)
INSERT INTO customers (first_name, last_name, email, phone_number, id_proof) VALUES
('John', 'Doe', 'john.doe@email.com', '9876543210', 'AADHAR123456'),
('Jane', 'Smith', 'jane.smith@email.com', '9876543211', 'PAN987654');

-- Insert Sample Reservation Data (for testing)
INSERT INTO reservations (customer_id, room_id, check_in_date, check_out_date, total_amount, status) VALUES
(1, 2, '2025-11-25', '2025-11-27', 5000.00, 'CONFIRMED'),
(2, 4, '2025-11-28', '2025-11-30', 10000.00, 'CONFIRMED');

-- Update room status for booked rooms
UPDATE rooms SET status = 'OCCUPIED' WHERE room_id IN (2, 4);

-- Verify data
SELECT 'Rooms Created:' AS Info, COUNT(*) AS Count FROM rooms
UNION ALL
SELECT 'Customers Created:', COUNT(*) FROM customers
UNION ALL
SELECT 'Reservations Created:', COUNT(*) FROM reservations;

-- Show all data
SELECT 'Available Rooms:' AS Section;
SELECT room_id, room_number, room_type, price_per_night, status FROM rooms WHERE status = 'AVAILABLE';

SELECT 'All Reservations:' AS Section;
SELECT 
    r.reservation_id,
    CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
    rm.room_number,
    r.check_in_date,
    r.check_out_date,
    r.total_amount,
    r.status
FROM reservations r
JOIN customers c ON r.customer_id = c.customer_id
JOIN rooms rm ON r.room_id = rm.room_id;

-- Database setup complete
SELECT '✓ Database setup completed successfully!' AS Status;