-- Blood Bank Management System Database Schema
-- Run this in MySQL to create the database and tables

CREATE DATABASE IF NOT EXISTS bloodbank;
USE bloodbank;

-- Users table (for authentication)
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'DONOR', 'RECIPIENT') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Blood groups table
CREATE TABLE IF NOT EXISTS blood_groups (
    blood_group_id INT AUTO_INCREMENT PRIMARY KEY,
    group_name VARCHAR(5) UNIQUE NOT NULL,
    description VARCHAR(255)
);

-- Insert blood groups
INSERT INTO blood_groups (group_name, description) VALUES
('A+', 'A positive'),
('A-', 'A negative'),
('B+', 'B positive'),
('B-', 'B negative'),
('AB+', 'AB positive'),
('AB-', 'AB negative'),
('O+', 'O positive'),
('O-', 'O negative');

-- Donors table
CREATE TABLE IF NOT EXISTS donors (
    donor_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    gender ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,
    date_of_birth DATE NOT NULL,
    blood_group_id INT NOT NULL,
    contact_number VARCHAR(15) NOT NULL,
    address TEXT NOT NULL,
    city VARCHAR(50) NOT NULL,
    state VARCHAR(50) NOT NULL,
    pincode VARCHAR(10) NOT NULL,
    last_donation_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (blood_group_id) REFERENCES blood_groups(blood_group_id)
);

-- Recipients table
CREATE TABLE IF NOT EXISTS recipients (
    recipient_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    contact_number VARCHAR(15) NOT NULL,
    hospital_name VARCHAR(100) NOT NULL,
    hospital_address TEXT NOT NULL,
    city VARCHAR(50) NOT NULL,
    state VARCHAR(50) NOT NULL,
    pincode VARCHAR(10) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Blood inventory
CREATE TABLE IF NOT EXISTS blood_inventory (
    inventory_id INT AUTO_INCREMENT PRIMARY KEY,
    blood_group_id INT NOT NULL,
    units_available INT NOT NULL DEFAULT 0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (blood_group_id) REFERENCES blood_groups(blood_group_id),
    UNIQUE KEY unique_blood_group (blood_group_id)
);

-- Initialize blood inventory with 0 units for all blood groups
INSERT INTO blood_inventory (blood_group_id, units_available) 
SELECT blood_group_id, 0 FROM blood_groups;

-- Blood donation records
CREATE TABLE IF NOT EXISTS donations (
    donation_id INT AUTO_INCREMENT PRIMARY KEY,
    donor_id INT NOT NULL,
    donation_date DATE NOT NULL,
    blood_group_id INT NOT NULL,
    units_donated DECIMAL(5,2) NOT NULL,
    status ENUM('PENDING', 'COMPLETED', 'REJECTED') DEFAULT 'PENDING',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (donor_id) REFERENCES donors(donor_id) ON DELETE CASCADE,
    FOREIGN KEY (blood_group_id) REFERENCES blood_groups(blood_group_id)
);

-- Blood requests
CREATE TABLE IF NOT EXISTS blood_requests (
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    recipient_id INT NOT NULL,
    blood_group_id INT NOT NULL,
    units_required INT NOT NULL,
    request_date DATE NOT NULL,
    required_date DATE NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'COMPLETED') DEFAULT 'PENDING',
    purpose TEXT,
    hospital_name VARCHAR(100) NOT NULL,
    hospital_address TEXT NOT NULL,
    contact_person VARCHAR(100) NOT NULL,
    contact_number VARCHAR(15) NOT NULL,
    admin_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (recipient_id) REFERENCES recipients(recipient_id) ON DELETE CASCADE,
    FOREIGN KEY (blood_group_id) REFERENCES blood_groups(blood_group_id)
);

-- Insert default admin user (password: admin123)
INSERT INTO users (email, password, role) 
VALUES ('admin@bloodbank.com', 'admin123', 'ADMIN');

-- Verify tables created
SHOW TABLES;

-- Display default admin
SELECT * FROM users;
