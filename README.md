<div align="center">

# 🏥 PRJ301 Clinic & Spa
### Hệ Thống Đặt Lịch Khám Bệnh & Thanh Toán Tự Động Toàn Diện

[![Java](https://img.shields.io/badge/Java-EE%208-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![SQL Server](https://img.shields.io/badge/SQL%20Server-2019-CC2927?style=for-the-badge&logo=microsoft-sql-server&logoColor=white)](https://www.microsoft.com/sql-server)
[![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white)](https://getbootstrap.com)
[![HikariCP](https://img.shields.io/badge/HikariCP-3.4.5-4479A1?style=for-the-badge)](https://github.com/brettwooldridge/HikariCP)
[![SePay](https://img.shields.io/badge/SePay-VietQR%20Live-00B14F?style=for-the-badge)](https://sepay.vn)
[![License](https://img.shields.io/badge/License-Academic-blue?style=for-the-badge)](./LICENSE)

<br/>

> **Môn học**: PRJ301 — Java Web Application Development  
> **Trường**: FPT University | **Kỳ**: Summer 2026  
> **Hình thức**: Bài tập cá nhân (Individual Assignment)

</div>

---

## 📖 Mục Lục

- [Giới Thiệu](#-giới-thiệu)
- [Điểm Sáng Kỹ Thuật](#-điểm-sáng-kỹ-thuật)
- [Tính Năng](#-tính-năng)
- [Tech Stack](#%EF%B8%8F-tech-stack)
- [Kiến Trúc Hệ Thống](#-kiến-trúc-hệ-thống)
- [Cấu Trúc Dự Án](#-cấu-trúc-dự-án)
- [Tiến Độ Phát Triển](#-tiến-độ-phát-triển)
- [Hướng Dẫn Cài Đặt](#-hướng-dẫn-cài-đặt)
- [Tài Khoản Mặc Định](#-tài-khoản-mặc-định)
- [URL Endpoints](#-url-endpoints)
- [Cơ Sở Dữ Liệu](#-cơ-sở-dữ-liệu)

---

## 🏥 Giới Thiệu

**PRJ301 Clinic & Spa** là một hệ thống Web Application toàn diện được xây dựng theo kiến trúc **Enterprise 3-Tier (MVC)** với Java EE 8, phục vụ quản lý và vận hành phòng khám nha khoa & spa làm đẹp.

Hệ thống cung cấp trải nghiệm đặt lịch khám trực tuyến liền mạch, tích hợp **thanh toán thật qua VietQR SePay** (Sacombank), quản lý hồ sơ bệnh án, và không gian làm việc chuyên biệt cho từng vai trò: Bác Sĩ, Lễ Tân, Bệnh Nhân và Quản Trị Viên.

---

## ⭐ Điểm Sáng Kỹ Thuật

### 🔒 1. Atomic Booking — Chống Race Condition Đa Tầng
Giải quyết bài toán đặt lịch đồng thời (Concurrent Booking) khi nhiều bệnh nhân cùng nhấn đặt 1 khung giờ tại cùng 1 millisecond:

```
Tầng 1 — Database:    CONSTRAINT UQ_Appointment_Schedule UNIQUE(schedule_id)
Tầng 2 — JDBC:        WITH (UPDLOCK, HOLDLOCK) + TRANSACTION_READ_COMMITTED
Tầng 3 — Application: SlotAlreadyBookedException + Toast Error Notification
```

### 💳 2. Thanh Toán VietQR SePay — Tích Hợp Thật 100%
- Sinh mã QR động chuẩn `vietqr.app` với `bank`, `acc`, `amount`, `des` thật.
- Webhook Servlet nhận callback từ SePay và tự động cập nhật `payment_status = PAID`.
- Xác thực bảo mật **HMAC-SHA256** qua header `X-SePay-Signature`.
- Secret Key nạp từ biến môi trường `System.getenv("SEPAY_SECRET_KEY")` — **không bao giờ hardcode**.

### 🗄️ 3. SQL Server Advanced Objects
| Object | Tên | Chức Năng |
| :--- | :--- | :--- |
| **Trigger** | `trg_UpdateSlotStatusOnAppointment` | Tự động khóa/mở slot sau mỗi INSERT/UPDATE lịch hẹn |
| **Stored Proc** | `sp_GetClinicRevenueReport` | Báo cáo doanh thu SePay/Tiền mặt theo khoảng thời gian |
| **Stored Proc** | `sp_GetAvailableSlotsByDoctorAndDate` | Lọc slot trống nhanh theo Bác sĩ & Ngày |
| **Function** | `fn_GetDoctorAverageRating` | Tính điểm đánh giá 1–5 sao của Bác sĩ |

### 🛡️ 4. Bảo Mật & Kiến Trúc Sạch
- **BCrypt Password Hashing** — Mật khẩu không bao giờ lưu plaintext.
- **3-Layer Security Filters**: `EncodingFilter` → `AuthenticationFilter` → `RoleFilter`.
- **Environment Variable** cho tất cả thông tin nhạy cảm (DB Password, API Keys).
- **BaseDAO Pattern** — Loại bỏ 90% boilerplate JDBC với Generic RowMapper.

---

## 🎯 Tính Năng

<table>
<tr>
<td width="50%">

### 👤 Bệnh Nhân (PATIENT)
- ✅ Đăng ký / Đăng nhập tài khoản
- ✅ Xem danh sách Dịch vụ & Bác sĩ
- ✅ Đặt lịch khám với Flatpickr Datepicker
- ✅ Thanh toán VietQR SePay (Live Bank)
- ✅ Theo dõi trạng thái thanh toán Real-time
- ✅ Xem lịch sử khám & đơn thuốc

</td>
<td width="50%">

### 🩺 Bác Sĩ (DOCTOR)
- ✅ Xem danh sách ca khám trong ngày
- ✅ Thực hiện khám & nhập chẩn đoán bệnh
- ✅ Kê đơn thuốc & chỉ định điều trị
- ✅ Lưu hồ sơ bệnh án vào MedicalRecords
- ✅ Theo dõi thống kê ca hoàn tất / chờ khám

</td>
</tr>
<tr>
<td width="50%">

### 🏨 Lễ Tân (RECEPTIONIST)
- ✅ Tổng quan sảnh tiếp đón theo ngày
- ✅ Xác nhận Check-in bệnh nhân tại sảnh
- ✅ Thu tiền mặt & cập nhật trạng thái
- ✅ Hủy cuộc hẹn khi cần thiết
- ✅ Thống kê: Tổng khách, Đã thu, Chưa thu

</td>
<td width="50%">

### ⚙️ Quản Trị Viên (ADMIN)
- 🔲 Quản lý tài khoản người dùng
- 🔲 Quản lý dịch vụ & bác sĩ
- 🔲 Báo cáo doanh thu (Stored Proc)
- 🔲 Cấu hình hệ thống động (ClinicSettings)

</td>
</tr>
</table>

---

## 🛠️ Tech Stack

```
┌─────────────────────────────────────────────────────────────────────┐
│                         PRESENTATION LAYER                          │
│   JSP 2.3 · JSTL 1.2 · Bootstrap 5.3 · Font Awesome 6 · Flatpickr  │
├─────────────────────────────────────────────────────────────────────┤
│                           BUSINESS LAYER                            │
│         Java EE 8 · Servlet 3.1 · Service Classes · Filters         │
├─────────────────────────────────────────────────────────────────────┤
│                          DATA ACCESS LAYER                          │
│               Pure JDBC · BaseDAO<T> · HikariCP 3.4.5               │
├─────────────────────────────────────────────────────────────────────┤
│                           DATABASE LAYER                            │
│   Microsoft SQL Server · Triggers · Stored Procedures · Functions   │
├─────────────────────────────────────────────────────────────────────┤
│                          EXTERNAL SERVICES                          │
│         SePay VietQR API · HMAC-SHA256 Webhook · Sacombank          │
└─────────────────────────────────────────────────────────────────────┘
```

| Hạng Mục | Công Nghệ | Phiên Bản |
| :--- | :--- | :--- |
| Back-end | Java EE (Servlet / JSP) | 8 / 3.1 / 2.3 |
| Build Tool | NetBeans Ant | — |
| Database | Microsoft SQL Server | 2019 |
| JDBC Driver | mssql-jdbc | 12.4.2.jre8 |
| Connection Pool | HikariCP | 3.4.5 |
| Password Hashing | jBCrypt | 0.4 |
| JSON Parser | Jackson Databind | 2.15.2 |
| Front-end | Bootstrap + Font Awesome | 5.3 / 6.7 |
| Date Picker | Flatpickr | 4.6.x |
| Payment Gateway | SePay VietQR | Live API |
| Testing | JUnit 5 + Postman + JMeter | — |

---

## 🏛️ Kiến Trúc Hệ Thống

```
Browser Request
      │
      ▼
┌─────────────┐
│   Filters   │  EncodingFilter → AuthenticationFilter → RoleFilter
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  Servlet    │  BookingServlet / DoctorServlet / ReceptionistServlet
│  Controller │  SepayWebhookServlet / HistoryServlet / ...
└──────┬──────┘
       │
       ▼
┌─────────────┐
│   Service   │  BookingService / (Business Logic & Validation)
│   Layer     │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  BaseDAO<T> │  AppointmentDAO / UserDAO / MedicalRecordDAO / ...
│  (Pure JDBC)│  HikariCP Connection Pool
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ SQL Server  │  Triggers / Stored Procedures / Functions
└─────────────┘

                    SePay VietQR Webhook
                           │
                    POST /sepay-webhook
                           │
                    HMAC-SHA256 Verify
                           │
                    UPDATE payment_status
```

---

## 📂 Cấu Trúc Dự Án

```
PRJ301_3W_ASSIGNMENT/
├── 📄 README.md
├── 📄 database.sql                      # Schema + Seed Data + Triggers/Stored Procs
├── 📄 .gitignore
└── 📁 PRJ301_Clinic/                    # NetBeans Ant Project Root
    ├── 📄 build.xml
    ├── 📁 src/java/
    │   ├── 📁 config/
    │   │   └── DBContext.java           # HikariCP Connection Pool
    │   ├── 📁 constant/
    │   │   ├── RoleConstant.java
    │   │   ├── RouterConstant.java
    │   │   └── SystemConstant.java
    │   ├── 📁 model/
    │   │   ├── User.java
    │   │   ├── Appointment.java
    │   │   ├── MedicalRecord.java
    │   │   ├── Service.java
    │   │   ├── DoctorProfile.java
    │   │   └── DoctorSchedule.java
    │   ├── 📁 dao/
    │   │   ├── BaseDAO.java             # Generic RowMapper + Helper Methods
    │   │   ├── UserDAO.java
    │   │   ├── AppointmentDAO.java      # Atomic Booking + Pagination
    │   │   ├── MedicalRecordDAO.java
    │   │   ├── ServiceDAO.java
    │   │   └── DoctorScheduleDAO.java
    │   ├── 📁 service/
    │   │   └── BookingService.java
    │   ├── 📁 controller/
    │   │   ├── MainController.java
    │   │   ├── LoginServlet.java
    │   │   ├── RegisterServlet.java
    │   │   ├── BookingServlet.java
    │   │   ├── HistoryServlet.java      # + Pagination
    │   │   ├── DoctorServlet.java       # Doctor Workspace
    │   │   ├── ReceptionistServlet.java # Receptionist Workspace
    │   │   └── SepayWebhookServlet.java # HMAC-SHA256 Verify
    │   ├── 📁 filter/
    │   │   ├── EncodingFilter.java
    │   │   ├── AuthenticationFilter.java
    │   │   └── RoleFilter.java
    │   ├── 📁 exception/
    │   │   └── SlotAlreadyBookedException.java
    │   └── 📁 util/
    │       └── BCryptUtil.java
    └── 📁 web/
        ├── 📁 assets/
        │   ├── css/style.css            # Glassmorphism + Custom Variables
        │   └── images/                  # Service & Auth images
        └── 📁 WEB-INF/
            ├── 📁 lib/                  # 14 JAR dependencies
            ├── 📄 web.xml
            └── 📁 views/
                ├── 📁 auth/             # login.jsp · register.jsp
                ├── 📁 patient/          # booking.jsp · payment.jsp · history.jsp
                ├── 📁 doctor/           # dashboard.jsp
                ├── 📁 receptionist/     # dashboard.jsp
                ├── 📁 public/           # home.jsp
                ├── 📁 components/       # navbar · footer · head · alerts
                └── 📁 error/            # 403 · 404 · 500
```

---

## 📊 Tiến Độ Phát Triển

> **Cập nhật lần cuối: 12/08/2026**

```
Module 1 — Core Foundation         ████████████████████ 100% ✅
Module 2 — Booking System          ████████████████████ 100% ✅
Module 3 — SePay Payment           ████████████████████ 100% ✅
Module 4 — Doctor & Receptionist   ████████████████████ 100% ✅
Module 5 — History & Pagination    ████████████████████ 100% ✅
Module 6 — Admin Dashboard         ░░░░░░░░░░░░░░░░░░░░   0% 🔲
Module 7 — Testing Suite           ░░░░░░░░░░░░░░░░░░░░   0% 🔲
```

| # | Module | Tính Năng Chính | Trạng Thái |
| :---: | :--- | :--- | :---: |
| 1 | **Core Foundation** | Enterprise 3-Tier MVC, BCrypt Auth, 3 Filters, Role Guard | ✅ Done |
| 1 | **UI/UX System** | Dual-Panel Glassmorphism, Toast Validation, Flatpickr | ✅ Done |
| 2 | **Atomic Booking** | Race Condition Lock, Slot UI, Doctor Schedule | ✅ Done |
| 3 | **SePay VietQR** | Live Bank Sacombank, Webhook HMAC-SHA256, Real-time Poll | ✅ Done |
| 4 | **Doctor Workspace** | Ca khám theo ngày, Chẩn đoán, Kê đơn thuốc | ✅ Done |
| 4 | **Receptionist Workspace** | Check-in, Thu tiền mặt, Hủy ca | ✅ Done |
| 4 | **Medical Records** | MedicalRecord Model + DAO + Hồ sơ bệnh án | ✅ Done |
| 5 | **History Page** | Lịch sử Glassmorphism, Xem Đơn Thuốc, Thanh Toán QR | ✅ Done |
| 5 | **Pagination** | SQL Server OFFSET/FETCH NEXT, Bootstrap Paginator | ✅ Done |
| 6 | **Admin Dashboard** | User/Service Management, Revenue Report | 🔲 Todo |
| 7 | **Testing** | JUnit 5, Postman, JMeter Concurrency, Lighthouse | 🔲 Todo |

---

## 🚀 Hướng Dẫn Cài Đặt

### Yêu Cầu Hệ Thống

| Thành Phần | Phiên Bản Tối Thiểu |
| :--- | :--- |
| JDK | 8+ |
| NetBeans IDE | 12+ |
| Apache Tomcat | 8.5 / 9.0 |
| SQL Server | 2016+ (hoặc SQL Server Express) |
| SSMS | Bất kỳ |

### Bước 1 — Khởi Tạo CSDL

```sql
-- Mở SSMS → New Query → Mở file database.sql → Execute (F5)
-- Script tự động tạo:
--   ✓ Database PRJ301_ClinicDB
--   ✓ 7 Tables (Users, Services, Appointments, MedicalRecords, ...)
--   ✓ 1 Trigger + 2 Stored Procedures + 1 Function
--   ✓ Seed Data (Tài khoản mặc định + Dịch vụ + Lịch bác sĩ)
```

### Bước 2 — Cấu Hình Kết Nối CSDL

Mở `PRJ301_Clinic/src/java/config/DBContext.java` và cập nhật:

```java
config.setJdbcUrl("jdbc:sqlserver://localhost:1433;databaseName=PRJ301_ClinicDB;...");
config.setUsername("sa");
config.setPassword("YOUR_SQL_SERVER_PASSWORD"); // ← Đổi thành mật khẩu của bạn
```

### Bước 3 — Cấu Hình SePay (Tùy Chọn)

```bash
# Windows — Đặt biến môi trường trước khi khởi động Tomcat
set SEPAY_SECRET_KEY=spsk_your_secret_key_here

# Nếu test local, dùng nút "⚡ Mô Phỏng Webhook SePay" trên giao diện Payment
```

### Bước 4 — Chạy Dự Án

```
1. Mở NetBeans IDE
2. File → Open Project → Chọn thư mục PRJ301_Clinic
3. Chuột phải Project → Clean and Build
4. Chuột phải Project → Run
5. Truy cập: http://localhost:8080/PRJ301_Clinic/
```

---

## 🌐 Tài Khoản Mặc Định

| Username | Password | Vai Trò | Họ & Tên |
| :--- | :---: | :--- | :--- |
| `admin` | `123456` | 🔑 **ADMIN** | Nguyễn Văn Admin |
| `drminh` | `123456` | 🩺 **DOCTOR** | BS. Nguyễn Văn Minh — Nha Khoa |
| `drlan` | `123456` | 🩺 **DOCTOR** | BS. Trần Thị Lan — Da Liễu & Spa |
| `patient1` | `123456` | 👤 **PATIENT** | Lê Hoàng Nam |
| `patient2` | `123456` | 👤 **PATIENT** | Phạm Thị Hoa |
| `receptionist1` | `123456` | 🏨 **RECEPTIONIST** | Phạm Thị Mai |

---

## 🗺️ URL Endpoints

| URL | Phương Thức | Mô Tả | Quyền |
| :--- | :---: | :--- | :--- |
| `/MainController?action=home` | GET | Trang Chủ | Tất cả |
| `/login` | GET/POST | Đăng Nhập | Khách |
| `/register` | GET/POST | Đăng Ký | Khách |
| `/logout` | GET | Đăng Xuất | Đã đăng nhập |
| `/booking` | GET/POST | Đặt Lịch Khám | PATIENT |
| `/booking?action=payment&id=X` | GET | Trang Thanh Toán VietQR | PATIENT |
| `/booking?action=check-payment-status&id=X` | GET | Polling Trạng Thái (AJAX) | PATIENT |
| `/history` | GET | Lịch Sử Khám & Đơn Thuốc | PATIENT |
| `/doctor/dashboard` | GET/POST | Workspace Bác Sĩ | DOCTOR |
| `/receptionist/dashboard` | GET/POST | Workspace Lễ Tân | RECEPTIONIST |
| `/admin/dashboard` | GET | Dashboard Quản Trị | ADMIN |
| `/sepay-webhook` | POST | Webhook SePay Callback | SePay Server |

---

## 🗄️ Cơ Sở Dữ Liệu

### Sơ Đồ ERD (Tóm Tắt)

```
Users ──────────────┐
  │                  │
  │ (patient_id)     │ (doctor via DoctorProfiles)
  ▼                  ▼
Appointments ←────── DoctorProfiles ←──── Users (role=DOCTOR)
  │    │                   │
  │    └─── DoctorSchedules│
  │
  ├─── Services (service_id)
  │
  └─── MedicalRecords
            │
            ├── diagnosis
            └── prescription_or_result

ClinicSettings (key-value config store)
```

### Danh Sách Bảng

| Bảng | Mô Tả | Dòng Seed |
| :--- | :--- | :---: |
| `Users` | Tài khoản hệ thống (4 roles) | 6 |
| `Services` | Danh mục dịch vụ khám & spa | 9 |
| `DoctorProfiles` | Hồ sơ chuyên môn bác sĩ | 2 |
| `DoctorSchedules` | Khung giờ làm việc bác sĩ | 16 |
| `Appointments` | Lịch hẹn khám bệnh | 2 |
| `MedicalRecords` | Hồ sơ bệnh án & đơn thuốc | 1 |
| `ClinicSettings` | Cấu hình động hệ thống | 10 |

---

<div align="center">

**📝 Thực hiện bởi sinh viên môn PRJ301 — FPT University | Summer 2026**

*Tuân thủ nghiêm ngặt 100% Hard Rules môn học và chuẩn mực lập trình Enterprise Java*

</div>
