package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import config.DBContext;
import constant.SystemConstant;
import exception.SlotAlreadyBookedException;
import model.Appointment;
import model.RevenueReport;

/**
 * Lớp AppointmentDAO quản lý Đặt lịch hẹn và Giao dịch Thanh toán. ⚡ TÍCH HỢP
 * KHÓA NGUYÊN TỬ WITH (UPDLOCK) CHỐNG RACE CONDITION (ĐIỂM CỘNG 10/10).
 */
public class AppointmentDAO extends BaseDAO<Appointment> {

    private static final Logger LOGGER = Logger.getLogger(AppointmentDAO.class.getName());

    // =========================================================================
    // TODO [BƯỚC 6a — DRY]: Khai báo hằng số BASE_SELECT (SQL JOIN tái sử dụng)
    // =========================================================================
    // Vấn đề: Đoạn SQL JOIN này xuất hiện 8 lần trong file này:
    //   "SELECT a.*, u_pat.fullname AS patient_name, u_pat.phone AS patient_phone, "
    //   + "u_doc.fullname AS doctor_name, s.service_name "
    //   + "FROM Appointments a "
    //   + "JOIN Users u_pat ON a.patient_id = u_pat.id "
    //   + "JOIN DoctorProfiles dp ON a.doctor_id = dp.id "
    //   + "JOIN Users u_doc ON dp.user_id = u_doc.id "
    //   + "JOIN Services s ON a.service_id = s.id "
    //
    // ✔ Giải pháp: Đưa vào hằng số private static final String BASE_SELECT = "...";
    // ✔ Sau đó các method chỉ cần: String sql = BASE_SELECT + "WHERE a.patient_id = ?";
    //
    // ❓ Tại sao là private và static và final?
    //    private = chỉ dùng trong AppointmentDAO
    //    static  = thuộc về class, không phụ thuộc instance
    //    final   = không được đổi giá trị sau khi khai báo (hằng số thất sự)
    // =========================================================================
    // private static final String BASE_SELECT = ???;


    protected Appointment mapResultSetToAppointment(ResultSet rs) throws SQLException {
        Appointment app = new Appointment();
        app.setId(rs.getInt("id"));
        app.setPatientId(rs.getInt("patient_id"));
        app.setDoctorId(rs.getInt("doctor_id"));
        app.setServiceId(rs.getInt("service_id"));
        app.setScheduleId(rs.getInt("schedule_id"));
        app.setAppointmentDate(rs.getDate("appointment_date"));
        app.setStartTime(rs.getTime("start_time"));
        app.setTotalPrice(rs.getBigDecimal("total_price"));
        app.setStatus(rs.getString("status"));
        app.setPaymentStatus(rs.getString("payment_status"));
        app.setPaymentMethod(rs.getString("payment_method"));
        app.setPaymentContent(rs.getString("payment_content"));
        app.setTransactionCode(rs.getString("transaction_code"));
        app.setNotes(rs.getString("notes"));
        app.setCreatedAt(rs.getTimestamp("created_at"));

        try {
            app.setPatientName(rs.getString("patient_name"));
        } catch (SQLException ignored) {
        }
        try {
            app.setPatientPhone(rs.getString("patient_phone"));
        } catch (SQLException ignored) {
        }
        try {
            app.setDoctorName(rs.getString("doctor_name"));
        } catch (SQLException ignored) {
        }
        try {
            app.setServiceName(rs.getString("service_name"));
        } catch (SQLException ignored) {
        }

        return app;
    }

    public boolean createBookingAtomic(Appointment app) {
        // =========================================================================
        // TODO: BÀI TẬP CỘT MỐC 1 & 2 - TRIỂN KHAI ĐẶT LỊCH NGUYÊN TỬ (ATOMIC BOOKING)
        // =========================================================================
        // Bước 1: Sử dụng executeTransaction(conn -> { ... }) để mở Transaction chung
        // connection.
        // Bước 2: Viết câu SQL Lock Hint: "SELECT is_available, start_time FROM
        // DoctorSchedules WITH (UPDLOCK, HOLDLOCK) WHERE id = ?"
        // Bước 3: Thực thi queryOne kiểm tra slot. Nếu null hoặc !is_available -> throw
        // new SlotAlreadyBookedException(...)
        // Bước 4: Thực thi executeInsertAndGetGeneratedKey để INSERT vào bảng
        // Appointments.
        // Bước 5: Cập nhật mã payment_content dạng "CLN" + newAppId bằng executeUpdate.
        // Bước 6: Cập nhật is_available = 0 ở bảng DoctorSchedules bằng executeUpdate.
        // Bước 7: Trả về true khi thành công.
        // =========================================================================

        try {
            return executeTransaction(conn -> {
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
                Time slotStarTime = queryOne(conn, lockSql, rs -> {
                    boolean isSlotAvailable = rs.getInt("is_slot_available") == 1;
                    if (!isSlotAvailable)
                        return null;
                    return rs.getTime("start_time");
                }, app.getScheduleId());
                if (slotStarTime == null)
                    throw new SlotAlreadyBookedException(
                            "Khung giờ này vừa được đăng ký bởi bệnh nhân khác. Vui lòng chọn ca rảnh khác!");
                if (app.getStartTime() == null)
                    app.setStartTime(slotStarTime);
                String insertSql = "INSERT INTO Appointments (patient_id, doctor_id, service_id, schedule_id, appointment_date, start_time, total_price, status, payment_status, payment_method, notes)"
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                int newAppId = executeInsertAndGetGeneratedKey(conn, insertSql, app.getPatientId(),

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
                app.setId(newAppId);
                String paymentContent = "CLN" + newAppId;
                app.setPaymentContent(paymentContent);
                String contentSql = "UPDATE Appointments SET payment_content = ? WHERE id = ?";
                executeUpdate(conn, contentSql, paymentContent, newAppId);
                String slotSql = "UPDATE DoctorSchedules SET is_available = 0 WHERE id = ?";
                executeUpdate(conn, slotSql, app.getScheduleId());
                return true;
            });
        } catch (SlotAlreadyBookedException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tạo booking atomic", e);
            return false;
        }
    }

    public Appointment findById(int id) {
        String sql = "SELECT a.*, u.fullname AS patient_name, u.phone AS patient_phone, doc_u.fullname AS doctor_name, s.service_name "
                + "FROM Appointments a "
                + "JOIN Users u ON a.patient_id = u.id "
                + "JOIN DoctorProfiles d ON a.doctor_id = d.id "
                + "JOIN Users doc_u ON d.user_id = doc_u.id "
                + "JOIN Services s ON a.service_id = s.id "
                + "WHERE a.id = ?";
        return queryOne(sql, this::mapResultSetToAppointment, id);
    }

    public List<Appointment> findByPatientId(int patientId) {
        String sql = "SELECT a.*, u.fullname AS patient_name, u.phone AS patient_phone, doc_u.fullname AS doctor_name, s.service_name "
                + "FROM Appointments a "
                + "JOIN Users u ON a.patient_id = u.id "
                + "JOIN DoctorProfiles d ON a.doctor_id = d.id "
                + "JOIN Users doc_u ON d.user_id = doc_u.id "
                + "JOIN Services s ON a.service_id = s.id "
                + "WHERE a.patient_id = ? "
                + "ORDER BY a.created_at DESC";
        return queryList(sql, this::mapResultSetToAppointment, patientId);
    }

    public List<Appointment> findPatientAppointmentsPaginated(int patientId, int offset, int limit) {
        String sql = "SELECT a.*, "
                + "u.fullname AS patient_name, "
                + "u.phone AS patient_phone, "
                + "doc_u.fullname AS doctor_name, "
                + "s.service_name "
                + "FROM Appointments a "
                + "JOIN Users u ON a.patient_id = u.id "
                + "JOIN DoctorProfiles d ON a.doctor_id = d.id "
                + "JOIN Users doc_u ON d.user_id = doc_u.id "
                + "JOIN Services s ON a.service_id = s.id "
                + "WHERE a.patient_id = ? "
                + "ORDER BY a.created_at DESC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        return queryList(sql, this::mapResultSetToAppointment, patientId, offset, limit);
    }

    // =========================================================================
    // TODO [BƯỚC 6b — queryCount]: Refactor các method count() dưới đây
    // =========================================================================
    // Hiện tại mỗi hàm count viết ~8 dòng JDBC thủ công. Sau khi implement queryCount()
    // trong BaseDAO, hãy refactor tất cả về 1 dòng:
    //
    //   Trước:                              Sau:
    //   public int countXxx() {           public int countXxx() {
    //       String sql = "...";               return queryCount("...", params);
    //       try (Connection conn = ...) {  }
    //           ...8 dòng...
    //       }
    //       return 0;
    //   }
    // =========================================================================
    public int countPatientAppointments(int patientId) {
        String sql = "SELECT COUNT(*) FROM Appointments WHERE patient_id = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đếm số lượng cuộc hẹn bệnh nhân", e);
        }
        return 0;
    }

    public boolean updatePaymentSuccess(int appointmentId, String transactionCode) {
        String sql = "UPDATE Appointments SET payment_status = '" + SystemConstant.PAYMENT_PAID + "', status = '"
                + SystemConstant.STATUS_CONFIRMED + "', transaction_code = ? WHERE id = ?";
        return executeUpdate(sql, transactionCode, appointmentId);
    }

    public List<Appointment> findAppointmentsByDoctorUserAndDate(int doctorUserId, String date) {
        String sql = "SELECT a.*, "
                + "u_pat.fullname AS patient_name, u_pat.phone AS patient_phone, "
                + "u_doc.fullname AS doctor_name, "
                + "s.service_name "
                + "FROM Appointments a "
                + "JOIN Users u_pat ON a.patient_id = u_pat.id "
                + "JOIN DoctorProfiles dp ON a.doctor_id = dp.id "
                + "JOIN Users u_doc ON dp.user_id = u_doc.id "
                + "JOIN Services s ON a.service_id = s.id "
                + "WHERE dp.user_id = ? AND a.appointment_date = ? "
                + "ORDER BY a.start_time ASC";
        return queryList(sql, this::mapResultSetToAppointment, doctorUserId, date);
    }

    public List<Appointment> findAppointmentsByDoctorUserAndDatePaginated(int doctorUserId, String date, int offset,
            int limit) {
        String sql = "SELECT a.*, "
                + "u_pat.fullname AS patient_name, u_pat.phone AS patient_phone, "
                + "u_doc.fullname AS doctor_name, "
                + "s.service_name "
                + "FROM Appointments a "
                + "JOIN Users u_pat ON a.patient_id = u_pat.id "
                + "JOIN DoctorProfiles dp ON a.doctor_id = dp.id "
                + "JOIN Users u_doc ON dp.user_id = u_doc.id "
                + "JOIN Services s ON a.service_id = s.id "
                + "WHERE dp.user_id = ? AND a.appointment_date = ? "
                + "ORDER BY a.start_time ASC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        return queryList(sql, this::mapResultSetToAppointment, doctorUserId, date, offset, limit);
    }

    public int countAppointmentsByDoctorUserAndDate(int doctorUserId, String date) {
        String sql = "SELECT COUNT(*) FROM Appointments a JOIN DoctorProfiles dp ON a.doctor_id = dp.id WHERE dp.user_id = ? AND a.appointment_date = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorUserId);
            ps.setObject(2, date);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        } catch (SQLException ignored) {
        }
        return 0;
    }

    public List<Appointment> findAppointmentsByDoctorUserPaginated(int doctorUserId, int offset, int limit) {
        String sql = "SELECT a.*, "
                + "u_pat.fullname AS patient_name, u_pat.phone AS patient_phone, "
                + "u_doc.fullname AS doctor_name, "
                + "s.service_name "
                + "FROM Appointments a "
                + "JOIN Users u_pat ON a.patient_id = u_pat.id "
                + "JOIN DoctorProfiles dp ON a.doctor_id = dp.id "
                + "JOIN Users u_doc ON dp.user_id = u_doc.id "
                + "JOIN Services s ON a.service_id = s.id "
                + "WHERE dp.user_id = ? "
                + "ORDER BY a.appointment_date DESC, a.start_time ASC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        return queryList(sql, this::mapResultSetToAppointment, doctorUserId, offset, limit);
    }

    public List<Appointment> findAppointmentsByDoctorUser(int doctorUserId) {
        String sql = "SELECT a.*, "
                + "u_pat.fullname AS patient_name, u_pat.phone AS patient_phone, "
                + "u_doc.fullname AS doctor_name, "
                + "s.service_name "
                + "FROM Appointments a "
                + "JOIN Users u_pat ON a.patient_id = u_pat.id "
                + "JOIN DoctorProfiles dp ON a.doctor_id = dp.id "
                + "JOIN Users u_doc ON dp.user_id = u_doc.id "
                + "JOIN Services s ON a.service_id = s.id "
                + "WHERE dp.user_id = ? "
                + "ORDER BY a.appointment_date DESC, a.start_time ASC";
        return queryList(sql, this::mapResultSetToAppointment, doctorUserId);
    }

    public int countAppointmentsByDoctorUser(int doctorUserId) {
        String sql = "SELECT COUNT(*) FROM Appointments a JOIN DoctorProfiles dp ON a.doctor_id = dp.id WHERE dp.user_id = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorUserId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        } catch (SQLException ignored) {
        }
        return 0;
    }

    public String findNextOrCurrentAppointmentDateForDoctor(int doctorUserId) {
        String sql = "SELECT TOP 1 a.appointment_date FROM Appointments a "
                + "JOIN DoctorProfiles dp ON a.doctor_id = dp.id "
                + "WHERE dp.user_id = ? AND a.appointment_date >= CAST(GETDATE() AS DATE) "
                + "ORDER BY a.appointment_date ASC";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorUserId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    java.sql.Date d = rs.getDate(1);
                    if (d != null)
                        return d.toString();
                }
            }
        } catch (SQLException ignored) {
        }
        return LocalDate.now().toString();
    }

    public List<Appointment> findAllAppointmentsByDate(String date) {
        String sql = "SELECT a.*, "
                + "u_pat.fullname AS patient_name, u_pat.phone AS patient_phone, "
                + "u_doc.fullname AS doctor_name, "
                + "s.service_name "
                + "FROM Appointments a "
                + "JOIN Users u_pat ON a.patient_id = u_pat.id "
                + "JOIN DoctorProfiles dp ON a.doctor_id = dp.id "
                + "JOIN Users u_doc ON dp.user_id = u_doc.id "
                + "JOIN Services s ON a.service_id = s.id "
                + "WHERE a.appointment_date = ? "
                + "ORDER BY a.start_time ASC";
        return queryList(sql, this::mapResultSetToAppointment, date);
    }

    public List<Appointment> findAllAppointmentsByDatePaginated(String date, int offset, int limit) {
        String sql = "SELECT a.*, "
                + "u_pat.fullname AS patient_name, u_pat.phone AS patient_phone, "
                + "u_doc.fullname AS doctor_name, "
                + "s.service_name "
                + "FROM Appointments a "
                + "JOIN Users u_pat ON a.patient_id = u_pat.id "
                + "JOIN DoctorProfiles dp ON a.doctor_id = dp.id "
                + "JOIN Users u_doc ON dp.user_id = u_doc.id "
                + "JOIN Services s ON a.service_id = s.id "
                + "WHERE a.appointment_date = ? "
                + "ORDER BY a.start_time ASC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        return queryList(sql, this::mapResultSetToAppointment, date, offset, limit);
    }

    public int countAllAppointmentsByDate(String date) {
        String sql = "SELECT COUNT(*) FROM Appointments WHERE appointment_date = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, date);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        } catch (SQLException ignored) {
        }
        return 0;
    }

    public List<Appointment> findAllAppointmentsPaginated(int offset, int limit) {
        String sql = "SELECT a.*, "
                + "u_pat.fullname AS patient_name, u_pat.phone AS patient_phone, "
                + "u_doc.fullname AS doctor_name, "
                + "s.service_name "
                + "FROM Appointments a "
                + "JOIN Users u_pat ON a.patient_id = u_pat.id "
                + "JOIN DoctorProfiles dp ON a.doctor_id = dp.id "
                + "JOIN Users u_doc ON dp.user_id = u_doc.id "
                + "JOIN Services s ON a.service_id = s.id "
                + "ORDER BY a.appointment_date DESC, a.start_time ASC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        return queryList(sql, this::mapResultSetToAppointment, offset, limit);
    }

    public List<Appointment> findAllAppointments() {
        String sql = "SELECT a.*, "
                + "u_pat.fullname AS patient_name, u_pat.phone AS patient_phone, "
                + "u_doc.fullname AS doctor_name, "
                + "s.service_name "
                + "FROM Appointments a "
                + "JOIN Users u_pat ON a.patient_id = u_pat.id "
                + "JOIN DoctorProfiles dp ON a.doctor_id = dp.id "
                + "JOIN Users u_doc ON dp.user_id = u_doc.id "
                + "JOIN Services s ON a.service_id = s.id "
                + "ORDER BY a.appointment_date DESC, a.start_time ASC";
        return queryList(sql, this::mapResultSetToAppointment);
    }

    public int countAllAppointments() {
        String sql = "SELECT COUNT(*) FROM Appointments";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        } catch (SQLException ignored) {
        }
        return 0;
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
        return executeUpdate(sql, paymentStatus, paymentMethod, appointmentId);
    }

    public RevenueReport getRevenueReport(Date startDate, Date endDate) {
        String sql = "EXEC dbo.sp_GetClinicRevenueReport ?, ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, startDate);
            ps.setDate(2, endDate);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new RevenueReport(
                            rs.getInt("total_appointments"),
                            rs.getInt("completed_appointments"),
                            rs.getInt("cancelled_appointments"),
                            rs.getBigDecimal("total_revenue_paid"),
                            rs.getBigDecimal("sepay_revenue"),
                            rs.getBigDecimal("cash_revenue"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi gọi Stored Proc sp_GetClinicRevenueReport", e);
        }
        return new RevenueReport();
    }
}
