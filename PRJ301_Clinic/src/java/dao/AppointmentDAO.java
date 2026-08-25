package dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import constant.SystemConstant;
import exception.SlotAlreadyBookedException;
import model.Appointment;
import model.RevenueReport;

/**
 * Lớp AppointmentDAO quản lý Đặt lịch hẹn và Giao dịch Thanh toán.
 */
public class AppointmentDAO extends BaseDAO<Appointment> {

    private static final Logger LOGGER = Logger.getLogger(AppointmentDAO.class.getName());

    // =========================================================================
    // 🧱 1. ROWMAPPER (TỰ ĐỘNG BẰNG REFLECTION CHUẨN DRY & SOLID)
    // =========================================================================
    private final RowMapper<Appointment> mapper = autoMapper(Appointment.class);

    private static final String BASE_SELECT = "SELECT a.*, u_pat.fullname AS patient_name, u_pat.phone AS patient_phone, "
            + "u_doc.fullname AS doctor_name, s.service_name "
            + "FROM Appointments a "
            + "JOIN Users u_pat ON a.patient_id = u_pat.id "
            + "JOIN DoctorProfiles dp ON a.doctor_id = dp.id "
            + "JOIN Users u_doc ON dp.user_id = u_doc.id "
            + "JOIN Services s ON a.service_id = s.id ";

    /**
     * Tạo lịch hẹn mới (Atomic Booking). Hàm này được quản lý tự động bởi
     * TransactionFilter. Không cần try-catch rollback thủ công.
     */
    public boolean createBookingAtomic(Appointment app) throws SlotAlreadyBookedException {

        // 1. Kiểm tra Slot có rảnh không và khóa Slot (WITH UPDLOCK)
        String lockSql = "SELECT ds.start_time, "
                + "CASE "
                + "  WHEN ds.work_date < CAST(GETDATE() AS DATE) THEN 0 "
                + "  WHEN ds.work_date = CAST(GETDATE() AS DATE) AND ds.start_time <= CAST(GETDATE() AS TIME) THEN 0 "
                + "  WHEN EXISTS ( "
                + "      SELECT 1 FROM Appointments a "
                + "      WHERE a.schedule_id = ds.id AND a.status IN ('PENDING', 'CONFIRMED', 'COMPLETED') "
                + "  ) THEN 0 "
                + "  ELSE ds.is_available "
                + "END AS is_slot_available "
                + "FROM DoctorSchedules ds WITH (UPDLOCK, HOLDLOCK) WHERE ds.id = ?";

        Appointment lockedAppt = queryOne(lockSql, rs -> {
            boolean isSlotAvailable = rs.getInt("is_slot_available") == 1;
            if (!isSlotAvailable) {
                return null;
            }

            Appointment tempAppt = new Appointment();
            tempAppt.setStartTime(rs.getTime("start_time"));
            return tempAppt;
        }, app.getScheduleId());

        if (lockedAppt == null || lockedAppt.getStartTime() == null) {
            throw new SlotAlreadyBookedException(
                    "Khung giờ này vừa được đăng ký bởi bệnh nhân khác. Vui lòng chọn ca rảnh khác!");
        }

        if (app.getStartTime() == null) {
            app.setStartTime(lockedAppt.getStartTime());
        }

        try {
            // 2. Thực hiện Insert Lịch Hẹn
            String insertSql = "INSERT INTO Appointments (patient_id, doctor_id, service_id, schedule_id, appointment_date, start_time, total_price, status, payment_status, payment_method, notes) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            int newAppId = executeInsertAndGetGeneratedKey(insertSql,
                    app.getPatientId(),
                    app.getDoctorId(),
                    app.getServiceId(),
                    app.getScheduleId(),
                    app.getAppointmentDate(),
                    app.getStartTime(),
                    app.getTotalPrice(),
                    app.getStatus() != null ? app.getStatus() : SystemConstant.STATUS_PENDING,
                    app.getPaymentStatus() != null ? app.getPaymentStatus() : SystemConstant.PAYMENT_UNPAID,
                    app.getPaymentMethod() != null ? app.getPaymentMethod() : SystemConstant.METHOD_SEPAY_QR,
                    app.getNotes());

            if (newAppId <= 0) {
                return false;
            }

            app.setId(newAppId);

            // 3. Cập nhật mã Payment Content (Dành cho SePay)
            String paymentContent = "CLN" + newAppId;
            app.setPaymentContent(paymentContent);

            String contentSql = "UPDATE Appointments SET payment_content = ? WHERE id = ?";
            executeUpdate(contentSql, paymentContent, newAppId);

            // 4. Cập nhật khóa Slot trong Database
            String slotSql = "UPDATE DoctorSchedules SET is_available = 0 WHERE id = ?";
            executeUpdate(slotSql, app.getScheduleId());

            // 5. Đẩy thông báo tức thì tới Bệnh nhân & Lễ tân
            NotificationDAO.pushNotification(app.getPatientId(), "Đặt lịch khám thành công",
                    "Lịch hẹn ca #" + newAppId + " (" + app.getAppointmentDate() + ") đã được tạo thành công.",
                    "APPOINTMENT", "history");
            NotificationDAO.pushNotificationToRole("RECEPTIONIST", "Lịch hẹn mới #" + newAppId,
                    "Bệnh nhân vừa đặt lịch khám mới cho ngày " + app.getAppointmentDate() + ".",
                    "APPOINTMENT", "receptionist/dashboard");

            return true;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi DB khi tạo booking, kích hoạt Rollback qua TransactionFilter", e);
            throw new RuntimeException("Lỗi thao tác cơ sở dữ liệu khi đặt lịch", e);
        }
    }

    public Appointment findById(int id) {
        String sql = BASE_SELECT
                + "WHERE a.id = ?";
        return queryOne(sql, mapper, id);
    }

    public List<Appointment> findByPatientId(int patientId) {
        String sql = BASE_SELECT
                + "WHERE a.patient_id = ? "
                + "ORDER BY a.created_at DESC";
        return queryList(sql, mapper, patientId);
    }

    public List<Appointment> findPatientAppointmentsPaginated(int patientId, int offset, int limit) {
        String sql = BASE_SELECT
                + "WHERE a.patient_id = ? "
                + "ORDER BY a.created_at DESC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        return queryList(sql, mapper, patientId, offset, limit);
    }

    public int countPatientAppointments(int patientId) {
        String sql = "SELECT COUNT(*) FROM Appointments WHERE patient_id = ?";
        return queryCount(sql, patientId);
    }

    public boolean updatePaymentSuccess(int appointmentId, String transactionCode) {
        String sql = "UPDATE Appointments SET payment_status = ?, transaction_code = ? WHERE id = ?";
        return executeUpdate(sql, SystemConstant.PAYMENT_PAID, transactionCode, appointmentId);
    }

    public List<Appointment> findAppointmentsByDoctorUserAndDate(int doctorUserId, String date) {
        String sql = BASE_SELECT
                + "WHERE dp.user_id = ? AND a.appointment_date = ? "
                + "ORDER BY a.start_time ASC";
        return queryList(sql, mapper, doctorUserId, date);
    }

    public List<Appointment> findAppointmentsByDoctorUserAndDatePaginated(int doctorUserId, String date, int offset,
            int limit) {
        String sql = BASE_SELECT
                + "WHERE dp.user_id = ? AND a.appointment_date = ? "
                + "ORDER BY a.start_time ASC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        return queryList(sql, mapper, doctorUserId, date, offset, limit);
    }

    public int countAppointmentsByDoctorUserAndDate(int doctorUserId, String date) {
        String sql = "SELECT COUNT(*) FROM Appointments a JOIN DoctorProfiles dp ON a.doctor_id = dp.id WHERE dp.user_id = ? AND a.appointment_date = ?";
        return queryCount(sql, doctorUserId, date);
    }

    public List<Appointment> findAppointmentsByDoctorUserPaginated(int doctorUserId, int offset, int limit) {
        String sql = BASE_SELECT
                + "WHERE dp.user_id = ? "
                + "ORDER BY a.appointment_date DESC, a.start_time ASC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        return queryList(sql, mapper, doctorUserId, offset, limit);
    }

    public List<Appointment> findAppointmentsByDoctorUser(int doctorUserId) {
        String sql = BASE_SELECT
                + "WHERE dp.user_id = ? "
                + "ORDER BY a.appointment_date DESC, a.start_time ASC";
        return queryList(sql, mapper, doctorUserId);
    }

    public int countAppointmentsByDoctorUser(int doctorUserId) {
        String sql = "SELECT COUNT(*) FROM Appointments a JOIN DoctorProfiles dp ON a.doctor_id = dp.id WHERE dp.user_id = ?";
        return queryCount(sql, doctorUserId);
    }

    public String findNextOrCurrentAppointmentDateForDoctor(int doctorUserId) {
        String sql = "SELECT TOP 1 a.appointment_date FROM Appointments a "
                + "JOIN DoctorProfiles dp ON a.doctor_id = dp.id "
                + "WHERE dp.user_id = ? AND a.appointment_date >= CAST(GETDATE() AS DATE) "
                + "ORDER BY a.appointment_date ASC";

        Appointment appt = queryOne(sql, rs -> {
            Appointment a = new Appointment();
            a.setAppointmentDate(rs.getDate(1));
            return a;
        }, doctorUserId);

        return (appt != null && appt.getAppointmentDate() != null)
                ? appt.getAppointmentDate().toString()
                : LocalDate.now().toString();
    }

    public List<Appointment> findAllAppointmentsByDate(String date) {
        String sql = BASE_SELECT
                + "WHERE a.appointment_date = ? "
                + "ORDER BY a.start_time ASC";
        return queryList(sql, mapper, date);
    }

    public List<Appointment> findAllAppointmentsByDatePaginated(String date, int offset, int limit) {
        String sql = BASE_SELECT
                + "WHERE a.appointment_date = ? "
                + "ORDER BY a.start_time ASC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        return queryList(sql, mapper, date, offset, limit);
    }

    public int countAllAppointmentsByDate(String date) {
        String sql = "SELECT COUNT(*) FROM Appointments WHERE appointment_date = ?";
        return queryCount(sql, date);
    }

    public List<Appointment> findAllAppointmentsPaginated(int offset, int limit) {
        String sql = BASE_SELECT
                + "ORDER BY a.appointment_date DESC, a.start_time ASC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        return queryList(sql, mapper, offset, limit);
    }

    public List<Appointment> findAllAppointments() {
        String sql = BASE_SELECT
                + "ORDER BY a.appointment_date DESC, a.start_time ASC";
        return queryList(sql, mapper);
    }

    public int countAllAppointments() {
        String sql = "SELECT COUNT(*) FROM Appointments";
        return queryCount(sql);
    }

    public boolean updateStatus(int appointmentId, String newStatus) {
        String sql = "UPDATE Appointments SET status = ? WHERE id = ?";
        boolean updated = executeUpdate(sql, newStatus, appointmentId);
        if (updated && SystemConstant.STATUS_CANCELLED.equalsIgnoreCase(newStatus)) {
            String unlockSlotSql = "UPDATE DoctorSchedules SET is_available = 1 WHERE id = (SELECT schedule_id FROM Appointments WHERE id = ?)";
            executeUpdate(unlockSlotSql, appointmentId);
        }
        return updated;
    }

    public boolean updatePayment(int appointmentId, String paymentStatus, String paymentMethod) {
        String sql = "UPDATE Appointments SET payment_status = ?, payment_method = ? WHERE id = ?";
        boolean ok = executeUpdate(sql, paymentStatus, paymentMethod, appointmentId);
        if (ok && SystemConstant.PAYMENT_PAID.equalsIgnoreCase(paymentStatus)) {
            Appointment app = findById(appointmentId);
            if (app != null) {
                NotificationDAO.pushNotification(app.getPatientId(), "Thanh toán thành công",
                        "Hóa đơn ca khám #" + appointmentId + " đã được xác nhận thanh toán thành công (" + (paymentMethod != null ? paymentMethod : "VietQR") + ").",
                        "PAYMENT", "history");
                NotificationDAO.pushNotificationToRole("ADMIN", "Doanh thu mới #" + appointmentId,
                        "Hệ thống vừa ghi nhận thanh toán thành công cho ca #" + appointmentId + ".",
                        "PAYMENT", "admin/dashboard");
            }
        }
        return ok;
    }

    public RevenueReport getRevenueReport(Date startDate, Date endDate) {
        String sql = "SELECT "
                + "COUNT(id) AS total_appointments, "
                + "COALESCE(SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END), 0) AS completed_appointments, "
                + "COALESCE(SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END), 0) AS cancelled_appointments, "
                + "COALESCE(SUM(CASE WHEN payment_status = 'PAID' THEN total_price ELSE 0 END), 0) AS total_revenue_paid, "
                + "COALESCE(SUM(CASE WHEN (payment_method = 'SEPAY_QR' OR payment_method IS NULL) AND payment_status = 'PAID' THEN total_price ELSE 0 END), 0) AS sepay_revenue, "
                + "COALESCE(SUM(CASE WHEN payment_method = 'CASH' AND payment_status = 'PAID' THEN total_price ELSE 0 END), 0) AS cash_revenue "
                + "FROM Appointments "
                + "WHERE appointment_date BETWEEN ? AND ?";
        
        RevenueReport report = queryOne(sql, rs -> new RevenueReport(
                rs.getInt("total_appointments"),
                rs.getInt("completed_appointments"),
                rs.getInt("cancelled_appointments"),
                rs.getBigDecimal("total_revenue_paid"),
                rs.getBigDecimal("sepay_revenue"),
                rs.getBigDecimal("cash_revenue")), startDate, endDate);

        return report != null ? report : new RevenueReport();
    }
}
