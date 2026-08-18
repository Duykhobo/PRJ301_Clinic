# 🛡️ PROJECT CORE RULES & STRICT ARCHITECTURAL GUIDELINES (PRJ301)

> **CRITICAL & MANDATORY INSTRUCTION FOR ALL AI ASSISTANTS & AGENTS**:
> 1. **Single Source of Truth (SSOT)**: You MUST continuously track, strictly adhere to, and keep [Topic_Proposal_PRJ301.md](file:///c:/Users/ThanhDuy/Documents/02_Study_Active/FPT/SEMSTER_4_SUMMER26/PRJ/PRJ301_3W_ASSIGNMENT/Topic_Proposal_PRJ301.md) updated at all times throughout the project lifecycle.
> 2. **UI/UX Reference Standards**: You MUST reference the Design Review Kit documented in [DESIGN_REVIEW_KIT.html](file:///c:/Users/ThanhDuy/Documents/02_Study_Active/FPT/SEMSTER_4_SUMMER26/PRJ/PRJ301_3W_ASSIGNMENT/DESIGN_REVIEW_KIT.html) for all front-end UI/UX implementations.
> 3. **Clean Code & Field-Level Validation**: You MUST enforce concise code (using ternary operators `?:`), full field-level error separation on forms, and strict feature branching with automated CI/CD.
> 4. **DRY Principle (Don't Repeat Yourself)**: You MUST strictly prevent code duplication across all architectural layers (DAO, Service, Controller, View, CSS).
> 5. **SOLID & ACID Strict Enforcement**: You MUST adhere 100% to SOLID object-oriented design and ACID transaction management principles.

---

## 📌 1. HARD TECHNICAL RULES (BẮT BUỘC TUÂN THỦ 100%)

Theo tài liệu đặc tả [Topic_Proposal_PRJ301.md](file:///c:/Users/ThanhDuy/Documents/02_Study_Active/FPT/SEMSTER_4_SUMMER26/PRJ/PRJ301_3W_ASSIGNMENT/Topic_Proposal_PRJ301.md):

1. **Kiến trúc Hệ thống**: 
   - Chuẩn **Java Web EE MVC-V2 3-Tier Enterprise** (NetBeans Ant Project).
   - Tách biệt rõ ràng 3 tầng: `Controller (Servlet - Thin)` $\rightarrow$ `Service (Fat Business Logic + Transaction)` $\rightarrow$ `DAO (Pure JDBC)`.
   - Tuyệt đối **KHÔNG dùng Spring Boot, Hibernate, JPA** (vi phạm quy chế môn học PRJ301).

2. **CSDL & Connection Management**:
   - Hệ quản trị: **Microsoft SQL Server**.
   - Connection Pool: **HikariCP** (`HikariCP-5.x.x.jar` / `3.4.5.jar`).
   - Quản lý giao dịch: **ThreadLocal Connection** kết hợp `TransactionFilter` tự động commit/rollback theo từng HTTP Request lifecycle.
   - JDBC thuần: 100% dùng `PreparedStatement` và cú pháp `try-with-resources` để đóng Connection/ResultSet phòng ngừa rò rỉ bộ nhớ (Connection Leak).
   - Chống Race Condition: Áp dụng `WITH (UPDLOCK, HOLDLOCK)` và ràng buộc CSDL (`UNIQUE (doctor_id, schedule_date, slot_time)`).

3. **Bảo mật & 4 Lớp Filters**:
   - `EncodingFilter`: Đảm bảo UTF-8 cho toàn bộ request/response.
   - `TransactionFilter`: Quản lý mở/đóng/commit/rollback kết nối theo vòng đời request.
   - `AuthenticationFilter`: Kiểm tra phiên làm việc (Session).
   - `AuthorizationFilter`: Phân quyền nghiêm ngặt 4 vai trò: `ADMIN`, `DOCTOR`, `PATIENT`, `RECEPTIONIST`.
   - Mật khẩu: 100% băm bằng **BCrypt** (`org.mindrot:jbcrypt`), không bao giờ lưu plain text.

4. **Tích hợp Thanh toán Tự động**:
   - **SePay VietQR API**: Tự sinh mã QR động kèm số tiền và nội dung chuyển khoản tự khóa cú pháp `CLINIC<appointment_id>` hoặc `SZ<id>`.
   - **Webhook SePay**: Tiếp nhận & xác thực tự động, kết hợp Client Polling 3s và kịch bản cứu hộ dự phòng (Nút Giả lập Webhook & Nút Tiền mặt).

---

## 🎨 2. UI/UX DESIGN SYSTEM GUIDELINES

Mọi màn hình JSP và Stylesheet phải tuân thủ chuẩn [DESIGN_REVIEW_KIT.html](file:///c:/Users/ThanhDuy/Documents/02_Study_Active/FPT/SEMSTER_4_SUMMER26/PRJ/PRJ301_3W_ASSIGNMENT/DESIGN_REVIEW_KIT.html):

1. **Tokens & Bảng màu**:
   - Giao diện hài hòa, hiện đại với độ tương phản cao, hỗ trợ mobile responsive 100%.
   - Trạng thái trực quan: Badge bo tròn (`.sz-badge-*`), Soft Alerts (`.alert-soft` s-success, s-danger, s-warn, s-info).
   
2. **Hệ thống Component Core (P0)**:
   - **Buttons**: Dạng pill bo tròn (`.btn-sz`), hiệu ứng hover nhấc nhẹ (`transform: translateY(-2px); box-shadow: ...`), nút icon tròn trong bảng (`.btn-icon`).
   - **Cards**: `.sz-card` với border mềm, shadow nhẹ, hover lift (`.hover-lift`).
   - **Form Controls**: Input không viền gắt, background mềm (`.sz-input`, `.form-control-glass`), focus ring rõ nét, toggle ẩn/hiện mật khẩu (`.input-eye`).
   - **Filter Pills**: Thanh lọc dạng tray bo tròn (`.pill-tray` + `.filter-pill`), active hiển thị rõ nét với badge đếm số lượng (`.sz-count`).
   - **Tables**: Header uppercase nền sáng, dòng hover mượt, cột tiền căn phải, tự động hỗ trợ responsive stack trên màn hình nhỏ.
   - **Date Selector & Time Slots**: Hàng pill chọn ngày (`.date-pill-row`), lưới khung giờ (`.slot-grid` / `.slot`) với 5 trạng thái rõ ràng: *Trống (Available)*, *Đang chọn (Selected)*, *Đã đặt (Booked)*, *Đã qua (Past)*, *Bảo trì/Đóng (Closed)*.
   - **Invoices**: Layout dạng giấy in chuyên nghiệp (`.invoice-paper`), dùng chung cho chế độ xem và in ấn (`window.print()`).
   - **Admin Layout**: Cấu trúc Shell hiện đại (`.adm-shell`, `.adm-side`, `.adm-main`), KPI cards gradient 6 màu SaaS, quản lý khung giờ trực quan dạng tile.

---

## 🧼 3. CLEAN CODE, FIELD-LEVEL VALIDATION & DRY ENFORCEMENT

1. **Toán tử 3 ngôi (`?:`) & Code tinh gọn**:
   - Luôn sử dụng toán tử 3 ngôi cho việc gán giá trị mặc định, kiểm tra null-safety, trim chuỗi và render trạng thái giao diện:
     ```java
     String username = request.getParameter("username") != null ? request.getParameter("username").trim() : "";
     ```
   - Tránh tuyệt đối viết các khối `if-else` lồng ghép dài dòng không cần thiết.

2. **Tách lỗi chi tiết cho từng Field (Field-Level Error Separation)**:
   - Tất cả các form (`/register`, `/login`, `/forgot-password`, `/profile`, `/booking`...) phải thu thập lỗi vào `Map<String, String> errors`.
   - Đánh dấu trực quan `.is-invalid` (viền đỏ `#f87171` + background mờ) trên từng input bị sai.
   - Hiển thị dòng thông báo lỗi riêng biệt kèm biểu tượng ngay dưới từng ô input:
     ```jsp
     <c:if test="${not empty errors.fieldName}">
         <div class="field-error-text"><i class="fa-solid fa-circle-exclamation"></i><span>${errors.fieldName}</span></div>
     </c:if>
     ```
   - **Sticky Form Inputs**: Luôn giữ lại toàn bộ dữ liệu hợp lệ mà người dùng đã nhập (`value="${...}"`), không bao giờ xóa trắng form bắt người dùng nhập lại.

3. **Triệt tiêu lặp code (DRY - Don't Repeat Yourself Principle)**:
   - **Tầng DAO**: Kế thừa `BaseDAO` / `RowMapper` cho các câu truy vấn JDBC mẫu, không copy-paste các đoạn code mở ResultSet/PreparedStatement giống hệt nhau.
   - **Tầng Controller**: Kế thừa `BaseRoleServlet` cho kiểm tra Role và phân trang. Luôn sử dụng hằng số từ `RouterConstant`, `MessageConstant`, `SystemConstant`, `RoleConstant` thay vì hardcode chuỗi string.
   - **Tầng Service & Util**: Tái sử dụng logic nghiệp vụ và đóng gói validation/hashing/date format vào `util.*` (`ValidationUtil`, `DateUtil`, `BCryptUtil`, `JsonUtil`, `SePayQRUtil`).
   - **Tầng JSP View**: Tách các thành phần giao diện lặp lại vào thư mục `/WEB-INF/views/components/` (`head.jsp`, `navbar.jsp`, `footer.jsp`, `alerts.jsp`, `sidebar-*.jsp`) và tái sử dụng qua `<jsp:include>`.
   - **Tầng CSS**: Khai báo và sử dụng biến Design Tokens (`:root`) và utility classes dùng chung, không viết lặp inline CSS style.

---

## 🧱 4. SOLID PRINCIPLES (ÁP DỤNG TRONG JAVA MVC-V2)

1. **S - Single Responsibility Principle (Đơn trách nhiệm)**:
   - `Controller (Servlet - Thin)`: Chỉ tiếp nhận HTTP request, trích xuất tham số, gọi Service và điều hướng View (`forward` / `sendRedirect`). Tuyệt đối không chứa logic nghiệp vụ hay câu lệnh SQL.
   - `Service (Fat)`: Chịu trách nhiệm 100% về quy tắc nghiệp vụ (Business Rules), tính toán giá tiền và quản lý giao dịch CSDL.
   - `DAO`: Chuyên biệt truy vấn CSDL thuần JDBC, đóng gói câu lệnh SQL và ánh xạ `ResultSet` $\rightarrow$ Model POJO.
   - `Model`: Chỉ chứa thuộc tính (fields), Getters/Setters, Constructors, `toString()`. Không chứa mã web hay SQL.

2. **O - Open/Closed Principle (Mở rộng, Đóng thay đổi)**:
   - `BaseDAO` và `RowMapper<T>` cho phép dễ dàng mở rộng thêm các Entity/DAO mới mà không cần chỉnh sửa mã nguồn cốt lõi.

3. **L - Liskov Substitution Principle (Thay thế Liskov)**:
   - Các lớp con của `BaseDAO` hoặc `BaseRoleServlet` phải tuân thủ nghiêm ngặt hợp đồng của lớp cha, có thể thay thế mà không làm thay đổi tính đúng đắn của chương trình.

4. **I - Interface Segregation Principle (Phân tách Interface)**:
   - Sử dụng các Functional Interface và Abstraction gọn nhẹ (`RowMapper<T>`), không gộp các hàm không liên quan vào một Interface cồng kềnh.

5. **D - Dependency Inversion / Decoupling (Đảo ngược phụ thuộc & Giảm kết dính)**:
   - Controller phụ thuộc vào tầng Service; Service phụ thuộc vào tầng DAO; không gọi phụ thuộc chéo lộn xộn giữa Controller $\leftrightarrow$ DAO.

---

## ⚡ 5. ACID PROPERTIES TRONG QUẢN LÝ GIAO DỊCH CSDL

1. **A - Atomicity (Tính Nguyên tử - Tất cả hoặc Không gì cả)**:
   - Các nghiệp vụ phức hợp (như *Bệnh nhân đặt lịch*: Insert `Appointment` + Khóa `DoctorSchedule` thành `BOOKED` + Tạo `Payment`) phải thực thi trong cùng 1 Transaction duy nhất.
   - Bất kỳ bước nào xảy ra lỗi $\rightarrow$ Lập tức `ROLLBACK` 100%, không để lại dữ liệu dở dang hoặc rác CSDL. Quản lý tự động qua `TransactionFilter` & `ThreadLocal Connection`.

2. **C - Consistency (Tính Nhất quán)**:
   - Mọi thao tác ghi dữ liệu phải bảo toàn 100% các ràng buộc toàn vẹn CSDL (Khóa ngoại Foreign Keys, Check Constraints, Unique Constraints, Triggers kiểm tra slot).

3. **I - Isolation (Tính Cô lập & Chống Race Condition)**:
   - Áp dụng gợi ý khóa mức dòng trong SQL Server: `WITH (UPDLOCK, HOLDLOCK)` khi truy vấn kiểm tra và giữ khung giờ khám.
   - Triệt tiêu hoàn toàn nguy cơ *Race Condition*, *Lost Update*, *Dirty Read* khi nhiều bệnh nhân cùng đặt 1 slot trong cùng 1 mili-giây.

4. **D - Durability (Tính Bền vững)**:
   - Sau khi giao dịch đã `COMMIT` thành công, dữ liệu được ghi nhận vĩnh viễn vào ổ đĩa và Transaction Log của SQL Server, đảm bảo an toàn tuyệt đối ngay cả khi server gặp sự cố khởi động lại.

---

## 🌿 6. GIT WORKFLOW & CI/CD ENFORCEMENT

1. **Chiến lược Tách Nhánh (Feature Branch Isolation)**:
   - Mỗi Task lớn / Epic bắt buộc phải được tạo và phát triển trên **nhánh Git riêng biệt**:
     - `feature/sepay-payment-integration`
     - `feature/ui-design-kit-sync`
     - `feature/service-transaction-hardening`
     - `feature/testing-qa-suite`
     - `feature/ci-cd-pipeline`
     - `feature/release-docs-and-demo`
   - Chỉ merge vào `develop` khi đã test pass, và merge vào `main` khi release.

2. **Quy chuẩn Commit (Conventional Commits)**:
   - `feat:`, `fix:`, `refactor:`, `test:`, `ci:`, `docs:`, `chore:`.

3. **CI/CD Pipeline Tự Động ([.github/workflows/ci-cd.yml](file:///c:/Users/ThanhDuy/Documents/02_Study_Active/FPT/SEMSTER_4_SUMMER26/PRJ/PRJ301_3W_ASSIGNMENT/.github/workflows/ci-cd.yml))**:
   - **CI (Build & Test)**: Tự động chạy trên mọi PR / Push nhánh `feature/**`, `develop`, `main` (Cài JDK 8, Apache Ant, chạy `ant compile` và `ant dist`).
   - **CD (Deploy & Release)**: Chỉ kích hoạt khi Merge vào nhánh `main` (Đóng gói `.war`, tự sinh Release Tag `v1.0.0-build-*` và publish GitHub Release).

---

## 🔄 7. QUY TRÌNH ĐỒNG BỘ TÀI LIỆU DỰ ÁN

Mỗi khi có sự thay đổi về:
1. Thiết kế bảng CSDL hoặc DAO/Service/Controller mới $\rightarrow$ Phải cập nhật ngay vào bảng đối chiếu và ma trận tính năng trong [Topic_Proposal_PRJ301.md](file:///c:/Users/ThanhDuy/Documents/02_Study_Active/FPT/SEMSTER_4_SUMMER26/PRJ/PRJ301_3W_ASSIGNMENT/Topic_Proposal_PRJ301.md).
2. Quy trình kiểm thử (JUnit 5, Postman) và tiến độ Checkpoint $\rightarrow$ Đồng bộ vào [Topic_Proposal_PRJ301.md](file:///c:/Users/ThanhDuy/Documents/02_Study_Active/FPT/SEMSTER_4_SUMMER26/PRJ/PRJ301_3W_ASSIGNMENT/Topic_Proposal_PRJ301.md), [Agile_Development_Guide.md](file:///c:/Users/ThanhDuy/Documents/02_Study_Active/FPT/SEMSTER_4_SUMMER26/PRJ/PRJ301_3W_ASSIGNMENT/Agile_Development_Guide.md) và [TASKS_AND_TODO.md](file:///c:/Users/ThanhDuy/Documents/02_Study_Active/FPT/SEMSTER_4_SUMMER26/PRJ/PRJ301_3W_ASSIGNMENT/TASKS_AND_TODO.md).
3. Thêm mới các trang JSP / View $\rightarrow$ Kế thừa đúng các component từ Design Review Kit.
