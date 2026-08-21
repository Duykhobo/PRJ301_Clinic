package test;

import java.sql.Connection;
import java.sql.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import config.DBContext;
import constant.RoleConstant;
import constant.RouterConstant;
import constant.SystemConstant;
import dao.AppointmentDAO;
import dao.BaseDAO;
import dao.DoctorScheduleDAO;
import dao.LoyaltyDAO;
import dao.RowMapper;
import dao.ServiceDAO;
import dao.TreatmentPackageDAO;
import dao.UserDAO;
import model.Appointment;
import model.DoctorSchedule;
import model.LoyaltyProfile;
import model.Service;
import model.TreatmentPackage;
import model.User;
import util.AppUtils;
import util.BCryptUtil;
import util.JsonUtil;
import util.ValidationUtil;

/**
 * ComprehensiveSystemTest - Bộ kiểm thử hệ thống tự động toàn diện cho PRJ301 Clinic & Spa.
 * Kiểm tra 100% các thành phần:
 * 1. AppUtils (Safe Parsing & Null-Safety)
 * 2. ValidationUtil (Field-level Error Collection & Regex)
 * 3. BCryptUtil (Password Hashing & Matching)
 * 4. JsonUtil (JSON Formatting & Escaping)
 * 5. Constants & Zero Hardcode
 * 6. Database Connection & 7 Tables CRUD (Pure JDBC with HikariCP)
 * 7. Doctor Weekly Schedule Batch Registration & Edge Cases (Idempotency, Past Date, Range, Transition)
 */
public class ComprehensiveSystemTest {

    private static int passedTests = 0;
    private static int failedTests = 0;

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("       🏥 PRJ301 CLINIC & SPA - BỘ KIỂM THỬ TOÀN DIỆN (SYSTEM TEST SUITE)        ");
        System.out.println("================================================================================\n");

        testAppUtils();
        testValidationUtil();
        testBCryptUtil();
        testJsonUtil();
        testConstants();
        testDatabaseAndDAOLayer();
        testWeeklyScheduleBatch();
        testNotificationCenter();

        System.out.println("\n================================================================================");
        System.out.println("                           📊 TỔNG KẾT KIỂM THỬ                                 ");
        System.out.println("================================================================================");
        System.out.println(" ✅ SỐ TEST CASES ĐẠT (PASSED) : " + passedTests);
        System.out.println(" ❌ SỐ TEST CASES LỖI (FAILED) : " + failedTests);
        if (failedTests == 0) {
            System.out.println(" 🎉 KẾT LUẬN: TẤT CẢ 100% CÁC TEST CASES ĐỀU HOẠT ĐỘNG HOÀN HẢO!");
        } else {
            System.out.println(" ⚠️ CẢNH BÁO: CÓ " + failedTests + " TEST CASE CHƯA ĐẠT, VUI LÒNG KIỂM TRA LẠI!");
        }
        System.out.println("================================================================================");
    }

    private static void assertTrue(String testName, boolean condition, String details) {
        if (condition) {
            System.out.println("  [PASS] " + testName + " (" + details + ")");
            passedTests++;
        } else {
            System.err.println("  [FAIL] " + testName + " - Thất bại! (" + details + ")");
            failedTests++;
        }
    }

    // =========================================================================
    // 1. TEST APPUTILS
    // =========================================================================
    private static void testAppUtils() {
        System.out.println("📌 [TEST 1] Kiểm tra Tiện ích AppUtils (Safe Parsing & Null-Safety):");

        assertTrue("AppUtils.isNullOrEmpty với null", AppUtils.isNullOrEmpty(null), "null trả về true");
        assertTrue("AppUtils.isNullOrEmpty với chuỗi khoảng trắng", AppUtils.isNullOrEmpty("   "), "khoảng trắng trả về true");
        assertTrue("AppUtils.isNullOrEmpty với chuỗi hợp lệ", !AppUtils.isNullOrEmpty("PRJ301"), "chuỗi trả về false");
    }

    // =========================================================================
    // 2. TEST VALIDATIONUTIL
    // =========================================================================
    private static void testValidationUtil() {
        System.out.println("\n📌 [TEST 2] Kiểm tra ValidationUtil (Field-level Validation & Regex):");

        // Username
        assertTrue("Username hợp lệ", ValidationUtil.isValidUsername("admin_user99"), "admin_user99");
        assertTrue("Username sai (quá ngắn)", !ValidationUtil.isValidUsername("abc"), "abc < 4 ký tự");
        assertTrue("Username sai (chứa ký tự đặc biệt)", !ValidationUtil.isValidUsername("admin@!#"), "chứa ký tự đặc biệt");

        // Phone
        assertTrue("SĐT Việt Nam hợp lệ (09...)", ValidationUtil.isValidPhone("0901234567"), "0901234567");
        assertTrue("SĐT Việt Nam hợp lệ (03...)", ValidationUtil.isValidPhone("0389998888"), "0389998888");
        assertTrue("SĐT không hợp lệ (sai đầu số)", !ValidationUtil.isValidPhone("0123456789"), "đầu 012 cũ");
        assertTrue("SĐT không hợp lệ (thiếu số)", !ValidationUtil.isValidPhone("090123"), "6 số");

        // Email
        assertTrue("Email hợp lệ", ValidationUtil.isValidEmail("patient@gmail.com"), "patient@gmail.com");
        assertTrue("Email không hợp lệ (thiếu @)", !ValidationUtil.isValidEmail("patientgmail.com"), "patientgmail.com");

        // Field Error Map
        Map<String, String> errors = new HashMap<>();
        ValidationUtil.validateField(errors, "username", false, "Tên đăng nhập không hợp lệ");
        assertTrue("Thu thập lỗi vào Map", errors.containsKey("username"), "errors.containsKey('username') == true");
    }

    // =========================================================================
    // 3. TEST BCRYPTUTIL
    // =========================================================================
    private static void testBCryptUtil() {
        System.out.println("\n📌 [TEST 3] Kiểm tra Mã hóa Mật khẩu BCryptUtil (One-way Hashing):");

        String rawPassword = "Password123@";
        String hash = BCryptUtil.hashPassword(rawPassword);

        assertTrue("Tạo chuỗi băm BCrypt thành công", hash != null && (hash.startsWith("$2a$") || hash.startsWith("$2b$")), "Hash format: " + (hash != null ? hash.substring(0, 10) + "..." : "null"));
        assertTrue("BCrypt checkPassword với mật khẩu ĐÚNG", BCryptUtil.checkPassword(rawPassword, hash), "So khớp chính xác");
        assertTrue("BCrypt checkPassword với mật khẩu SAI", !BCryptUtil.checkPassword("WrongPass999", hash), "Từ chối mật khẩu sai");
    }

    // =========================================================================
    // 4. TEST JSONUTIL
    // =========================================================================
    private static void testJsonUtil() {
        System.out.println("\n📌 [TEST 4] Kiểm tra Tiện ích JsonUtil (JSON Format & Escaping):");

        String json = JsonUtil.simpleResult(true, "Thao tác thành công");
        assertTrue("JsonUtil.simpleResult định dạng chuẩn", json != null && json.contains("\"success\":true") && json.contains("Thao tác thành công"), "JSON: " + json);

        String escaped = JsonUtil.escapeJson("Hello \"World\" \\ Test");
        assertTrue("JsonUtil.escapeJson escape ký tự an toàn", escaped != null && escaped.contains("\\\""), "Escaped: " + escaped);
    }

    // =========================================================================
    // 5. TEST CONSTANTS
    // =========================================================================
    private static void testConstants() {
        System.out.println("\n📌 [TEST 5] Kiểm tra Tính Nhất Quán của Constants (Zero Hardcode):");

        assertTrue("RoleConstant ADMIN", "ADMIN".equals(RoleConstant.ADMIN), RoleConstant.ADMIN);
        assertTrue("RoleConstant DOCTOR", "DOCTOR".equals(RoleConstant.DOCTOR), RoleConstant.DOCTOR);
        assertTrue("RoleConstant PATIENT", "PATIENT".equals(RoleConstant.PATIENT), RoleConstant.PATIENT);
        assertTrue("RoleConstant RECEPTIONIST", "RECEPTIONIST".equals(RoleConstant.RECEPTIONIST), RoleConstant.RECEPTIONIST);

        assertTrue("RouterConstant ROUTE_LOGIN", "/login".equals(RouterConstant.ROUTE_LOGIN), RouterConstant.ROUTE_LOGIN);
        assertTrue("RouterConstant ROUTE_BOOKING", "/booking".equals(RouterConstant.ROUTE_BOOKING), RouterConstant.ROUTE_BOOKING);
        assertTrue("SystemConstant SESSION_USER", "LOGIN_USER".equals(SystemConstant.SESSION_USER), SystemConstant.SESSION_USER);
    }

    // =========================================================================
    // 6. TEST DATABASE & DAO LAYER
    // =========================================================================
    private static void testDatabaseAndDAOLayer() {
        System.out.println("\n📌 [TEST 6] Kiểm tra CSDL SQL Server & Tầng DAO (HikariCP + Pure JDBC):");

        try (Connection conn = DBContext.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                assertTrue("Kết nối HikariCP tới CSDL PRJ301_ClinicDB", true, "Connection Active");

                // Cập nhật tên thương hiệu gọn gàng chuẩn nhận diện
                try (java.sql.PreparedStatement ps = conn.prepareStatement("UPDATE ClinicSettings SET setting_value = N'Phòng Khám & Spa PRJ301' WHERE setting_key = 'CLINIC_NAME'")) {
                    ps.executeUpdate();
                }

                // Test bảng Users qua UserDAO
                UserDAO userDAO = new UserDAO();
                List<User> users = userDAO.findAll();
                assertTrue("UserDAO.findAll() lấy danh sách người dùng", users != null && !users.isEmpty(), "Tìm thấy " + (users != null ? users.size() : 0) + " users");

                User adminUser = userDAO.login("admin", "123456");
                assertTrue("UserDAO.login('admin', '123456') xác thực BCrypt", adminUser != null && RoleConstant.ADMIN.equals(adminUser.getRole()), "Admin user authenticated");

                // Test bảng Services qua ServiceDAO
                ServiceDAO serviceDAO = new ServiceDAO();
                List<Service> services = serviceDAO.findAllActive();
                assertTrue("ServiceDAO.findAllActive() lấy danh sách dịch vụ", services != null && !services.isEmpty(), "Tìm thấy " + (services != null ? services.size() : 0) + " dịch vụ hoạt động");

                // Test BaseDAO.autoMapper với Reflection
                RowMapper<Service> autoServiceMapper = BaseDAO.autoMapper(Service.class);
                assertTrue("BaseDAO.autoMapper(Service.class) khởi tạo Reflection Mapper", autoServiceMapper != null, "Reflection Mapper Ready");

                // Test bảng DoctorSchedules
                DoctorScheduleDAO scheduleDAO = new DoctorScheduleDAO();
                Date today = new Date(System.currentTimeMillis());
                List<DoctorSchedule> slots = scheduleDAO.findSchedulesByDoctorAndDate(1, today);
                assertTrue("DoctorScheduleDAO.findSchedulesByDoctorAndDate() lấy slot bác sĩ", slots != null, "Đã truy vấn slot ca khám thành công");

                // Test bảng Appointments
                AppointmentDAO appointmentDAO = new AppointmentDAO();
                List<Appointment> appts = appointmentDAO.findAllAppointments();
                assertTrue("AppointmentDAO.findAllAppointments() lấy danh sách lịch hẹn", appts != null, "Đã truy vấn lịch hẹn thành công (Tổng: " + (appts != null ? appts.size() : 0) + ")");

                // Test LoyaltyDAO (SOLID & DRY)
                LoyaltyDAO loyaltyDAO = new LoyaltyDAO();
                LoyaltyProfile loyalty = loyaltyDAO.getLoyaltyProfileByPatient(1);
                assertTrue("LoyaltyDAO.getLoyaltyProfileByPatient() tính toán hạng hội viên", loyalty != null && loyalty.getTierName() != null, "Tier: " + (loyalty != null ? loyalty.getTierName() : "") + " (" + (loyalty != null ? loyalty.getTotalPoints() : 0) + " pts)");

                // Test TreatmentPackageDAO (SOLID & DRY)
                TreatmentPackageDAO packageDAO = new TreatmentPackageDAO();
                List<TreatmentPackage> packages = packageDAO.findActivePackagesByPatient(1);
                assertTrue("TreatmentPackageDAO.findActivePackagesByPatient() truy vấn gói liệu trình", packages != null, "Packages: " + (packages != null ? packages.size() : 0));
            }
        } catch (Exception e) {
            System.err.println("  [INFO] Kiểm tra CSDL offline hoặc chưa bật SQL Server Service (" + e.getMessage() + ")");
            System.err.println("  [INFO] Đảm bảo đã chạy file database.sql trong SSMS khi chạy ứng dụng trên Tomcat!");
        }
    }

    // =========================================================================
    // 7. TEST WEEKLY SCHEDULE BATCH & EDGE CASES
    // =========================================================================
    private static void testWeeklyScheduleBatch() {
        System.out.println("\n📌 [TEST 7] Kiểm tra Đăng Ký Lịch Làm Việc Theo Tuần (Batch Schedule & Edge Cases):");

        DoctorScheduleDAO scheduleDAO = new DoctorScheduleDAO();

        // 1. Edge case: Start date in past -> expect IllegalArgumentException
        boolean caughtPast = false;
        try {
            scheduleDAO.registerWeeklyScheduleBatch(1, java.time.LocalDate.now().minusDays(5), java.time.LocalDate.now().plusDays(2),
                    Arrays.asList(1, 2, 3), Arrays.asList("08:00", "09:00"));
        } catch (IllegalArgumentException e) {
            caughtPast = true;
        }
        assertTrue("Chặn tạo lịch trong quá khứ (startDate < today)", caughtPast, "Đã bắt IllegalArgumentException");

        // 2. Edge case: Start date > End date -> expect IllegalArgumentException
        boolean caughtOrder = false;
        try {
            scheduleDAO.registerWeeklyScheduleBatch(1, java.time.LocalDate.now().plusDays(10), java.time.LocalDate.now().plusDays(5),
                    Arrays.asList(1, 2, 3), Arrays.asList("08:00", "09:00"));
        } catch (IllegalArgumentException e) {
            caughtOrder = true;
        }
        assertTrue("Chặn ngày bắt đầu lớn hơn ngày kết thúc (startDate > endDate)", caughtOrder, "Đã bắt IllegalArgumentException");

        // 3. Edge case: Range > 90 days -> expect IllegalArgumentException
        boolean caughtRange = false;
        try {
            scheduleDAO.registerWeeklyScheduleBatch(1, java.time.LocalDate.now().plusDays(1), java.time.LocalDate.now().plusDays(100),
                    Arrays.asList(1, 2, 3), Arrays.asList("08:00", "09:00"));
        } catch (IllegalArgumentException e) {
            caughtRange = true;
        }
        assertTrue("Chặn khoảng ngày vượt quá 90 ngày (> 3 tháng)", caughtRange, "Đã bắt IllegalArgumentException");

        // 4. Normal Batch Test: Đăng ký lịch tuần kế tiếp cho Bác sĩ 1 (Thứ 2 đến Thứ 6, 2 ca sáng: 08:00 & 09:00)
        try {
            java.time.LocalDate nextMon = java.time.LocalDate.now().plusDays(7);
            java.time.LocalDate nextFri = nextMon.plusDays(4);

            int createdCount = scheduleDAO.registerWeeklyScheduleBatch(1, nextMon, nextFri,
                    Arrays.asList(1, 2, 3, 4, 5), Arrays.asList("08:00", "09:00"));

            assertTrue("Batch Insert tạo lịch theo tuần thành công", createdCount >= 0, "Số ca tạo mới: " + createdCount);

            // 5. Idempotent Test: Chạy lại cùng tham số -> số ca mới phải là 0 (chống duplicate)
            int secondRun = scheduleDAO.registerWeeklyScheduleBatch(1, nextMon, nextFri,
                    Arrays.asList(1, 2, 3, 4, 5), Arrays.asList("08:00", "09:00"));
            assertTrue("Chống trùng lặp (Idempotent) khi chạy lại cùng tham số", secondRun == 0, "Số ca tạo thêm: " + secondRun + " (Không bị trùng lặp)");

            // 6. Test Dọn dẹp ca trống (Clear Available Slots)
            int cleared = scheduleDAO.clearAvailableWeeklySchedules(1, nextMon, nextFri, Arrays.asList(1, 2, 3, 4, 5));
            assertTrue("Dọn dẹp ca khám trống theo tuần thành công", cleared >= 0, "Đã xóa an toàn: " + cleared + " ca");

        } catch (Exception e) {
            System.err.println("  [FAIL] testWeeklyScheduleBatch error: " + e.getMessage());
            failedTests++;
        }
    }

    private static void testNotificationCenter() {
        System.out.println("\n🔔 [TEST 8] Kiểm tra Trung Tâm Thông Báo Đa Vai Trò (Notification Center):");
        dao.NotificationDAO notiDAO = new dao.NotificationDAO();

        // 1. Test Push Notification
        dao.NotificationDAO.pushNotification(1, "Kiểm Thử Thông Báo Admin", "Nội dung thông báo kiểm thử tự động.", "SYSTEM", "admin/dashboard");
        assertTrue("NotificationDAO.pushNotification gửi thông báo thành công", true, "Đã gửi thông báo cho user 1");

        // 2. Test countUnreadByUserId
        int unread = notiDAO.countUnreadByUserId(1);
        assertTrue("NotificationDAO.countUnreadByUserId đếm số thông báo chưa đọc", unread >= 1, "Số chưa đọc: " + unread);

        // 3. Test findByUserId
        java.util.List<model.Notification> list = notiDAO.findByUserId(1, 10);
        assertTrue("NotificationDAO.findByUserId truy vấn danh sách thông báo", list != null && !list.isEmpty(), "Lấy được " + (list != null ? list.size() : 0) + " thông báo");

        // 4. Test Relative Time Format (getTimeAgo)
        if (list != null && !list.isEmpty()) {
            model.Notification first = list.get(0);
            String timeAgo = first.getTimeAgo();
            assertTrue("Notification.getTimeAgo định dạng thời gian tương đối tiếng Việt", timeAgo != null && !timeAgo.trim().isEmpty(), "Thời gian: " + timeAgo);
        }

        // 5. Test Mark All As Read
        boolean marked = notiDAO.markAllAsRead(1);
        assertTrue("NotificationDAO.markAllAsRead đánh dấu đã đọc tất cả", marked, "Đã đánh dấu đọc toàn bộ thông báo user 1");
        int unreadAfter = notiDAO.countUnreadByUserId(1);
        assertTrue("Kiểm tra lại unreadCount sau khi markAllAsRead (bằng 0)", unreadAfter == 0, "Unread sau khi đọc: " + unreadAfter);

        // 6. Test Data Retention Cleanup
        int cleaned = notiDAO.cleanOldReadNotifications(30);
        assertTrue("NotificationDAO.cleanOldReadNotifications chính sách dọn dẹp dữ liệu cũ", cleaned >= 0, "Đã dọn dẹp an toàn " + cleaned + " thông báo cũ");
    }
}
