# 🏨 Hotel Reservation Management System

A comprehensive **Command-Line Interface (CLI)** application for managing hotel reservations, built with **Java**, **JDBC**, and **MySQL**. This system demonstrates enterprise-level software architecture with layered design, database connectivity, and CRUD operations.

---

## 📋 Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [System Architecture](#system-architecture)
- [Database Schema](#database-schema)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Local Installation](#local-installation)
  - [Run on GitHub Codespaces](#run-on-github-codespaces)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [API/DAO Methods](#apidao-methods)
- [Future Enhancements](#future-enhancements)
- [Contributing](#contributing)
- [License](#license)

---

## ✨ Features

- ✅ **Room Management** - View available rooms, filter by type (Single, Double, Suite, Deluxe)
- ✅ **Customer Management** - Automatic customer creation or linking existing profiles
- ✅ **Reservation System** - Create, view, and cancel bookings with intelligent date validation
- ✅ **Availability Checking** - Real-time room availability with conflict detection
- ✅ **Price Calculation** - Automatic total cost computation based on nights and room rate
- ✅ **Status Tracking** - Monitor reservation status (Confirmed, Checked-In, Checked-Out, Cancelled)
- ✅ **Data Validation** - Email, phone number, and date range validation
- ✅ **SQL Injection Protection** - PreparedStatements for secure database operations

---

## 🛠️ Technologies Used

| Technology    | Purpose                                  |
| ------------- | ---------------------------------------- |
| **Java 17**   | Core programming language                |
| **JDBC**      | Database connectivity                    |
| **MySQL 8.0** | Relational database                      |
| **Maven**     | Dependency management & build automation |
| **Git**       | Version control                          |

---

## 🏗️ System Architecture

```
┌─────────────────────────────────────────┐
│         ConsoleUI (Presentation)        │  ← User Interaction
├─────────────────────────────────────────┤
│     ReservationService (Business)       │  ← Validation & Logic
├─────────────────────────────────────────┤
│  RoomDAO | CustomerDAO | ReservationDAO │  ← Database Operations
├─────────────────────────────────────────┤
│      DatabaseConnection (Utility)       │  ← JDBC Connection Pool
├─────────────────────────────────────────┤
│           MySQL Database                │  ← Data Persistence
└─────────────────────────────────────────┘
```

**Design Patterns Used:**

- **Singleton Pattern** - Database connection management
- **DAO Pattern** - Separation of data access logic
- **Layered Architecture** - Clear separation of concerns
- **MVC-inspired** - Model-View-Controller principles

---

## 🗄️ Database Schema

### **ER Diagram**

```
┌─────────────┐         ┌──────────────────┐         ┌─────────────┐
│   Rooms     │         │   Reservations   │         │  Customers  │
├─────────────┤         ├──────────────────┤         ├─────────────┤
│ room_id (PK)│◄────────│ reservation_id   │────────►│customer_id  │
│ room_number │         │ customer_id (FK) │         │ first_name  │
│ room_type   │         │ room_id (FK)     │         │ last_name   │
│ price       │         │ check_in_date    │         │ email       │
│ status      │         │ check_out_date   │         │ phone       │
│ floor       │         │ total_amount     │         │ id_proof    │
│ capacity    │         │ status           │         └─────────────┘
└─────────────┘         └──────────────────┘
```

### **Tables:**

**1. `rooms`** - Hotel room inventory

- Primary Key: `room_id` (AUTO_INCREMENT)
- Unique: `room_number`
- ENUM: `room_type` (SINGLE, DOUBLE, SUITE, DELUXE)
- ENUM: `status` (AVAILABLE, OCCUPIED, MAINTENANCE)

**2. `customers`** - Guest information

- Primary Key: `customer_id` (AUTO_INCREMENT)
- Unique: `email`
- Timestamp: `created_at`

**3. `reservations`** - Booking records

- Primary Key: `reservation_id` (AUTO_INCREMENT)
- Foreign Keys: `customer_id`, `room_id`
- ENUM: `status` (CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED)
- CHECK Constraint: `check_out_date > check_in_date`
- Timestamps: `created_at`, `updated_at`

---

## 🚀 Getting Started

### Prerequisites

Before running this project, ensure you have:

- ☕ **Java JDK 11 or 17** - [Download here](https://www.oracle.com/java/technologies/downloads/)
- 🗄️ **MySQL 8.0+** - [Download here](https://dev.mysql.com/downloads/mysql/)
- 📦 **Maven 3.6+** - [Download here](https://maven.apache.org/download.cgi)
- 🔧 **Git** - [Download here](https://git-scm.com/downloads)

---

### 🖥️ Local Installation

#### **1. Clone the Repository**

```bash
git clone https://github.com/YOUR_USERNAME/hotel-reservation-system.git
cd hotel-reservation-system
```

#### **2. Set Up MySQL Database**

```bash
# Login to MySQL
mysql -u root -p

# Run the following SQL commands:
```

```sql
-- Create database
CREATE DATABASE hotel_reservation_db;
USE hotel_reservation_db;

-- Create tables
CREATE TABLE rooms (
    room_id INT PRIMARY KEY AUTO_INCREMENT,
    room_number VARCHAR(10) UNIQUE NOT NULL,
    room_type ENUM('SINGLE', 'DOUBLE', 'SUITE', 'DELUXE') NOT NULL,
    price_per_night DECIMAL(10, 2) NOT NULL,
    status ENUM('AVAILABLE', 'OCCUPIED', 'MAINTENANCE') DEFAULT 'AVAILABLE',
    floor_number INT,
    max_occupancy INT
);

CREATE TABLE customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone_number VARCHAR(15) NOT NULL,
    id_proof VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

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
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (room_id) REFERENCES rooms(room_id),
    CONSTRAINT check_dates CHECK (check_out_date > check_in_date)
);

-- Insert sample data
INSERT INTO rooms (room_number, room_type, price_per_night, floor_number, max_occupancy) VALUES
('101', 'SINGLE', 1500.00, 1, 1),
('102', 'DOUBLE', 2500.00, 1, 2),
('103', 'DOUBLE', 2500.00, 1, 2),
('201', 'SUITE', 5000.00, 2, 4),
('202', 'DELUXE', 7500.00, 2, 4),
('301', 'SINGLE', 1500.00, 3, 1),
('302', 'SUITE', 5000.00, 3, 4);
```

#### **3. Configure Database Connection**

Edit `src/main/resources/application.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/hotel_reservation_db
db.username=root
db.password=YOUR_MYSQL_PASSWORD
db.driver=com.mysql.cj.jdbc.Driver
```

#### **4. Build the Project**

```bash
mvn clean compile
```

#### **5. Run the Application**

```bash
mvn exec:java
```

**Alternative - Run JAR:**

```bash
# Create JAR file
mvn clean package

# Run JAR
java -cp target/hotel-reservation-system-1.0-SNAPSHOT.jar com.hotel.Main
```

---

### ☁️ Run on GitHub Codespaces

**Experience the project without local setup!**

#### **🎯 Quick Start (3 Steps):**

1. **Open this repository on GitHub**
2. **Change URL from `github.com` to `github.dev`**
   ```
   https://github.com/YOUR_USERNAME/hotel-reservation-system
   ↓
   https://github.dev/YOUR_USERNAME/hotel-reservation-system
   ```
3. **Wait for VS Code to load in browser**

#### **📝 Detailed Steps:**

**Step 1: Create a Codespace**

- Go to your repository on GitHub
- Click the **green "Code"** button
- Select **"Codespaces"** tab
- Click **"Create codespace on main"**

**Step 2: Install MySQL in Codespace**

```bash
# Install MySQL
sudo apt-get update
sudo apt-get install -y mysql-server

# Start MySQL service
sudo service mysql start

# Set root password and create database
sudo mysql -e "ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'codespace123';"
sudo mysql -u root -pcodespace123 < src/main/resources/schema.sql
```

**Step 3: Configure Application**

Update `src/main/resources/application.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/hotel_reservation_db
db.username=root
db.password=codespace123
db.driver=com.mysql.cj.jdbc.Driver
```

**Step 4: Run the Application**

```bash
mvn clean compile
mvn exec:java
```

**🎉 You're now running the hotel reservation system in the cloud!**

---

## 📖 Usage

### Main Menu Options:

```
------------------------------------------------------------
MAIN MENU
------------------------------------------------------------
1. View Available Rooms
2. View Available Rooms by Type
3. Create New Reservation
4. View All Reservations
5. View Reservation by ID
6. Cancel Reservation
7. Exit
------------------------------------------------------------
```

### Example Workflow:

**1. View Available Rooms**

```
Enter your choice: 1

====================================================================================================
ID      Room No     Type        Price/Night     Floor       Capacity
====================================================================================================
1       101         SINGLE      ₹1500.00        1           1
2       102         DOUBLE      ₹2500.00        1           2
...
```

**2. Create a Reservation**

```
Enter your choice: 3

--- Customer Information ---
First Name: John
Last Name: Doe
Email: john.doe@email.com
Phone Number (10 digits): 9876543210
ID Proof (Aadhaar/PAN/Passport): AADHAR123456

--- Booking Information ---
Enter Room ID: 2
Check-In Date (YYYY-MM-DD): 2025-11-25
Check-Out Date (YYYY-MM-DD): 2025-11-27

Processing reservation...
✓ Reservation created successfully!
Reservation ID: 1
Room Number: 102
Total Amount: ₹5000.00 for 2 night(s)
```

**3. View Reservations**

```
Enter your choice: 4

========================================================================================================================
ID       Customer            Room        Check-In    Check-Out   Amount      Status
========================================================================================================================
1        John Doe            102         2025-11-25  2025-11-27  ₹5000.00    CONFIRMED
...
```

---

## 📁 Project Structure

```
hotel-reservation-system/
│
├── src/
│   └── main/
│       ├── java/com/hotel/
│       │   ├── Main.java                      # Application entry point
│       │   ├── model/
│       │   │   ├── Room.java                  # Room entity
│       │   │   ├── Customer.java              # Customer entity
│       │   │   └── Reservation.java           # Reservation entity
│       │   ├── dao/
│       │   │   ├── RoomDAO.java               # Room database operations
│       │   │   ├── CustomerDAO.java           # Customer database operations
│       │   │   └── ReservationDAO.java        # Reservation database operations
│       │   ├── service/
│       │   │   └── ReservationService.java    # Business logic layer
│       │   ├── util/
│       │   │   ├── DatabaseConnection.java    # Singleton DB connection
│       │   │   └── InputValidator.java        # Input validation utilities
│       │   └── ui/
│       │       └── ConsoleUI.java             # CLI interface
│       └── resources/
│           ├── application.properties         # Database configuration
│           └── schema.sql                     # Database schema
│
├── target/                                    # Compiled classes (generated)
├── pom.xml                                    # Maven configuration
├── README.md                                  # This file
└── .gitignore                                 # Git ignore rules
```

---

## 🔌 API/DAO Methods

### RoomDAO

- `getAllRooms()` - Retrieve all rooms
- `getRoomById(int id)` - Get room by ID
- `getRoomByNumber(String number)` - Get room by number
- `getAvailableRooms()` - List all available rooms
- `getAvailableRoomsByType(RoomType type)` - Filter by room type
- `updateRoomStatus(int id, RoomStatus status)` - Update availability

### CustomerDAO

- `addCustomer(Customer customer)` - Create new customer
- `getCustomerById(int id)` - Retrieve by ID
- `getCustomerByEmail(String email)` - Lookup by email
- `getCustomerByPhone(String phone)` - Lookup by phone
- `updateCustomer(Customer customer)` - Update details
- `deleteCustomer(int id)` - Remove customer

### ReservationDAO

- `createReservation(Reservation reservation)` - Book a room
- `getReservationById(int id)` - Get reservation details
- `getAllReservations()` - List all bookings
- `getReservationsByCustomer(int customerId)` - Customer history
- `getActiveReservations()` - Current bookings
- `isRoomAvailable(int roomId, LocalDate checkIn, LocalDate checkOut)` - Check availability
- `updateReservationStatus(int id, ReservationStatus status)` - Change status
- `deleteReservation(int id)` - Cancel booking

---

## 🚀 Future Enhancements

- [ ] **REST API** - Convert to Spring Boot REST service
- [ ] **Web Frontend** - React/Angular dashboard
- [ ] **Authentication** - User login with JWT
- [ ] **Payment Integration** - Stripe/Razorpay integration
- [ ] **Email Notifications** - Booking confirmations via JavaMail
- [ ] **Connection Pooling** - HikariCP for performance
- [ ] **Caching** - Redis for frequently accessed data
- [ ] **Unit Tests** - JUnit + Mockito test suite
- [ ] **Docker** - Containerize application
- [ ] **CI/CD Pipeline** - GitHub Actions automation
- [ ] **Search & Filters** - Advanced room search
- [ ] **Reports** - Revenue analytics and occupancy reports
- [ ] **Multi-language Support** - i18n implementation

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

**Your Name**

- GitHub: [@YOUR_USERNAME](https://github.com/YOUR_USERNAME)
- LinkedIn: [Your LinkedIn](https://linkedin.com/in/YOUR_PROFILE)
- Email: your.email@example.com

---

## 🙏 Acknowledgments

- MySQL for robust database management
- Maven community for excellent build tools
- Java community for comprehensive documentation
- Stack Overflow for troubleshooting support

---

## 📊 Project Statistics

![Java](https://img.shields.io/badge/Java-17-orange?style=flat&logo=java)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=flat&logo=mysql)
![Maven](https://img.shields.io/badge/Maven-3.9-red?style=flat&logo=apache-maven)
![License](https://img.shields.io/badge/License-MIT-green?style=flat)

---

**⭐ If you found this project helpful, please give it a star!**
