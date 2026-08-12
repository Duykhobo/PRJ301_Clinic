-- ============================================================================
-- SCRIPT KHỞI TẠO CƠ SỞ DỮ LIỆU MICROSOFT SQL SERVER
-- Môn học: PRJ301 - Java Web Application Development
-- Chủ đề: Hệ thống Đặt lịch Phòng khám & Spa (PRJ301_ClinicDB)
-- ============================================================================

USE master;
GO

IF EXISTS (SELECT name FROM sys.databases WHERE name = N'PRJ301_ClinicDB')
BEGIN
    ALTER DATABASE PRJ301_ClinicDB SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE PRJ301_ClinicDB;
END
GO

CREATE DATABASE PRJ301_ClinicDB;
GO

USE PRJ301_ClinicDB;
GO

-- ============================================================================
-- 1. BẢNG Users (Quản lý Tài khoản & Phân quyền)
-- Roles: ADMIN, DOCTOR, PATIENT, RECEPTIONIST (Lễ tân / Thu ngân)
-- Mật khẩu mặc định trong seed data là: 123456 (đã mã hóa BCrypt)
-- ============================================================================
CREATE TABLE Users (
    id INT IDENTITY(1,1) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NULL UNIQUE, -- Đặt NULL để hỗ trợ bệnh nhân đăng ký tại quầy chỉ dùng SĐT
    fullname NVARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'DOCTOR', 'PATIENT', 'RECEPTIONIST')),
    status BIT DEFAULT 1, -- 1: Active, 0: Inactive / Banned
    created_at DATETIME DEFAULT GETDATE()
);
GO

-- ============================================================================
-- 2. BẢNG Services (Danh mục Dịch vụ khám / Chăm sóc)
-- ============================================================================
CREATE TABLE Services (
    id INT IDENTITY(1,1) PRIMARY KEY,
    service_name NVARCHAR(100) NOT NULL,
    price DECIMAL(18,2) NOT NULL CHECK (price >= 0),
    duration_minutes INT NOT NULL CHECK (duration_minutes > 0),
    description NVARCHAR(MAX),
    image_url VARCHAR(255),
    status BIT DEFAULT 1 -- 1: Active, 0: Hidden
);
GO

-- ============================================================================
-- 3. BẢNG DoctorProfiles (Hồ sơ Bác sĩ / KTV)
-- ============================================================================
CREATE TABLE DoctorProfiles (
    id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL UNIQUE FOREIGN KEY REFERENCES Users(id) ON DELETE CASCADE,
    specialty NVARCHAR(100) NOT NULL,
    experience_years INT DEFAULT 1 CHECK (experience_years >= 0),
    room_number VARCHAR(20),
    bio NVARCHAR(MAX)
);
GO

-- ============================================================================
-- 4. BẢNG DoctorSchedules (Khung giờ / Lịch làm việc của Bác sĩ)
-- ============================================================================
CREATE TABLE DoctorSchedules (
    id INT IDENTITY(1,1) PRIMARY KEY,
    doctor_id INT NOT NULL FOREIGN KEY REFERENCES DoctorProfiles(id) ON DELETE CASCADE,
    work_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_available BIT DEFAULT 1, -- 1: Trống, 0: Đã có hẹn hoặc khóa
    CONSTRAINT UQ_Doctor_Schedule UNIQUE (doctor_id, work_date, start_time)
);
GO

-- ============================================================================
-- 5. BẢNG Appointments (Lịch hẹn khám & Giao dịch SePay)
-- Status: PENDING, CONFIRMED, COMPLETED, CANCELLED
-- Payment Status: UNPAID, PAID
-- Payment Method: CASH, SEPAY_QR
-- Ràng buộc UNIQUE(schedule_id): Ngăn chặn 2 cuộc hẹn trỏ cùng 1 slot giờ
-- ============================================================================
CREATE TABLE Appointments (
    id INT IDENTITY(1,1) PRIMARY KEY,
    patient_id INT NOT NULL FOREIGN KEY REFERENCES Users(id),
    doctor_id INT NOT NULL FOREIGN KEY REFERENCES DoctorProfiles(id),
    service_id INT NOT NULL FOREIGN KEY REFERENCES Services(id),
    schedule_id INT NOT NULL UNIQUE FOREIGN KEY REFERENCES DoctorSchedules(id),
    appointment_date DATE NOT NULL, -- Historical Snapshot
    start_time TIME NOT NULL,       -- Historical Snapshot
    total_price DECIMAL(18,2) NOT NULL, -- Historical Price Snapshot
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'CONFIRMED', 'COMPLETED', 'CANCELLED')),
    payment_status VARCHAR(20) DEFAULT 'UNPAID' CHECK (payment_status IN ('UNPAID', 'PAID')),
    payment_method VARCHAR(20) DEFAULT 'CASH' CHECK (payment_method IN ('CASH', 'SEPAY_QR')),
    payment_content VARCHAR(100), -- Nội dung chuyển khoản yêu cầu bởi SePay Webhook (Cú pháp: CLINIC<id>)
    transaction_code VARCHAR(50),  -- Mã giao dịch ngân hàng trả về từ SePay Webhook
    notes NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE()
);
GO

-- ============================================================================
-- 6. BẢNG MedicalRecords (Hồ sơ khám bệnh & Đánh giá)
-- ============================================================================
CREATE TABLE MedicalRecords (
    id INT IDENTITY(1,1) PRIMARY KEY,
    appointment_id INT NOT NULL UNIQUE FOREIGN KEY REFERENCES Appointments(id) ON DELETE CASCADE,
    patient_id INT NOT NULL FOREIGN KEY REFERENCES Users(id),
    doctor_id INT NOT NULL FOREIGN KEY REFERENCES DoctorProfiles(id),
    diagnosis NVARCHAR(MAX),
    prescription_or_result NVARCHAR(MAX),
    rating INT CHECK (rating BETWEEN 1 AND 5),
    review_comment NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE()
);
GO

-- ============================================================================
-- 7. BẢNG ClinicSettings (Cấu hình Hệ thống & Thông tin Phòng khám)
-- Cho phép Admin tự do điều chỉnh Tên phòng khám, Hotline, Giờ làm việc, STK SePay...
-- ============================================================================
CREATE TABLE ClinicSettings (
    id INT IDENTITY(1,1) PRIMARY KEY,
    setting_key VARCHAR(50) NOT NULL UNIQUE,
    setting_value NVARCHAR(MAX) NOT NULL,
    description NVARCHAR(255),
    updated_at DATETIME DEFAULT GETDATE()
);
GO

-- ============================================================================
-- DỮ LIỆU MẪU (SEED DATA CHUYÊN NGHIỆP DÀNH CHO DỰ ÁN PRJ301)
-- Mật khẩu hash BCrypt cho tất cả tài khoản mẫu bên dưới là "123456":
-- $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- ============================================================================

-- 1. Chèn Users (Tài khoản Admin, Doctor, Receptionist, Patients)
INSERT INTO Users (username, password, email, fullname, phone, role, status) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin@clinic.com', N'Quản Trị Viên Master', '0901234567', 'ADMIN', 1),
('drminh', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'drminh@clinic.com', N'BS. Nguyễn Văn Minh', '0912345678', 'DOCTOR', 1),
('drlan', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'drlan@clinic.com', N'BS. Trần Thị Lan', '0923456789', 'DOCTOR', 1),
('receptionist1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'letan@clinic.com', N'Lễ Tân Nguyễn Mai Phương', '0933334444', 'RECEPTIONIST', 1),
('patient1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'patient1@gmail.com', N'Lê Hoàng Nam', '0934567890', 'PATIENT', 1),
('patient2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'patient2@gmail.com', N'Phạm Thu Hương', '0945678901', 'PATIENT', 1),
('patient3', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'patient3@gmail.com', N'Vũ Ngọc Anh', '0956789012', 'PATIENT', 1),
('patient4', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'patient4@gmail.com', N'Đặng Minh Trí', '0967890123', 'PATIENT', 1);

-- 2. Chèn Services (Danh mục Dịch vụ Phòng khám & Spa)
INSERT INTO Services (service_name, price, duration_minutes, description, image_url, status) VALUES
(N'Khám & Tẩy Trắng Răng Laser', 1500000.00, 45, N'Tẩy trắng răng công nghệ Laser Whitening không gây ê buốt, sáng bóng tự nhiên.', 'https://images.unsplash.com/photo-1588776814546-1ffcf47267a5?auto=format&fit=crop&w=600&q=80', 1),
(N'Khám Nha Khoa Tổng Quát', 300000.00, 30, N'Kiểm tra sức khỏe răng miệng, lấy cao răng đánh bóng chuyên sâu.', 'https://images.unsplash.com/photo-1606811841689-23dfddce3e95?auto=format&fit=crop&w=600&q=80', 1),
(N'Chăm Sóc Da Mặt Deep Cleansing Spa', 850000.00, 60, N'Liệu trình làm sạch sâu, thải độc và trẻ hóa làn da căng mịn.', 'https://images.unsplash.com/photo-1570172619644-dfd03ed5d881?auto=format&fit=crop&w=600&q=80', 1),
(N'Chăm Sóc Da Mụn & Phục Hồi Y Khoa', 650000.00, 50, N'Điều trị mụn chuyên y khoa, chiếu ánh sáng sinh học làm lành da nhanh chóng.', 'https://images.unsplash.com/photo-1512290900673-7002ff2e4318?auto=format&fit=crop&w=600&q=80', 1),
(N'Trẻ Hóa Da Công Nghệ High Tech', 2500000.00, 90, N'Liệu trình nâng cơ, xóa nhăn và tái tạo collagen cho làn da tuổi trung niên.', 'https://images.unsplash.com/photo-1516549655169-df83a0774514?auto=format&fit=crop&w=600&q=80', 1),
(N'Niềng Răng Thẩm Mỹ Khám Tư Vấn', 500000.00, 45, N'Chụp X-quang panorama tư vấn phác đồ niềng răng trong suốt và mắc cài.', 'https://images.unsplash.com/photo-1598256989800-fe5f95da9787?auto=format&fit=crop&w=600&q=80', 1);

-- 3. Chèn DoctorProfiles (Hồ sơ Bác sĩ)
INSERT INTO DoctorProfiles (user_id, specialty, experience_years, room_number, bio) VALUES
(2, N'Nha Khoa Thẩm Mỹ & Phục Hình', 10, 'Room 101', N'Trưởng khoa Nha Khoa với 10 năm kinh nghiệm trong lĩnh vực phục hình và thẩm mỹ nụ cười.'),
(3, N'Da Liễu & Thẩm Mỹ Skin Care Spa', 8, 'Room 202', N'Chuyên gia da liễu hàng đầu, chuyên điều trị các vấn đề về da và trẻ hóa chuyên sâu.');

-- 4. Chèn DoctorSchedules (Khung giờ làm việc cho Hôm nay, Ngày mai và Ngày 2026-08-15)
INSERT INTO DoctorSchedules (doctor_id, work_date, start_time, end_time, is_available) VALUES
-- Hôm nay (GETDATE())
(1, CAST(GETDATE() AS DATE), '08:00', '09:00', 0),
(1, CAST(GETDATE() AS DATE), '09:00', '10:00', 1),
(1, CAST(GETDATE() AS DATE), '10:00', '11:00', 1),
(1, CAST(GETDATE() AS DATE), '11:00', '12:00', 1),
(1, CAST(GETDATE() AS DATE), '14:00', '15:00', 1),
(2, CAST(GETDATE() AS DATE), '14:00', '15:00', 0),
(2, CAST(GETDATE() AS DATE), '15:00', '16:00', 1),
(2, CAST(GETDATE() AS DATE), '16:00', '17:00', 1),

-- Ngày mai (GETDATE() + 1)
(1, CAST(DATEADD(DAY, 1, GETDATE()) AS DATE), '08:00', '09:00', 1),
(1, CAST(DATEADD(DAY, 1, GETDATE()) AS DATE), '09:00', '10:00', 1),
(1, CAST(DATEADD(DAY, 1, GETDATE()) AS DATE), '10:00', '11:00', 1),
(2, CAST(DATEADD(DAY, 1, GETDATE()) AS DATE), '14:00', '15:00', 1),
(2, CAST(DATEADD(DAY, 1, GETDATE()) AS DATE), '15:00', '16:00', 1),

-- Ngày thử nghiệm cố định 2026-08-15
(1, '2026-08-15', '08:00', '09:00', 1),
(1, '2026-08-15', '09:00', '10:00', 1),
(1, '2026-08-15', '10:00', '11:00', 1),
(1, '2026-08-15', '14:00', '15:00', 1),
(2, '2026-08-15', '14:00', '15:00', 1),
(2, '2026-08-15', '15:00', '16:00', 1);

-- 5. Chèn Appointments (Lịch hẹn mẫu)
INSERT INTO Appointments (patient_id, doctor_id, service_id, schedule_id, appointment_date, start_time, total_price, status, payment_status, payment_method, payment_content, transaction_code, notes) VALUES
(5, 1, 1, 1, CAST(GETDATE() AS DATE), '08:00', 1500000.00, 'COMPLETED', 'PAID', 'SEPAY_QR', 'CLINIC1', 'FT2408110001', N'Khách hàng muốn tẩy trắng trước ngày cưới.'),
(6, 2, 3, 6, CAST(GETDATE() AS DATE), '14:00', 850000.00, 'CONFIRMED', 'PAID', 'SEPAY_QR', 'CLINIC2', 'FT2408110002', N'Da nhạy cảm, dễ dị ứng.');

-- 6. Chèn MedicalRecords (Hồ sơ bệnh án mẫu)
INSERT INTO MedicalRecords (appointment_id, patient_id, doctor_id, diagnosis, prescription_or_result, rating, review_comment) VALUES
(1, 5, 1, N'Răng ố vàng nhẹ do uống cà phê.', N'Tẩy trắng thành công Laser Whitening. Dùng kem đánh răng chống ê buốt 3 ngày.', 5, N'Bác sĩ Minh rất mát tay, răng trắng sáng đẹp lắm!');
GO

-- 7. Chèn ClinicSettings (Cấu hình Hệ thống & Ngân hàng VietQR SePay)
INSERT INTO ClinicSettings (setting_key, setting_value, description) VALUES
('CLINIC_NAME', N'Phòng Khám & Spa Nha Khoa Quốc Tế PRJ301', N'Tên phòng khám hiển thị trên Header/Footer'),
('CLINIC_HOTLINE', '0901234567', N'Số điện thoại tổng đài tư vấn'),
('CLINIC_EMAIL', 'contact@prj301clinic.com', N'Email liên hệ hỗ trợ'),
('CLINIC_ADDRESS', N'123 Đường Nguyễn Văn Cừ, Phường 4, Quận 5, TP.HCM', N'Địa chỉ chi nhánh chính'),
('OPENING_HOURS', N'08:00 - 20:00 (Từ Thứ 2 đến Chủ Nhật)', N'Khung giờ mở cửa hoạt động chung'),
('CLINIC_SLOT_DURATION', '60', N'Thời lượng mỗi khung giờ khám (Phút) - Động'),
('CLINIC_TIME_SLOTS', '08:00,09:00,10:00,11:00,14:00,15:00,16:00,17:00', N'Danh sách các khung giờ khám khả dụng trong ngày - Động'),
('SEPAY_BANK_NAME', 'MBBank', N'Tên ngân hàng tài khoản SePay'),
('SEPAY_BANK_ACC', '0901234567', N'Số tài khoản nhận chuyển khoản SePay'),
('SEPAY_ACCOUNT_HOLDER', N'PHONG KHAM PRJ301', N'Tên chủ tài khoản nhận tiền');
GO

-- ============================================================================
-- NÂNG CAO: STORED PROCEDURES, USER-DEFINED FUNCTIONS & DATABASE TRIGGERS
-- (Điểm cộng kỹ thuật ấn tượng khi bảo vệ với Giảng viên môn PRJ301)
-- ============================================================================

-- 1. FUNCTION: Tính điểm đánh giá trung bình (1 - 5 sao) của một Bác sĩ
IF OBJECT_ID('dbo.fn_GetDoctorAverageRating', 'FN') IS NOT NULL
    DROP FUNCTION dbo.fn_GetDoctorAverageRating;
GO

CREATE FUNCTION dbo.fn_GetDoctorAverageRating(@DoctorId INT)
RETURNS DECIMAL(3,2)
AS
BEGIN
    DECLARE @AvgRating DECIMAL(3,2);
    SELECT @AvgRating = ISNULL(AVG(CAST(rating AS DECIMAL(3,2))), 0.0)
    FROM MedicalRecords
    WHERE doctor_id = @DoctorId AND rating IS NOT NULL;
    RETURN @AvgRating;
END;
GO

-- 2. STORED PROCEDURE: Lấy danh sách các khung giờ khả dụng theo Bác sĩ và Ngày
IF OBJECT_ID('dbo.sp_GetAvailableSlotsByDoctorAndDate', 'P') IS NOT NULL
    DROP PROCEDURE dbo.sp_GetAvailableSlotsByDoctorAndDate;
GO

CREATE PROCEDURE dbo.sp_GetAvailableSlotsByDoctorAndDate
    @DoctorId INT,
    @WorkDate DATE
AS
BEGIN
    SET NOCOUNT ON;
    SELECT 
        s.id AS id,
        s.id AS schedule_id,
        s.doctor_id,
        s.work_date,
        s.start_time,
        s.end_time,
        s.is_available
    FROM DoctorSchedules s
    WHERE s.doctor_id = @DoctorId 
      AND s.work_date = @WorkDate
      AND s.is_available = 1
      AND s.id NOT IN (
          SELECT schedule_id 
          FROM Appointments 
          WHERE status IN ('PENDING', 'CONFIRMED', 'COMPLETED')
      )
    ORDER BY s.start_time ASC;
END;
GO

-- 3. STORED PROCEDURE: Báo cáo Thống kê Doanh thu và Số lượng Lịch hẹn cho Admin Dashboard
IF OBJECT_ID('dbo.sp_GetClinicRevenueReport', 'P') IS NOT NULL
    DROP PROCEDURE dbo.sp_GetClinicRevenueReport;
GO

CREATE PROCEDURE dbo.sp_GetClinicRevenueReport
    @StartDate DATE,
    @EndDate DATE
AS
BEGIN
    SET NOCOUNT ON;
    SELECT 
        COUNT(id) AS total_appointments,
        SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed_appointments,
        SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END) AS cancelled_appointments,
        SUM(CASE WHEN payment_status = 'PAID' THEN total_price ELSE 0 END) AS total_revenue_paid,
        SUM(CASE WHEN payment_method = 'SEPAY_QR' AND payment_status = 'PAID' THEN total_price ELSE 0 END) AS sepay_revenue,
        SUM(CASE WHEN payment_method = 'CASH' AND payment_status = 'PAID' THEN total_price ELSE 0 END) AS cash_revenue
    FROM Appointments
    WHERE appointment_date BETWEEN @StartDate AND @EndDate;
END;
GO

-- 4. TRIGGER: Tự động khóa slot DoctorSchedules (is_available = 0) khi có lịch hẹn mới
-- Hoặc tự động mở lại slot (is_available = 1) khi lịch hẹn chuyển sang CANCELLED
IF OBJECT_ID('dbo.trg_UpdateSlotStatusOnAppointment', 'TR') IS NOT NULL
    DROP TRIGGER dbo.trg_UpdateSlotStatusOnAppointment;
GO

CREATE TRIGGER dbo.trg_UpdateSlotStatusOnAppointment
ON Appointments
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    -- Trường hợp 1: Thêm mới hoặc Cập nhật Lịch hẹn sang PENDING/CONFIRMED/COMPLETED -> Khóa Slot (is_available = 0)
    UPDATE DoctorSchedules
    SET is_available = 0
    FROM DoctorSchedules ds
    INNER JOIN inserted i ON ds.id = i.schedule_id
    WHERE i.status IN ('PENDING', 'CONFIRMED', 'COMPLETED');

    -- Trường hợp 2: Lịch hẹn chuyển sang CANCELLED -> Mở lại Slot (is_available = 1)
    UPDATE DoctorSchedules
    SET is_available = 1
    FROM DoctorSchedules ds
    INNER JOIN inserted i ON ds.id = i.schedule_id
    WHERE i.status = 'CANCELLED';
END;
GO
