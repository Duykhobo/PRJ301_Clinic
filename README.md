# 🏥 PRJ301 - HỆ THỐNG ĐẶT LỊCH PHÒNG KHÁM & SPA NHA KHOA QUỐC TẾ (PRJ301_ClinicDB)

> **Môn học**: PRJ301 - Java Web Application Development  
> **Hình thức**: Bài tập cá nhân (Individual Assignment)  
> **Công nghệ**: NetBeans Java Web (Ant System), Pure JDBC, Microsoft SQL Server, HikariCP, BCrypt, SePay VietQR  

---

## 📌 GIỚI THIỆU TỔNG QUAN

**PRJ301_ClinicDB** là hệ thống quản lý và đặt lịch hẹn khám bệnh / spa làm đẹp trực tuyến hiện đại. Hệ thống cung cấp giải pháp đặt lịch hẹn theo các khung giờ 60 phút khả dụng, hỗ trợ thanh toán tự động qua **Cổng thanh toán SePay VietQR** (tự động khóa số tiền và nội dung chuyển khoản `CLN<id>`), đối soát Webhook tự động, quản lý bệnh án bảo mật và cung cấp công cụ quản trị toàn diện cho phòng khám.

---

## ✅ TRẠNG THÁI TIẾN ĐỘ PHÁT TRIỂN (PROGRESS TRACKER)

> Cập nhật lần cuối: **12/08/2026**

| Module | Tính Năng | Trạng Thái |
| :--- | :--- | :---: |
| **Module 1** | Authentication (Đăng nhập / Đăng ký BCrypt, 3 Filters) | ✅ **HOÀN THÀNH** |
| **Module 1** | Dual-Panel Glassmorphism UI Login & Register | ✅ **HOÀN THÀNH** |
| **Module 2** | Đặt Lịch Khám (Atomic Booking + Race Condition Lock) | ✅ **HOÀN THÀNH** |
| **Module 2** | Flatpickr Glassmorphism Datepicker + Locked Slot UI | ✅ **HOÀN THÀNH** |
| **Module 2** | Custom Toast Notification Validation (novalidate) | ✅ **HOÀN THÀNH** |
| **Module 3** | Thanh Toán VietQR SePay - Sacombank `070148520060` | ✅ **HOÀN THÀNH** |
| **Module 3** | Webhook Servlet `/sepay-webhook` (HMAC-SHA256, Env Key) | ✅ **HOÀN THÀNH** |
| **Module 3** | Real-time Polling Trạng thái Thanh toán (3s/lần) | ✅ **HOÀN THÀNH** |
| **Module 3** | Nút Mô Phỏng Webhook SePay (Demo/Test Local) | ✅ **HOÀN THÀNH** |
| **Module 4** | Doctor Workspace `/doctor/dashboard` (Kê Đơn, Chẩn Đoán) | ✅ **HOÀN THÀNH** |
| **Module 4** | Receptionist Workspace `/receptionist/dashboard` | ✅ **HOÀN THÀNH** |
| **Module 4** | MedicalRecord Model + DAO + Lưu Hồ Sơ Bệnh Án | ✅ **HOÀN THÀNH** |
| **Module 5** | Lịch Sử Khám Glassmorphism (Xem Đơn Thuốc, Thanh Toán QR) | ✅ **HOÀN THÀNH** |
| **Module 5** | Enterprise Pagination (SQL Server OFFSET...FETCH NEXT) | ✅ **HOÀN THÀNH** |
| **Module 6** | Admin Dashboard | 🔲 **CHƯA THỰC HIỆN** |
| **Module 7** | JUnit 5 / JMeter Concurrency Test / Lighthouse | 🔲 **CHƯA THỰC HIỆN** |

---

## 🚀 ĐIỂM SÁNG KỸ THUẬT & TÍNH NĂNG VƯỢT TRỘI (KEY SELLING POINTS)

1. **Chống Race Condition đa tầng (Concurrency Control)**:
   - **Tầng CSDL**: Ràng buộc `CONSTRAINT UQ_Appointment_Schedule UNIQUE (schedule_id)` ngăn chặn trùng lịch từ gốc.
   - **Tầng JDBC Transaction**: Sử dụng `WITH (UPDLOCK)` và `Connection.TRANSACTION_READ_COMMITTED` đảm bảo thao tác Atomic khi 2 khách hàng bấm đặt trùng 1 slot tại cùng 1 milisecond.
2. **SQL Server Advanced Objects (Trigger, Stored Procs, Function)**:
   - Trigger `trg_UpdateSlotStatusOnAppointment`: Tự động khóa (`is_available = 0`) hoặc mở lại slot (`is_available = 1`) trong `DoctorSchedules` khi có lịch hẹn mới/hủy.
   - Stored Procedure `sp_GetClinicRevenueReport`: Thống kê doanh thu SePay/Tiền mặt cho Admin.
   - Stored Procedure `sp_GetAvailableSlotsByDoctorAndDate`: Lọc slot trống nhanh chóng.
   - Function `fn_GetDoctorAverageRating`: Tính điểm đánh giá 1-5 sao của Bác sĩ.
3. **Thanh toán Tự động SePay VietQR (LIVE BANK)**:
   - Tài khoản thật: **Sacombank `070148520060` - NGUYEN THANH DUY**.
   - Tự động sinh mã `payment_content = "CLN" + appointmentId`.
   - Tạo VietQR nhúng sẵn số tiền & cú pháp `CLN<id>`, tự động khóa thông tin trên App Ngân hàng.
   - Webhook `/sepay-webhook` tiếp nhận JSON từ SePay để tự động đổi `payment_status = 'PAID'`.
   - Xác thực bảo mật HMAC-SHA256 (`X-SePay-Signature`). Secret Key nạp từ biến môi trường.
4. **Workspace Bác Sĩ & Lễ Tân Glassmorphism**:
   - Bác Sĩ: Xem ca khám theo ngày, chẩn đoán & kê đơn thuốc qua Modal, tự động chuyển trạng thái `COMPLETED`.
   - Lễ Tân: Theo dõi sảnh tiếp đón, thu tiền mặt, check-in và hủy ca.
5. **Bảo mật Dữ liệu Y tế (Medical Privacy Scoping)**:
   - Phân tách DAO: Bác sĩ & Bệnh nhân được đọc hồ sơ ca khám của mình; Đơn thuốc ẩn khỏi người ngoài.
6. **Giao Diện Lịch Sử Khám & Phân Trang**:
   - Xem lịch sử đặt lịch với đầy đủ thông tin bác sĩ, dịch vụ, trạng thái.
   - Modal Xem Đơn Thuốc: Bệnh nhân xem chẩn đoán & đơn thuốc sau khi khám xong.
   - Enterprise Pagination MS SQL Server `OFFSET ... FETCH NEXT 5 ROWS ONLY`.

---

## 🛠️ CÔNG NGHỆ & THƯ VIỆN SỬ DỤNG (TECH STACK)

- **Back-end Core**: Java EE 8 (Servlet 3.1, JSP 2.3, JSTL 1.2).
- **IDE & Build Tool**: NetBeans IDE (Ant Build System).
- **CSDL**: Microsoft SQL Server (Driver `mssql-jdbc-12.4.2.jre8.jar`).
- **Connection Pool**: HikariCP `3.4.5` (Dành riêng cho Java 8).
- **Mã hóa Mật khẩu**: jBCrypt `0.4`.
- **JSON Parser**: Jackson Databind `2.15.2`.
- **Testing Suite**: JUnit 5, Postman, Apache JMeter.
- **Front-end**: HTML5, CSS3, JavaScript ES6+, Bootstrap 5.3, Font Awesome 6, Flatpickr.
- **Payment**: SePay VietQR API (`vietqr.app`), HMAC-SHA256 Webhook Verification.

---

## 📐 MA TRẬN PHÂN LOẠI TÍNH NĂNG THEO ĐỘ ƯU TIÊN

| Mức độ Ưu tiên | Hạng mục Tính năng & Kỹ thuật | Trạng thái |
| :--- | :--- | :--- |
| 🔴 **MỨC 1: BẮT BUỘC** *(Core Hard Rules)* | • **Enterprise 3-Tier Architecture** (`Controller` → `Service` → `DAO`).<br/>• Pure JDBC (`PreparedStatement`) & HikariCP SQL Server.<br/>• BCrypt Hashing & 3 Filters (`Encoding`, `Auth`, `Role`).<br/>• Phân quyền 4 Roles (`ADMIN`, `DOCTOR`, `PATIENT`, `RECEPTIONIST`).<br/>• 100% CRUD trên 7 Bảng CSDL.<br/>• Chống Race Condition: `WITH (UPDLOCK)` & `UNIQUE(schedule_id)`. | ✅ **HOÀN THÀNH 100%** |
| 🟡 **MỨC 2: QUAN TRỌNG** *(Real-World MVP)* | • Thanh toán tự động SePay VietQR Động (`CLN<id>`) - **LIVE BANK Sacombank**.<br/>• Webhook SePay đối soát tự động + HMAC-SHA256 bảo mật.<br/>• Doctor Workspace: Chẩn đoán, kê đơn thuốc, lưu MedicalRecords.<br/>• Receptionist Workspace: Tiếp nhận sảnh, thu tiền mặt, hủy ca.<br/>• Lịch sử Khám Glassmorphism + Xem Đơn Thuốc + Pagination. | ✅ **HOÀN THÀNH 100%** |
| 🟢 **MỨC 3: MỞ RỘNG** *(Advanced 10/10)* | • 4 thành phần SQL Server nâng cao (Trigger `trg_UpdateSlotStatusOnAppointment`, Stored Procs `sp_GetClinicRevenueReport`, `sp_GetAvailableSlotsByDoctorAndDate`, Function `fn_GetDoctorAverageRating`).<br/>• Bộ kiểm thử JUnit 5, Postman, Apache JMeter Concurrency Test, JaCoCo Coverage Report, Cypress E2E UI Test & Lighthouse. | ⚙️ **ĐANG THỰC HIỆN** |

---

## 📂 CẤU TRÚC THƯ MỤC DỰ ÁN (PROJECT STRUCTURE)

```text
PRJ301_Assignment/
├── database.sql                    # Script CSDL SQL Server (Schema 7 Bảng + Mock Data + Triggers/Procedures)
├── README.md                       # Tài liệu hướng dẫn dự án & Theo dõi tiến độ
├── .gitignore                      # File cấu hình bỏ qua file build tạm của NetBeans
└── PRJ301_Clinic/                  # THƯ MỤC DỰ ÁN NETBEANS ANT JAVA WEB
    ├── build.xml                   # Ant Build Script
    ├── src/java/                   # GÓI MÃ NGUỒN JAVA
    │   ├── config/                 # DBContext.java (HikariCP Pool SQL Server)
    │   ├── constant/               # RoleConstant.java, SystemConstant.java, RouterConstant.java
    │   ├── model/                  # 8 Models POJO (User, Service, Appointment, MedicalRecord...)
    │   ├── dao/                    # 7 DAOs JDBC thuần (UserDAO, AppointmentDAO, MedicalRecordDAO...)
    │   ├── service/                # Business Services & Transactions
    │   ├── controller/             # Servlets Controllers (Auth, Booking, Doctor, Receptionist, SepayWebhook)
    │   ├── filter/                 # 3 Security Filters (Encoding, Auth, Role)
    │   ├── exception/              # AppException, SlotAlreadyBookedException...
    │   └── util/                   # BCryptUtil, ValidationUtil...
    ├── test/                       # BỘ TEST JUNIT 5
    └── web/                        # THƯ MỤC WEBAPP
        ├── assets/                 # css/style.css, js/, images/
        └── WEB-INF/
            ├── lib/                # Bộ thư viện JAR (mssql-jdbc, HikariCP, jbcrypt, jackson, jstl, junit5...)
            ├── views/
            │   ├── auth/           # login.jsp, register.jsp (Dual-Panel Glassmorphism)
            │   ├── patient/        # booking.jsp, payment.jsp, history.jsp
            │   ├── doctor/         # dashboard.jsp (Workspace Bác Sĩ)
            │   ├── receptionist/   # dashboard.jsp (Workspace Lễ Tân)
            │   ├── public/         # home.jsp
            │   ├── components/     # navbar.jsp, footer.jsp, head.jsp, alerts.jsp
            │   └── error/          # 403.jsp, 404.jsp, 500.jsp
            └── web.xml             # Deployment Descriptor
```

---

## ⚡ HƯỚNG DẪN CÀI ĐẶT & CHẠY DỰ ÁN

### 1. Khởi tạo CSDL SQL Server:
- Mở **SQL Server Management Studio (SSMS)**.
- Mở file `database.sql` và thực thi (Execute) để tạo CSDL `PRJ301_ClinicDB`, 7 bảng, dữ liệu mẫu và các Trigger/Procedures.

### 2. Cấu hình Kết nối CSDL:
- Mở file `PRJ301_Clinic/src/java/config/DBContext.java`.
- Cập nhật Username (`sa`) và Mật khẩu SQL Server của máy bạn ở dòng 23-24:
  ```java
  config.setUsername("sa");
  config.setPassword("YOUR_SQL_SERVER_PASSWORD");
  ```

### 3. Cấu hình SePay Webhook (Tùy chọn - Dùng nút Mô Phỏng nếu test local):
- Đặt biến môi trường `SEPAY_SECRET_KEY` với Secret Key từ SePay Dashboard.
- Trong SePay Dashboard → Tiền tố mã thanh toán: `CLN` (3 ký tự).

### 4. Mở và Chạy Dự án trên NetBeans:
- Mở **NetBeans IDE**.
- Chọn **File** -> **Open Project** -> Chọn thư mục `PRJ301_Clinic`.
- Chuột phải vào project chọn **Clean and Build**.
- Chuột phải vào project chọn **Run** (chạy trên Tomcat 8.5/9.0).

---

## 🌐 DANH SÁCH TÀI KHOẢN MẶC ĐỊNH (SEED DATA ACCOUNTS)

| Username | Password | Vai trò (Role) | Họ và tên |
| :--- | :--- | :--- | :--- |
| `admin` | `123456` | **ADMIN** | Quản trị viên Nguyễn Văn Admin |
| `drminh` | `123456` | **DOCTOR** | BS. Nguyễn Văn Minh (Nha Khoa Thẩm Mỹ) |
| `drlan` | `123456` | **DOCTOR** | BS. Trần Thị Lan (Da Liễu & Spa) |
| `patient1` | `123456` | **PATIENT** | Bệnh nhân Lê Hoàng Nam |
| `patient2` | `123456` | **PATIENT** | Bệnh nhân Phạm Thị Hoa |
| `receptionist1` | `123456` | **RECEPTIONIST** | Lễ tân Phạm Thị Mai |

---

## 🗺️ DANH SÁCH ĐƯỜNG DẪN URL

| URL | Mô Tả | Quyền Truy Cập |
| :--- | :--- | :--- |
| `/MainController?action=home` | Trang Chủ | Tất cả |
| `/login` | Đăng Nhập | Khách |
| `/register` | Đăng Ký | Khách |
| `/booking` | Đặt Lịch Khám | PATIENT |
| `/booking?action=payment&id=X` | Thanh Toán VietQR SePay | PATIENT |
| `/history` | Lịch Sử Khám & Đơn Thuốc | PATIENT |
| `/doctor/dashboard` | Workspace Bác Sĩ | DOCTOR |
| `/receptionist/dashboard` | Workspace Lễ Tân | RECEPTIONIST |
| `/sepay-webhook` | Webhook SePay (POST) | SePay Server |

---

## 📝 GIẤY PHÉP & BẢO QUYỀN

Được thực hiện độc lập bởi sinh viên môn PRJ301, tuân thủ nghiêm ngặt 100% quy định Hard Rules môn học.
