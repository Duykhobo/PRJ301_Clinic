# 🛡️ PROJECT CORE RULES & ARCHITECTURAL GUIDELINES (PRJ301)

> **CRITICAL INSTRUCTION FOR ALL AI ASSISTANTS & AGENTS**:
> 1. **Single Source of Truth (SSOT)**: You MUST continuously track, adhere to, and keep [Topic_Proposal_PRJ301.md](file:///c:/Users/ThanhDuy/Documents/02_Study_Active/FPT/SEMSTER_4_SUMMER26/PRJ/PRJ301_3W_ASSIGNMENT/Topic_Proposal_PRJ301.md) updated at all times throughout the project lifecycle.
> 2. **UI/UX Reference Standards**: You MUST reference the Design Review Kit documented in [DESIGN_REVIEW_KIT.html](file:///c:/Users/ThanhDuy/Documents/02_Study_Active/FPT/SEMSTER_4_SUMMER26/PRJ/PRJ301_3W_ASSIGNMENT/DESIGN_REVIEW_KIT.html) and [DESIGN_SYSTEM_GUIDE.md](file:///c:/Users/ThanhDuy/Documents/02_Study_Active/FPT/SEMSTER_4_SUMMER26/PRJ/PRJ301_3W_ASSIGNMENT/DESIGN_SYSTEM_GUIDE.md) for all front-end UI/UX implementations.

---

## 📌 1. HARD TECHNICAL RULES (BẮT BUỘC TUÂN THỦ 100%)

Theo tài liệu đặc tả [Topic_Proposal_PRJ301.md](file:///c:/Users/ThanhDuy/Documents/02_Study_Active/FPT/SEMSTER_4_SUMMER26/PRJ/PRJ301_3W_ASSIGNMENT/Topic_Proposal_PRJ301.md):

1. **Kiến trúc Hệ thống**: 
   - Chuẩn **Java Web EE MVC-V2 3-Tier Enterprise** (NetBeans Ant Project).
   - Tách biệt rõ ràng 3 tầng: `Controller (Servlet - Thin)` $\rightarrow$ `Service (Fat Business Logic + Transaction)` $\rightarrow$ `DAO (Pure JDBC)`.
   - Tuyệt đối **KHÔNG dùng Spring Boot, Hibernate, JPA** (vi phạm quy chế PRJ301).

2. **CSDL & Connection Management**:
   - Hệ quản trị: **Microsoft SQL Server**.
   - Connection Pool: **HikariCP** (`HikariCP-5.x.x.jar`).
   - Quản lý giao dịch: **ThreadLocal Connection** kết hợp `TransactionFilter` tự động commit/rollback.
   - JDBC thuần: 100% dùng `PreparedStatement` và cú pháp `try-with-resources` để đóng Connection/ResultSet phòng ngừa rò rỉ bộ nhớ.
   - Chống Race Condition: Áp dụng `WITH (UPDLOCK, HOLDLOCK)` và ràng buộc CSDL (`UNIQUE (doctor_id, schedule_date, slot_time)`).

3. **Bảo mật & 4 Lớp Filters**:
   - `EncodingFilter`: Đảm bảo UTF-8 cho toàn bộ request/response.
   - `TransactionFilter`: Quản lý mở/đóng kết nối theo từng HTTP Request lifecycle.
   - `AuthenticationFilter`: Kiểm tra phiên làm việc (Session).
   - `AuthorizationFilter`: Phân quyền nghiêm ngặt 4 vai trò: `ADMIN`, `DOCTOR`, `PATIENT`, `RECEPTIONIST`.
   - Mật khẩu: 100% băm bằng **BCrypt** (`org.mindrot:jbcrypt`), không lưu plain text.

4. **Tích hợp Thanh toán Tự động**:
   - **SePay VietQR API**: Tự sinh mã QR động kèm số tiền và nội dung chuyển khoản cú pháp `CLINIC<appointment_id>` hoặc `SZ<id>`.
   - **Webhook SePay**: Tiếp nhận & xác thực tự động kèm kịch bản dự phòng (Manual Verify & Cash Payment Fallback).

---

## 🎨 2. UI/UX DESIGN SYSTEM & COMPONENT GUIDELINES

Mọi màn hình JSP và Stylesheet phải tuân thủ chuẩn Design Review Kit:

1. **Tokens & Bảng màu**:
   - Giao diện hài hòa, hiện đại với độ tương phản cao, hỗ trợ mobile responsive 100%.
   - Trạng thái trực quan: Badge bo tròn (pill), Soft Alerts (s-success, s-danger, s-warn, s-info).
   
2. **Hệ thống Component Core (P0)**:
   - **Buttons**: Dạng pill bo tròn, hiệu ứng hover nhấc nhẹ (`transform: translateY(-2px); box-shadow: ...`), nút icon tròn trong bảng (`.btn-icon`).
   - **Cards**: `.sz-card` với border mềm, shadow nhẹ, hover lift (`.hover-lift`).
   - **Form Controls**: Input không viền gắt, background mềm, focus ring rõ nét, toggle ẩn/hiện mật khẩu (`.input-eye`).
   - **Filter Pills**: Thanh lọc dạng tray bo tròn (`.pill-tray` + `.filter-pill`), active hiển thị rõ nét với badge đếm số lượng (`.sz-count`).
   - **Tables**: Header uppercase nền sáng, dòng hover mượt, cột tiền căn phải, tự động hỗ trợ responsive stack trên màn hình nhỏ.
   - **Date Selector & Time Slots**: Hàng pill chọn ngày (`.date-pill-row`), lưới khung giờ (`.slot-grid` / `.slot`) với các trạng thái rõ ràng: *Trống (Available)*, *Đang chọn (Selected)*, *Đã đặt (Booked)*, *Đã qua (Past)*, *Bảo trì/Đóng (Closed)*.
   - **Invoices**: Layout dạng giấy in chuyên nghiệp (`.invoice-paper`), dùng chung cho chế độ xem và in ấn (`window.print()`).
   - **Admin Layout**: Cấu trúc Shell hiện đại (`.adm-shell`, `.adm-side`, `.adm-main`), KPI cards gradient 6 màu SaaS, quản lý khung giờ trực quan dạng tile.

---

## 🔄 3. QUY TRÌNH THEO DÕI & CẬP NHẬT TÀI LIỆU (WORKFLOW)

Mỗi khi có sự thay đổi về:
1. Thiết kế bảng CSDL hoặc DAO/Service/Controller mới $\rightarrow$ Phải cập nhật ngay vào bảng đối chiếu và ma trận tính năng trong [Topic_Proposal_PRJ301.md](file:///c:/Users/ThanhDuy/Documents/02_Study_Active/FPT/SEMSTER_4_SUMMER26/PRJ/PRJ301_3W_ASSIGNMENT/Topic_Proposal_PRJ301.md).
2. Quy trình kiểm thử (JUnit 5, Postman) và tiến độ Checkpoint $\rightarrow$ Đồng bộ vào [Topic_Proposal_PRJ301.md](file:///c:/Users/ThanhDuy/Documents/02_Study_Active/FPT/SEMSTER_4_SUMMER26/PRJ/PRJ301_3W_ASSIGNMENT/Topic_Proposal_PRJ301.md) và [Agile_Development_Guide.md](file:///c:/Users/ThanhDuy/Documents/02_Study_Active/FPT/SEMSTER_4_SUMMER26/PRJ/PRJ301_3W_ASSIGNMENT/Agile_Development_Guide.md).
3. Thêm mới các trang JSP / View $\rightarrow$ Kế thừa đúng các component từ Design Review Kit.
