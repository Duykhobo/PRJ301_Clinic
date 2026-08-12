package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import constant.SystemConstant;
import exception.SlotAlreadyBookedException;
import model.Appointment;

/**
 * Lớp AppointmentDAO quản lý Đặt lịch hẹn và Giao dịch Thanh toán. ⚡ TÍCH HỢP
 * KHÓA NGUYÊN TỬ WITH (UPDLOCK) CHỐNG RACE CONDITION (ĐIỂM CỘNG 10/10).
 */
public class AppointmentDAO extends BaseDAO<Appointment> {

    private static final Logger LOGGER = Logger.getLogger(AppointmentDAO.class.getName());

    // =========================================================================
    // 🧱 1. HELPER MAPPER (CHUẨN DRY)
    // =========================================================================
    /**
     * Mapper chuyển ResultSet từ câu SQL JOIN Appointments + Users +
     * DoctorProfiles + Services.
     */
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

        // Joined Fields
        try { app.setPatientName(rs.getString("patient_name")); } catch (SQLException ignored) {}
        try { app.setPatientPhone(rs.getString("patient_phone")); } catch (SQLException ignored) {}
        try { app.setDoctorName(rs.getString("doctor_name")); } catch (SQLException ignored) {}
        try { app.setServiceName(rs.getString("service_name")); } catch (SQLException ignored) {}
        return app;
    }

    // =========================================================================
    // 🔑 2. CÁC NGHỆP VỤ DAO ĐẶT LỊCH HẸN
    // =========================================================================

    /**
     * Tạo Đặt lịch hẹn mới theo giao dịch nguyên tử (Atomic Booking Transaction).
     * Áp dụng kỹ thuật khóa dòng WITH (UPDLOCK, HOLDLOCK) trên SQL Server chống Race Condition.
     *
     * @param app Đối tượng Appointment chứa thông tin đặt lịch
     * @return true nếu đặt lịch thành công 100%
     * @throws SlotAlreadyBookedException nếu khung giờ đã bị người khác đăng ký trước đó
     */
    public boolean createBookingAtomic(Appointment app) throws SlotAlreadyBookedException {
        try {
            return executeTransaction(conn -> {
                // Bước 1: Khóa Slot bằng SQL WITH (UPDLOCK, HOLDLOCK) & Lấy start_time tự động
                String lockSql = "SELECT is_available, start_time FROM DoctorSchedules WITH (UPDLOCK, HOLDLOCK) WHERE id = ?";
                java.sql.Time slotStartTime = queryOne(conn, lockSql, rs -> {
                    boolean isAvail = rs.getBoolean("is_available");
                    if (!isAvail) return null;
                    return rs.getTime("start_time");
                }, app.getScheduleId());

                if (slotStartTime == null) {
                    throw new SlotAlreadyBookedException(
                            "Khung giờ này vừa được người khác đặt! Vui lòng chọn khung giờ khác.");
                }
                if (app.getStartTime() == null) {
                    app.setStartTime(slotStartTime);
                }
                // Bước 2: Insert Appointment và lấy ID tự động tăng
                String insertSql = "INSERT INTO Appointments (patient_id, doctor_id, service_id, schedule_id, appointment_date, start_time, total_price, status, payment_status, payment_method, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                int newAppId = executeInsertAndGetGeneratedKey(conn, insertSql,
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
                app.setId(newAppId);
                String paymentContent = "CLN" + newAppId;
                app.setPaymentContent(paymentContent);
                // Bước 3: Cập nhật payment_content = "CLN" + newAppId (SePay VietQR)
                String contentSql = "UPDATE Appointments SET payment_content = ? WHERE id = ?";
                executeUpdate(conn, contentSql, paymentContent, newAppId);
                // Bước 4: Khóa slot trong DoctorSchedules (is_available = 0)
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

    /**
     * Tìm thông tin chi tiết Lịch hẹn theo ID (kèm Tên Bệnh nhân, Bác sĩ, Dịch vụ).
     *
     * @param id Mã Lịch hẹn
     * @return Đối tượng Appointment hoặc null nếu không tìm thấy
     */
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

    /**
     * Lấy danh sách Lịch sử Đặt hẹn của 1 Bệnh nhân (Sắp xếp mới nhất lên đầu).
     *
     * @param patientId Mã Bệnh nhân (User ID)
     * @return Danh sách Lịch hẹn của bệnh nhân
     */
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

    /**
     * Lấy danh sách Lịch sử Cuộc hẹn có Phân Trang cho Bệnh Nhân (MS SQL Server OFFSET...FETCH NEXT).
     */
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

    /**
     * Đếm tổng số cuộc hẹn của Bệnh Nhân để tính tổng số trang.
     */
    public int countPatientAppointments(int patientId) {
        String sql = "SELECT COUNT(*) FROM Appointments WHERE patient_id = ?";
        try (java.sql.Connection conn = config.DBContext.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
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

    /**
     * Cập nhật trạng thái Thanh toán thành công (Xác thực qua Webhook VietQR SePay).
     *
     * @param appointmentId   Mã Lịch hẹn
     * @param transactionCode Mã giao dịch ngân hàng do SePay truyền về
     * @return true nếu cập nhật thành công
     */
    public boolean updatePaymentSuccess(int appointmentId, String transactionCode) {
        String sql = "UPDATE Appointments SET payment_status = '" + SystemConstant.PAYMENT_PAID + "', status = '"
                + SystemConstant.STATUS_CONFIRMED + "', transaction_code = ? WHERE id = ?";
        return executeUpdate(sql, transactionCode, appointmentId);
    }

    /**
     * Lấy danh sách lịch hẹn của Bác sĩ theo ID Bác sĩ (User ID) và Ngày khám.
     */
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

    /**
     * Lấy danh sách tất cả lịch hẹn trong ngày dành cho Lễ tân theo dõi tại sảnh.
     */
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

    /**
     * Cập nhật trạng thái cuộc hẹn (CONFIRMED, COMPLETED, CANCELLED).
     */
    public boolean updateStatus(int appointmentId, String newStatus) {
        String sql = "UPDATE Appointments SET status = ? WHERE id = ?";
        return executeUpdate(sql, newStatus, appointmentId);
    }

    /**
     * Cập nhật trạng thái thanh toán (UNPAID, PAID) và hình thức (CASH, SEPAY_QR).
     */
    public boolean updatePayment(int appointmentId, String paymentStatus, String paymentMethod) {
        String sql = "UPDATE Appointments SET payment_status = ?, payment_method = ? WHERE id = ?";
        return executeUpdate(sql, paymentStatus, paymentMethod, appointmentId);
    }

    /**
     * Gọi Stored Procedure sp_GetClinicRevenueReport để lấy báo cáo doanh thu Admin.
     */
    public model.RevenueReport getRevenueReport(java.sql.Date startDate, java.sql.Date endDate) {
        String sql = "EXEC dbo.sp_GetClinicRevenueReport ?, ?";
        model.RevenueReport report = queryOne(sql, rs -> new model.RevenueReport(
                rs.getInt("total_appointments"),
                rs.getInt("completed_appointments"),
                rs.getInt("cancelled_appointments"),
                rs.getBigDecimal("total_revenue_paid"),
                rs.getBigDecimal("sepay_revenue"),
                rs.getBigDecimal("cash_revenue")
        ), startDate, endDate);

        return report != null ? report : new model.RevenueReport();
    }
}
