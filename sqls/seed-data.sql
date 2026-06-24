USE `student_db`;

CREATE TABLE IF NOT EXISTS `students` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `student_id` VARCHAR(255) NOT NULL UNIQUE,
    `first_name` VARCHAR(255),
    `last_name` VARCHAR(255),
    `email` VARCHAR(255) NOT NULL UNIQUE,
    `programme` VARCHAR(255)
);

INSERT INTO `students` (`id`, `student_id`, `first_name`, `last_name`, `email`, `programme`) VALUES
(1, 'B032510001', 'Zayn', 'Malik', 'B032510001@utem.edu.my', 'Software Development'),
(2, 'B032510002', 'Harry', 'Styles', 'B032510002@utem.edu.my', 'Software Development'),
(3, 'B032510003', 'Niall', 'Horan', 'B032510003@utem.edu.my', 'Software Development'),
(4, 'B032510034', 'Adib', 'Nazli', 'B032510034@utem.edu.my', 'Software Development')
ON DUPLICATE KEY UPDATE `student_id` = VALUES(`student_id`);

USE `enrollment_db`;

CREATE TABLE IF NOT EXISTS `courses` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `course_code` VARCHAR(50) NOT NULL UNIQUE,
    `title` VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS `enrollments` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `student_id` VARCHAR(255) NOT NULL,
    `course_id` BIGINT NOT NULL,
    `semester` VARCHAR(255) NOT NULL,
    `status` VARCHAR(255) NOT NULL,
    FOREIGN KEY (`course_id`) REFERENCES `courses`(`id`)
);

INSERT INTO `courses` (`id`, `course_code`, `title`) VALUES
(1, 'BITS1223', 'Introduction to Computer Science'),
(2, 'BITS1233', 'Software Architecture & Microservices'),
(3, 'BITS1112', 'Programming Techniques')
ON DUPLICATE KEY UPDATE `course_code` = VALUES(`course_code`);

INSERT INTO `enrollments` (`student_id`, `course_id`, `semester`, `status`) VALUES
('B032510001', 1, '2025/2026', 'ENROLLED'),
('B032510002', 2, '2025/2026', 'PENDING'),
('B032510003', 2, '2025/2026', 'ENROLLED');

USE `booking_db`;

CREATE TABLE IF NOT EXISTS `bookings` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `student_id` VARCHAR(255) NOT NULL,
    `resource_id` VARCHAR(255) NOT NULL,
    `start_time` DATETIME,
    `status` VARCHAR(255) NOT NULL
);

INSERT INTO `bookings` (`student_id`, `resource_id`, `start_time`, `status`) VALUES
('B032510001', 'A-302', '2026-06-24 09:00:00', 'ACTIVE'),
('B032510002', 'ISBN-9783161484100', '2026-06-24 10:30:00', 'ACTIVE'),
('B032510003', 'A-205', '2026-06-24 08:15:00', 'FAILED');

USE `notification_db`;

CREATE TABLE IF NOT EXISTS `notifications` (
    `id` VARCHAR(255) PRIMARY KEY,
    `type` VARCHAR(255) NOT NULL,
    `message` VARCHAR(255) NOT NULL,
    `timestamp` VARCHAR(255) NOT NULL,
    `is_read` TINYINT(1) NOT NULL DEFAULT 0,
    `matric_no` VARCHAR(255)
);

INSERT INTO `notifications` (`id`, `type`, `message`, `timestamp`, `is_read`, `matric_no`) VALUES
('ntf-001', 'BOOKING', 'Your reservation for room A-302 has been confirmed.', '2026-06-24T09:05:00', 0, 'B032510001'),
('ntf-002', 'ENROLLMENT', 'Successfully registered for course SE212.', '2026-06-24T09:15:00', 1, 'B032510002');