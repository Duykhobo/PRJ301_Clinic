package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import config.DBContext;
import model.DoctorSchedule;

/**
 * Lớp DoctorScheduleDAO quản lý Khung giờ làm việc 60 phút của Bác sĩ
 * (DoctorSchedules).
 * Tích hợp gọi Stored Procedure sp_GetAvailableSlotsByDoctorAndDate (Điểm cộng
 * 10/10).
 */
public class DoctorScheduleDAO extends BaseDAO<DoctorSchedule> {

    private static final Logger LOGGER = Logger.getLogger(DoctorScheduleDAO.class.getName());

    // =========================================================================
    // 🧱 1. HELPER MAPPER (CHUẨN DRY)
    // =========================================================================
    /**
     * Mapper chuyển ResultSet thành đối tượng DoctorSchedule.
     */
    protected DoctorSchedule mapResultSetToSchedule(ResultSet rs) throws SQLException {
        DoctorSchedule schedule = new DoctorSchedule();
        schedule.setId(rs.getInt("id"));
        schedule.setDoctorId(rs.getInt("doctor_id"));
        schedule.setWorkDate(rs.getDate("work_date"));
        schedule.setStartTime(rs.getTime("start_time"));
        schedule.setEndTime(rs.getTime("end_time"));
        schedule.setIsAvailable(rs.getBoolean("is_available"));
        return schedule;
    }

    // =========================================================================
    // 🔑 2. CÁC NGHỆP VỤ DAO LỊCH LÀM VIỆC (TODO BẠN TỰ GÕ CODE THỰC HÀNH)
    // =========================================================================

    /**
     * TODO 1: Gọi Stored Procedure sp_GetAvailableSlotsByDoctorAndDate lấy danh
     * sách Slot 60m còn trống
     * Gợi ý SQL Stored Proc: "{call sp_GetAvailableSlotsByDoctorAndDate(?, ?)}"
     * Cú pháp JDBC:
     * try (Connection conn = DBContext.getConnection();
     * CallableStatement cs = conn.prepareCall("{call
     * sp_GetAvailableSlotsByDoctorAndDate(?, ?)}")) {
     * cs.setInt(1, doctorId);
     * cs.setDate(2, workDate);
     * try (ResultSet rs = cs.executeQuery()) {
     * while (rs.next()) list.add(mapResultSetToSchedule(rs));
     * }
     * }
     */
    public List<DoctorSchedule> findAvailableSlotsByDoctorAndDate(int doctorId, Date workDate) {
        List<DoctorSchedule> list = new ArrayList<>();
        String sql = "{call sp_GetAvailableSlotsByDoctorAndDate(?, ?)}";
        try (Connection conn = DBContext.getConnection();
                CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, doctorId);
            cs.setDate(2, workDate);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToSchedule(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Loi khi goi Stored Proc sp_GetAvailableSlotsByDoctorAndDate", e);
        }
        return list;
    }

    /**
     * TODO 2: Tìm Slot theo ID
     * Gợi ý: SELECT * FROM DoctorSchedules WHERE id = ?
     */
    public DoctorSchedule findById(int id) {
        String sql = "SELECT * FROM DoctorSchedules WHERE id = ?";
        return queryOne(sql, this::mapResultSetToSchedule, id);
    }

    /**
     * TODO 3: Cập nhật trạng thái Slot (is_available = 0: Khóa / 1: Mở)
     * Gợi ý: UPDATE DoctorSchedules SET is_available = ? WHERE id = ?
     */
    public boolean updateSlotAvailability(int id, boolean isAvailable) {
        String sql = "UPDATE DoctorSchedules SET is_available = ? WHERE id = ?";
        return executeUpdate(sql, isAvailable, id);
    }
}
