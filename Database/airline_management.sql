-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: May 03, 2026 at 04:43 PM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `airline_management`
--

-- --------------------------------------------------------

--
-- Table structure for table `bookings`
--

CREATE TABLE `bookings` (
  `id` int(11) NOT NULL,
  `booking_id` varchar(20) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `flight_id` int(11) DEFAULT NULL,
  `passenger_name` varchar(100) DEFAULT NULL,
  `seat_number` varchar(10) DEFAULT NULL,
  `seat_class` enum('Economy','Business','First') DEFAULT NULL,
  `original_price` decimal(10,2) DEFAULT NULL,
  `discount` decimal(5,2) DEFAULT 0.00,
  `final_price` decimal(10,2) DEFAULT NULL,
  `payment_method` enum('Bank','Card','Bkash','Nagad','Cash','Admin','Staff') DEFAULT NULL,
  `status` enum('confirmed','cancelled') DEFAULT 'confirmed',
  `booked_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `bookings`
--

INSERT INTO `bookings` (`id`, `booking_id`, `user_id`, `flight_id`, `passenger_name`, `seat_number`, `seat_class`, `original_price`, `discount`, `final_price`, `payment_method`, `status`, `booked_at`) VALUES
(1, 'BK1777813947308', 3, 4, 'sumaita', 'E1', 'Economy', 600.00, 50.00, 300.00, 'Bkash', 'confirmed', '2026-05-03 13:12:27'),
(2, 'BK1777814131884', 3, 5, 'sumaita', 'F1', 'First', 1500.00, 20.00, 1200.00, 'Bkash', 'cancelled', '2026-05-03 13:15:31'),
(3, 'BK1777817258121', 1, 4, 'admin', 'B1', 'Business', 1000.00, 85.00, 150.00, 'Admin', 'confirmed', '2026-05-03 14:07:38'),
(4, 'BK1777817342054', 2, 1, 'staff', 'F1', 'First', 12000.00, 70.00, 3600.00, 'Staff', 'confirmed', '2026-05-03 14:09:02'),
(5, 'BK1777817602443', 1, 5, 'admin', 'B1', 'Business', 1000.00, 85.00, 150.00, 'Admin', 'confirmed', '2026-05-03 14:13:22'),
(6, 'BK1777817937479', 1, 2, 'admin', 'B1', 'Business', 8000.00, 85.00, 1200.00, 'Admin', 'confirmed', '2026-05-03 14:18:57'),
(7, 'BK1777818613114', 1, 3, 'admin', 'F1', 'First', 12000.00, 85.00, 1800.00, 'Bkash', 'confirmed', '2026-05-03 14:30:13'),
(8, 'BK1777818679757', 3, 2, 'sumaita', 'B2', 'Business', 8000.00, 50.00, 4000.00, 'Bkash', 'cancelled', '2026-05-03 14:31:19'),
(9, 'BK1777818774874', 2, 1, 'staff', 'E1', 'Economy', 5000.00, 70.00, 1500.00, 'Bkash', 'confirmed', '2026-05-03 14:32:54');

-- --------------------------------------------------------

--
-- Table structure for table `coupons`
--

CREATE TABLE `coupons` (
  `id` int(11) NOT NULL,
  `coupon_code` varchar(20) DEFAULT NULL,
  `discount_percent` decimal(5,2) DEFAULT NULL,
  `expiry_date` date DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `coupons`
--

INSERT INTO `coupons` (`id`, `coupon_code`, `discount_percent`, `expiry_date`, `is_active`) VALUES
(1, 'SAVE20', 20.00, '2026-12-31', 1),
(2, 'GET10', 10.00, '2026-06-03', 1),
(3, 'BUY40', 40.00, '2026-05-22', 1),
(4, 'SAVE10', 10.00, '2026-12-31', 1),
(5, 'SAVE30', 30.00, '2026-12-31', 1),
(6, 'FLY50', 50.00, '2026-12-31', 1);

-- --------------------------------------------------------

--
-- Table structure for table `employees`
--

CREATE TABLE `employees` (
  `id` int(11) NOT NULL,
  `employee_id` varchar(20) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `role` enum('staff','admin') DEFAULT NULL,
  `staff_category` enum('pilot','crew') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `employees`
--

INSERT INTO `employees` (`id`, `employee_id`, `user_id`, `role`, `staff_category`) VALUES
(1, 'EMP-ADMIN-001', 1, 'admin', NULL),
(2, 'EMP-STAFF-001', 2, 'staff', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `flights`
--

CREATE TABLE `flights` (
  `id` int(11) NOT NULL,
  `flight_no` varchar(20) DEFAULT NULL,
  `source` varchar(100) DEFAULT NULL,
  `destination` varchar(100) DEFAULT NULL,
  `flight_date` date DEFAULT NULL,
  `flight_time` time DEFAULT NULL,
  `economy_price` decimal(10,2) DEFAULT NULL,
  `business_price` decimal(10,2) DEFAULT NULL,
  `first_price` decimal(10,2) DEFAULT NULL,
  `total_seats` int(11) DEFAULT 150,
  `available_seats` int(11) DEFAULT 150,
  `status` enum('scheduled','cancelled','completed') DEFAULT 'scheduled'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `flights`
--

INSERT INTO `flights` (`id`, `flight_no`, `source`, `destination`, `flight_date`, `flight_time`, `economy_price`, `business_price`, `first_price`, `total_seats`, `available_seats`, `status`) VALUES
(1, 'BD-101', 'Dhaka', 'Dubai', '2026-05-10', '08:00:00', 5000.00, 8000.00, 12000.00, 150, 148, 'scheduled'),
(2, 'BD-202', 'Dhaka', 'London', '2026-05-11', '14:30:00', 5000.00, 8000.00, 12000.00, 150, 149, 'scheduled'),
(3, 'BD-303', 'Dhaka', 'New York', '2026-05-12', '22:00:00', 5000.00, 8000.00, 12000.00, 150, 149, 'scheduled'),
(4, 'BD-102', 'Dubai', 'Dhaka', '2026-05-06', '08:00:00', 600.00, 1000.00, 1500.00, 50, 48, 'scheduled'),
(5, 'BD-103', 'Singapore', 'Dhaka', '2026-05-12', '09:00:00', 500.00, 1000.00, 1500.00, 50, 48, 'scheduled');

-- --------------------------------------------------------

--
-- Table structure for table `seats`
--

CREATE TABLE `seats` (
  `id` int(11) NOT NULL,
  `flight_id` int(11) DEFAULT NULL,
  `seat_number` varchar(10) DEFAULT NULL,
  `seat_class` enum('Economy','Business','First') DEFAULT NULL,
  `is_booked` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `full_name` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `role` enum('user','staff','admin') DEFAULT 'user',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `full_name`, `email`, `username`, `password`, `role`, `created_at`) VALUES
(1, 'Admin', 'admin@airline.com', 'admin', 'admin123', 'admin', '2026-05-03 09:06:07'),
(2, 'Staff One', 'staff@airline.com', 'staff', 'staff123', 'staff', '2026-05-03 09:06:07'),
(3, 'Sumaita', 'sumaita@airline.com', 'sumaita', 'sumaita123', 'user', '2026-05-03 10:06:50');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `bookings`
--
ALTER TABLE `bookings`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `booking_id` (`booking_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `flight_id` (`flight_id`);

--
-- Indexes for table `coupons`
--
ALTER TABLE `coupons`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `coupon_code` (`coupon_code`);

--
-- Indexes for table `employees`
--
ALTER TABLE `employees`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `employee_id` (`employee_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `flights`
--
ALTER TABLE `flights`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `flight_no` (`flight_no`);

--
-- Indexes for table `seats`
--
ALTER TABLE `seats`
  ADD PRIMARY KEY (`id`),
  ADD KEY `flight_id` (`flight_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `bookings`
--
ALTER TABLE `bookings`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `coupons`
--
ALTER TABLE `coupons`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `employees`
--
ALTER TABLE `employees`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `flights`
--
ALTER TABLE `flights`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `seats`
--
ALTER TABLE `seats`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `bookings`
--
ALTER TABLE `bookings`
  ADD CONSTRAINT `bookings_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `bookings_ibfk_2` FOREIGN KEY (`flight_id`) REFERENCES `flights` (`id`);

--
-- Constraints for table `employees`
--
ALTER TABLE `employees`
  ADD CONSTRAINT `employees_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `seats`
--
ALTER TABLE `seats`
  ADD CONSTRAINT `seats_ibfk_1` FOREIGN KEY (`flight_id`) REFERENCES `flights` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
