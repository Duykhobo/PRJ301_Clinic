# 🏥 PRJ301 - HỆ THỐNG ĐẶT LỊCH PHÒNG KHÁM & SPA NHA KHOA QUỐC TẾ (PRJ301_ClinicDB)

> **Môn học**: PRJ301 - Java Web Application Development  
> **Hình thức**: Bài tập cá nhân (Individual Assignment)  
> **Công nghệ**: NetBeans Java Web (Ant System), Pure JDBC, Microsoft SQL Server, HikariCP, BCrypt, SePay VietQR  

---

## 📌 GIỚI THIỆU TỔNG QUAN

**PRJ301_ClinicDB** là hệ thống quản lý và đặt lịch hẹn khám bệnh / spa làm đẹp trực tuyến hiện đại. Hệ thống cung cấp giải pháp đặt lịch hẹn theo các khung giờ 60 phút khả dụng, hỗ trợ thanh toán tự động qua **Cổng thanh toán SePay VietQR** (tự động khóa số tiền và nội dung chuyển khoản `CLINIC<id>`), đối soát Webhook tự động, quản lý bệnh án bảo mật và cung cấp công cụ quản trị toàn diện cho phòng khám.

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
3. **Thanh toán Tự động SePay VietQR**:
   - Tự động sinh mã `payment_content = "CLINIC" + appointmentId`.
   - Tạo VietQR nhúng sẵn số tiền & cú pháp `CLINIC<id>`, tự động khóa thông tin trên App Ngân hàng.
   - Servlet tiếp nhận Webhook JSON từ SePay để tự động đổi `payment_status = 'PAID'`.
   - Nút **Manual Verify** trên trang Admin hỗ trợ duyệt tay nếu khách hàng chuyển sai cú pháp.
4. **Bảo mật Dữ liệu Y tế (Medical Privacy Scoping)**:
   - Phân tách DAO: `getMedicalRecordDetail()` cho Bác sĩ & Bệnh nhân ca khám; `getPublicReviews()` chỉ lấy rating và comment công khai, ẩn chẩn đoán và đơn thuốc.
5. **Cấu hình Động Hệ thống (`ClinicSettings`)**:
   - Admin có thể thay đổi thời lượng slot, khung giờ mở cửa, tên phòng khám, STK ngân hàng trực tiếp trên Web mà không cần sửa code hay rebuild project.

---

## 🛠️ CÔNG NGHỆ & THƯ VIỆN SỬ DỤNG (TECH STACK)

- **Back-end Core**: Java EE 8 (Servlet 3.1, JSP 2.3, JSTL 1.2).
- **IDE & Build Tool**: NetBeans IDE (Ant Build System).
- **CSDL**: Microsoft SQL Server (Driver `mssql-jdbc-12.4.2.jre8.jar`).
- **Connection Pool**: HikariCP `3.4.5` (Dành riêng cho Java 8).
- **Mã hóa Mật khẩu**: jBCrypt `0.4`.
- **JSON Parser**: Jackson Databind `2.15.2`.
- **Testing Suite**: JUnit 5, Postman, Apache JMeter.
- **Front-end**: HTML5, CSS3, JavaScript ES6+, Bootstrap 5.3 Responsive.

---

## 📐 MA TRẬN PHÂN LOẠI TÍNH NĂNG THEO ĐỘ ƯU TIÊN

| Mức độ Ưu tiên | Hạng mục Tính năng & Kỹ thuật | Trạng thái |
| :--- | :--- | :--- |
| 🔴 **MỨC 1: BẮT BUỘC** *(Core Hard Rules)* | • **Enterprise 3-Tier Architecture** (`Controller` $\rightarrow$ `Service` $\rightarrow$ `DAO`).<br/>• Pure JDBC (`PreparedStatement`) & HikariCP SQL Server.<br/>• BCrypt Hashing & 3 Filters (`Encoding`, `Auth`, `Role`).<br/>• Phân quyền 4 Roles (`ADMIN`, `DOCTOR`, `PATIENT`, `RECEPTIONIST`).<br/>• 100% CRUD trên 7 Bảng CSDL.<br/>• Chống Race Condition: `WITH (UPDLOCK)` & `UNIQUE(schedule_id)`. | **BẮT BUỘC 100%** (Tiêu chí qua môn) |
| 🟡 **MỨC 2: QUAN TRỌNG** *(Real-World MVP)* | • Thanh toán tự động SePay VietQR Động (`CLINIC<id>`).<br/>• Webhook SePay đối soát tự động & Manual Verify cho Admin.<br/>• Bảng `ClinicSettings` Cấu hình Động.<br/>• Phân quyền Bảo mật Y tế (Medical Privacy Scoping DAO). | **HOÀN THIỆN MVP** (Tiêu chí thực tế) |
| 🟢 **MỨC 3: MỞ RỘNG** *(Advanced 10/10)* | • 4 thành phần SQL Server nâng cao (Trigger `trg_UpdateSlotStatusOnAppointment`, Stored Procs `sp_GetClinicRevenueReport`, `sp_GetAvailableSlotsByDoctorAndDate`, Function `fn_GetDoctorAverageRating`).<br/>• Bộ kiểm thử JUnit 5, Postman, Apache JMeter Concurrency Test, JaCoCo Coverage Report, Cypress E2E UI Test & Lighthouse. | **ĐIỂM CỘNG NÂNG CAO** (Tiêu chí Điểm 10/10) |

---

## 📂 CẤU TRÚC THƯ MỤC DỰ ÁN (PROJECT STRUCTURE)

```text
PRJ301_Assignment/
├── database.sql                    # Script CSDL SQL Server (Schema 7 Bảng + Mock Data + Triggers/Procedures)
├── Topic_Proposal_PRJ301.md        # Bản Đề xuất Đề tài & Đặc tả SRS chi tiết
├── Agile_Development_Guide.md      # Hướng dẫn Quản lý Dự án Agile & Thói quen Code Sạch
├── README.md                       # Tài liệu hướng dẫn dự án
├── .gitignore                      # File cấu hình bỏ qua file build tạm của NetBeans
└── PRJ301_Clinic/                  # THƯ MỤC DỰ ÁN NETBEANS ANT JAVA WEB
    ├── build.xml                   # Ant Build Script
    ├── src/java/                   # GÓI MÃ NGUỒN JAVA
    │   ├── config/                 # DBContext.java (HikariCP Pool SQL Server)
    │   ├── constant/               # RoleConstant.java, SystemConstant.java
    │   ├── model/                  # 7 Models POJO (User, Service, Appointment...)
    │   ├── dao/                    # 7 DAOs JDBC thuần (UserDAO, AppointmentDAO...)
    │   ├── service/                # Business Services & Transactions
    │   ├── controller/             # Servlets Controllers (Auth, Patient, Doctor, Admin, Webhook)
    │   ├── filter/                 # 3 Security Filters (Encoding, Auth, Role)
    │   ├── exception/              # AppException, SlotAlreadyBookedException, UnauthorizedException...
    │   └── util/                   # BCryptUtil, ValidationUtil, SePayQRUtil...
    ├── test/                       # BỘ TEST JUNIT 5
    │   ├── dao/                    # UserDAOTest.java, AppointmentDAOTest.java...
    │   ├── util/                   # BCryptUtilTest.java...
    │   ├── service/                # AppointmentServiceTest.java...
    │   └── TestDBConnection.java   # File test kết nối CSDL SQL Server
    └── web/                        # THƯ MỤC WEBAPP
        ├── assets/                 # css/, js/, images/, bootstrap/
        └── WEB-INF/
            ├── lib/                # Bộ 14 thư viện JAR (mssql-jdbc, HikariCP, jbcrypt, jackson, jstl, junit5...)
            ├── views/              # JSP Views (auth/, patient/, doctor/, admin/, common/)
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

### 3. Thử nghiệm Kết nối CSDL:
- Chạy file `test/TestDBConnection.java` trên IDE để kiểm tra kết nối HikariCP tới SQL Server thành công.

### 4. Mở và Chạy Dự án trên NetBeans:
- Mở **NetBeans IDE**.
- Chọn **File** -> **Open Project** -> Chọn thư mục `PRJ301_Clinic`.
- Chuột phải vào project chọn **Clean and Build**.
- Chuột phải vào project chọn **Run** (chạy trên Tomcat 8.5/9.0 hoặc GlassFish).

---

## 🌐 DANH SÁCH TÀI KHOẢN MẶC ĐỊNH (SEED DATA ACCOUNTS)

| Username | Password | Vai trò (Role) | Họ và tên |
| :--- | :--- | :--- | :--- |
| `admin` | `123456` | **ADMIN** | Quản trị viên Nguyễn Văn Admin |
| `doctor1` | `123456` | **DOCTOR** | BS. Bùi Văn Minh (Nha Khoa Thẩm Mỹ) |
| `doctor2` | `123456` | **DOCTOR** | BS. Trần Thị Hồng (Da Liễu & Spa) |
| `patient1` | `123456` | **PATIENT** | Bệnh nhân Lê Hoàng Nam |
| `receptionist1` | `123456` | **RECEPTIONIST** | Lễ tân Phạm Thị Mai |

---

## 📝 GIẤY PHÉP & BẢO QUYỀN

Được thực hiện độc lập bởi sinh viên môn PRJ301, tuân thủ nghiêm ngặt 100% quy định Hard Rules môn học.
