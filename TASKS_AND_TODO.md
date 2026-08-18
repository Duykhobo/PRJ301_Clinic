# 🎯 DANH MỤC TASK & TODO TIẾN ĐỘ DỰ ÁN (PRJ301_CLINIC)

> **Mục tiêu**: Hoàn thiện toàn bộ hệ thống Java Web MVC-V2 chuẩn Enterprise, tích hợp thanh toán SePay QR tự động, đồng bộ Design Kit UI/UX và hoàn thiện bộ kiểm thử trước **Hard Deadline 22/08/2026**.
> **Tài liệu quy chuẩn (SSOT)**: [Topic_Proposal_PRJ301.md](file:///c:/Users/ThanhDuy/Documents/02_Study_Active/FPT/SEMSTER_4_SUMMER26/PRJ/PRJ301_3W_ASSIGNMENT/Topic_Proposal_PRJ301.md) & [DESIGN_REVIEW_KIT.html](file:///c:/Users/ThanhDuy/Documents/02_Study_Active/FPT/SEMSTER_4_SUMMER26/PRJ/PRJ301_3W_ASSIGNMENT/DESIGN_REVIEW_KIT.html).

---

## 🌳 1. BẢNG PHÂN NHÁNH GIT THEO TỪNG TASK LỚN (FEATURE BRANCH MATRIX)

| Nhóm Task Lớn (Epic) | Tên Nhánh Git (`feature/`) | Mức Ưu Tiên | Trạng Thái | Deadline Dự Kiến |
| :--- | :--- | :---: | :---: | :---: |
| **Epic 1: Tích hợp Thanh toán SePay QR & Webhook** | `feature/sepay-payment-integration` | 🔴 **P0** | 🔄 In Progress | **18/08 (Hôm nay)** |
| **Epic 2: Đồng bộ Giao diện UI/UX Design Kit** | `feature/ui-design-kit-sync` | 🔴 **P0** | 🔄 In Progress | **19/08 (Ngày mai)** |
| **Epic 3: Atomic Service & Chống Race Condition** | `feature/service-transaction-hardening` | 🟡 **P1** | ⏳ Todo | **20/08** |
| **Epic 4: Bộ Kiểm thử JUnit 5, Postman & JMeter** | `feature/testing-qa-suite` | 🟡 **P1** | ⏳ Todo | **20/08** |
| **Epic 5: Đóng gói Release, Video Demo & Slide** | `feature/release-docs-and-demo` | 🔴 **P0** | ⏳ Todo | **21/08 (Code Freeze)** |

---

## 📌 2. CHI TIẾT TỪNG NHÁNH VÀ CHECKLIST CÔNG VIỆC (TASK BREAKDOWN)

### 🌿 Nhánh 1: `feature/sepay-payment-integration`
*Mục tiêu: Đảm bảo luồng tạo QR $\rightarrow$ Polling $\rightarrow$ Webhook $\rightarrow$ Auto Confirm $\rightarrow$ Fallback chạy mượt 100%.*
* **Lệnh chuyển nhánh**: `git checkout feature/sepay-payment-integration`

- [ ] **Task 1.1**: Kiểm tra bộ sinh mã QR SePay tự động (`SePayQRUtil` / `BookingServlet`)
  - [ ] Sinh ảnh VietQR với đúng số tiền và nội dung chuyển khoản tự khóa cú pháp `CLINIC<appointment_id>`.
  - [ ] Hiển thị thông tin chuyển khoản rõ ràng trên `payment.jsp` (Số tài khoản, Tên chủ tài khoản, Ngân hàng MBBank).
- [ ] **Task 1.2**: Hoàn thiện cơ chế Realtime Polling phía Client
  - [ ] Tích hợp JS `setInterval()` 3s/lần gọi API `/api/check-payment-status?id=...`.
  - [ ] Khi status chuyển thành `PAID` $\rightarrow$ Hiển thị Modal/Alert thành công và tự động chuyển hướng sang `history.jsp` hoặc trang Chi tiết hóa đơn.
- [ ] **Task 1.3**: Hoàn thiện Webhook Handler (`SepayWebhookServlet.java`)
  - [ ] Nhận payload JSON từ SePay, validate API Token / Secret.
  - [ ] Trích xuất mã `CLINIC<id>`, cập nhật `payment_status = 'PAID'` và `status = 'CONFIRMED'`.
  - [ ] Commit Transaction an toàn thông qua `TransactionFilter`.
- [ ] **Task 1.4**: Kịch bản Cứu hộ / Demo Fallback (Bảo vệ đồ án an toàn 100%)
  - [ ] Nút **[ 🔴 Giả lập Webhook SePay (Demo) ]**: Gửi request POST giả lập đối soát ngay lập tức.
  - [ ] Nút **[ 💳 Chọn Thanh toán Tiền mặt khi đến ]**: Đổi `payment_method = 'CASH'`, chuyển trạng thái `PENDING` sang danh sách chờ xác nhận của Lễ tân/Admin.

---

### 🌿 Nhánh 2: `feature/ui-design-kit-sync`
*Mục tiêu: Đồng bộ toàn bộ các trang JSP theo chuẩn thẩm mỹ từ [DESIGN_REVIEW_KIT.html](file:///c:/Users/ThanhDuy/Documents/02_Study_Active/FPT/SEMSTER_4_SUMMER26/PRJ/PRJ301_3W_ASSIGNMENT/DESIGN_REVIEW_KIT.html).*
* **Lệnh chuyển nhánh**: `git checkout feature/ui-design-kit-sync`

- [ ] **Task 2.1**: Nâng cấp Stylesheet chung (`style.css` & `dashboard.css`)
  - [ ] Thêm các class chuẩn: `.btn-sz`, `.btn-icon`, `.sz-card`, `.sz-badge-*`, `.alert-soft`, `.pill-tray`, `.slot-grid`, `.invoice-paper`.
  - [ ] Đảm bảo bảng dữ liệu responsive tự động co giãn (`.sz-table` & card stack trên mobile).
- [ ] **Task 2.2**: Đồng bộ phân hệ Khách hàng / Bệnh nhân (Patient Views)
  - [ ] `web/WEB-INF/views/patient/booking.jsp`: Wizard 4 bước (Chọn Bác sĩ/Dịch vụ $\rightarrow$ Chọn Ngày $\rightarrow$ Chọn Khung Giờ Slot Grid $\rightarrow$ Xác nhận & Ghi chú) + Sticky Summary Card realtime.
  - [ ] `web/WEB-INF/views/patient/payment.jsp`: Giao diện thanh toán QR 2 cột hiện đại.
  - [ ] `web/WEB-INF/views/patient/history.jsp`: Tab Lịch sắp tới / Lịch sử, tích hợp In Hóa đơn Paper Style (`.invoice-paper`).
- [ ] **Task 2.3**: Đồng bộ phân hệ Xác thực & Hồ sơ (Auth & Profile Views)
  - [ ] `login.jsp`, `register.jsp`, `forgot-password.jsp`: Form input nền mềm, toggle ẩn/hiện mật khẩu `.input-eye`.
  - [ ] `profile.jsp`: Form thông tin cá nhân + đổi mật khẩu bảo mật.
- [ ] **Task 2.4**: Đồng bộ phân hệ Bác sĩ (Doctor Dashboard)
  - [ ] `doctor/dashboard.jsp`: Lưới lịch khám trong ngày, form nhập Chẩn đoán & Toa thuốc, đăng ký khung giờ rảnh.
- [ ] **Task 2.5**: Đồng bộ phân hệ Lễ tân (Receptionist Dashboard)
  - [ ] `receptionist/dashboard.jsp`: Check-in bệnh nhân, thu tiền mặt tại quầy, in phiếu khám bệnh.
- [ ] **Task 2.6**: Đồng bộ phân hệ Quản trị viên (Admin Dashboard)
  - [ ] `admin/dashboard.jsp`: Layout Admin Shell (`.adm-shell`), 6 KPI Cards SaaS gradient, Quản lý Users, Quản lý Bác sĩ/Dịch vụ & Cấu hình `ClinicSettings`.

---

### 🌿 Nhánh 3: `feature/service-transaction-hardening`
*Mục tiêu: Đảm bảo toàn vẹn dữ liệu, ThreadLocal Transaction và triệt tiêu Race Condition.*
* **Lệnh chuyển nhánh**: `git checkout feature/service-transaction-hardening`

- [ ] **Task 3.1**: Hoàn thiện Atomic Transaction trong `BookingService.java`
  - [ ] Nghiệp vụ đặt lịch: Insert Appointment + Cập nhật trạng thái Slot trong `DoctorSchedules` thành công hoặc rollback toàn bộ.
- [ ] **Task 3.2**: Chống Race Condition & Đặt trùng Slot
  - [ ] Áp dụng gợi ý khóa SQL Server: `WITH (UPDLOCK, HOLDLOCK)` tại câu lệnh kiểm tra slot khả dụng.
  - [ ] Xử lý ngoại lệ `SlotAlreadyBookedException` trả về thông báo Soft Alert thân thiện cho người dùng.
- [ ] **Task 3.3**: Hoàn thiện `UserService.java`, `ClinicService.java` & `MedicalRecordService.java`
  - [ ] Phân tách triệt để: Servlet chỉ gọi Service $\rightarrow$ Service điều phối DAO và quản lý Logic nghiệp vụ.

---

### 🌿 Nhánh 4: `feature/testing-qa-suite`
*Mục tiêu: Đảm bảo độ tin cậy của mã nguồn và xuất artifacts phục vụ bảo vệ đồ án.*
* **Lệnh chuyển nhánh**: `git checkout feature/testing-qa-suite`

- [ ] **Task 4.1**: Bộ Unit Test JUnit 5 trong `test/`
  - [ ] `test/dao/UserDAOTest.java`: Test CRUD, login đúng/sai mật khẩu, mã hóa BCrypt.
  - [ ] `test/dao/AppointmentDAOTest.java`: Test tạo lịch hẹn, truy vấn theo bệnh nhân/bác sĩ.
  - [ ] `test/dao/DoctorScheduleDAOTest.java`: Test cập nhật khung giờ và kiểm tra slot trống.
  - [ ] `test/util/BCryptUtilTest.java`: Test tính nhất quán của hàm băm và xác thực mật khẩu.
- [ ] **Task 4.2**: Postman / Bruno API Test Collection
  - [ ] Tạo file Postman JSON test toàn bộ luồng Auth, Booking, SePay Polling và Webhook.
- [ ] **Task 4.3**: Kịch bản Kiểm thử Tải Đồng thời (Apache JMeter)
  - [ ] File kịch bản JMeter (`.jmx`): Giả lập 50-100 threads đồng thời đặt 1 slot trong 1 giây để chứng minh không bị trùng lịch (Race Condition Free).
- [ ] **Task 4.4**: Báo cáo Code Coverage JaCoCo (> 80% coverage cho tầng DAO/Service).

---

### 🌿 Nhánh 5: `feature/release-docs-and-demo`
*Mục tiêu: Chuẩn bị đầy đủ tài liệu, video demo và slide cho buổi bảo vệ vấn đáp.*
* **Lệnh chuyển nhánh**: `git checkout feature/release-docs-and-demo`

- [ ] **Task 5.1**: Cập nhật hoàn chỉnh [README.md](file:///c:/Users/ThanhDuy/Documents/02_Study_Active/FPT/SEMSTER_4_SUMMER26/PRJ/PRJ301_3W_ASSIGNMENT/README.md)
  - [ ] Hướng dẫn cài đặt CSDL SQL Server & chạy Ant Build trong NetBeans.
  - [ ] Danh sách tài khoản mẫu 4 Roles (`Admin`, `Doctor`, `Patient`, `Receptionist`) kèm mật khẩu.
  - [ ] Bảng ánh xạ tính năng theo barem điểm PRJ301.
- [ ] **Task 5.2**: Slide Thuyết Trình Bảo Vệ Đồ Án (PPTX / PDF)
  - [ ] Slide 1-3: Đặt vấn đề, Lý do chọn đề tài, Mô hình Agile & Kiến trúc MVC-V2 3-tier.
  - [ ] Slide 4-7: Sơ đồ ERD 7 bảng, Công nghệ HikariCP, ThreadLocal Transaction, SePay VietQR.
  - [ ] Slide 8-10: Demo 4 phân hệ Role, Kịch bản Fallback, Kết quả kiểm thử JUnit 5 & JMeter.
- [ ] **Task 5.3**: Quay Video Demo Đồ Án (Full Flow 5-7 phút)
  - [ ] Kịch bản demo mạch lạc: Đăng ký $\rightarrow$ Đặt lịch $\rightarrow$ Quét SePay QR $\rightarrow$ Bác sĩ khám/kê đơn $\rightarrow$ Lễ tân check-in/in hóa đơn $\rightarrow$ Admin thống kê KPI.
- [ ] **Task 5.4**: Merge toàn bộ nhánh vào `develop` $\rightarrow$ `main` & Đóng gói nộp LMS.

---

## ⚡ 3. HƯỚNG DẪN QUY TRÌNH THỰC HIỆN GIT WORKFLOW

```bash
# 1. Chuyển sang nhánh tính năng cần làm:
git checkout feature/<tên-nhánh>

# 2. Code và test cục bộ (JUnit 5 / Browser)
# 3. Commit theo chuẩn Conventional Commits:
git add .
git commit -m "feat(sepay): implement dynamic QR generation and client polling endpoint"

# 4. Khi hoàn thành trọn vẹn 1 nhánh -> Merge vào nhánh develop:
git checkout develop
git merge feature/<tên-nhánh>
```
