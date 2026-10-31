-- Payroll System database setup
-- Run this once in MySQL before starting the app:
--   mysql -u root -p < schema.sql

CREATE DATABASE IF NOT EXISTS payroll_db;
USE payroll_db;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(64) NOT NULL          -- SHA-256 hash
);

CREATE TABLE IF NOT EXISTS employees (
    emp_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    department VARCHAR(50),
    designation VARCHAR(100),
    join_date DATE NOT NULL,
    basic_salary DOUBLE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Active'
);

CREATE TABLE IF NOT EXISTS payroll (
    id INT AUTO_INCREMENT PRIMARY KEY,
    emp_id INT NOT NULL,
    pay_month INT NOT NULL,
    pay_year INT NOT NULL,
    basic DOUBLE,
    hra DOUBLE,
    da DOUBLE,
    bonus DOUBLE,
    gross DOUBLE,
    pf DOUBLE,
    tax DOUBLE,
    other_deduction DOUBLE,
    net_salary DOUBLE,
    UNIQUE KEY uq_emp_month (emp_id, pay_month, pay_year),
    FOREIGN KEY (emp_id) REFERENCES employees(emp_id) ON DELETE CASCADE
);

-- Default login: admin / admin123  (demo only!)
INSERT IGNORE INTO users (username, password)
VALUES ('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9');
