1. สร้าง Database
CREATE DATABASE IF NOT EXISTS shelter_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shelter_db;

2. สร้างตาราง Shelters
CREATE TABLE IF NOT EXISTS shelters (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    risk_level INT NOT NULL DEFAULT 1 COMMENT '1=Low, 2=Medium, 3=High',
    capacity INT NOT NULL DEFAULT 50,
    current_occupancy INT DEFAULT 0 -- เก็บจำนวนคนปัจจุบัน (หรือจะใช้ Count จากตาราง citizens ก็ได้)
);

3. สร้างตาราง Citizens
CREATE TABLE IF NOT EXISTS citizens (
    id VARCHAR(10) PRIMARY KEY, -- ใช้ VARCHAR เพราะ ID ใน Java เป็น String (เช่น "C01")
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    category VARCHAR(20) NOT NULL COMMENT 'Child, Adult, Elder',
    has_health_risk BOOLEAN DEFAULT FALSE,
    assigned_shelter_id INT DEFAULT NULL, -- ถ้าเป็น NULL แปลว่ายังไม่ได้ที่พัก (Waiting List)
    
    -- สร้างความสัมพันธ์ (Foreign Key) ไปหาตาราง Shelters
    CONSTRAINT fk_shelter
        FOREIGN KEY (assigned_shelter_id) 
        REFERENCES shelters(id)
        ON DELETE SET NULL -- ถ้าศูนย์ปิด ให้คนกลับมาเป็นสถานะรอดำเนินการ (NULL)
);
เพิ่มข้อมูลศูนย์พักพิง
INSERT INTO shelters (name, risk_level, capacity, current_occupancy) VALUES 
('City Hall (Safe Zone)', 1, 5, 0),
('School Gym (Moderate)', 2, 10, 0),
('Mountain Base (High Risk)', 3, 8, 0),
('Hospital Basement', 1, 3, 0),
('River Bank Camp', 3, 4, 0);

เพิ่มข้อมูลประชากร (บางคนได้ที่พักแล้ว, บางคนยังรออยู่)
INSERT INTO citizens (id, name, age, category, has_health_risk, assigned_shelter_id) VALUES 
('C01', 'John Doe', 45, 'Adult', FALSE, NULL),
('C02', 'Jane Smith', 70, 'Elder', TRUE, NULL),
('C03', 'Baby Shark', 5, 'Child', FALSE, 1),
('C04', 'Grandma Rose', 80, 'Elder', TRUE, 4),
('C05', 'Mike Tyson', 30, 'Adult', FALSE, NULL),
('C06', 'Sarah Connor', 35, 'Adult', TRUE, NULL);

ใส่ข้อมูล (Csv) ไว้ในโฟลเดอร์ Data
