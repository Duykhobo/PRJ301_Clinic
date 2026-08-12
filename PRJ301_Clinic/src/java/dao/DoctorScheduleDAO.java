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
        try {
            schedule.setId(rs.getInt("id"));
        } catch (SQLException e) {
            schedule.setId(rs.getInt("schedule_id"));
        }
        schedule.setDoctorId(rs.getInt("doctor_id"));
        schedule.setWorkDate(rs.getDate("work_date"));
        schedule.setStartTime(rs.getTime("start_time"));
        schedule.setEndTime(rs.getTime("end_time"));
        schedule.setIsAvailable(rs.getBoolean("is_available"));
        return schedule;
    }

    // =========================================================================
    // 🔑 2. CÁC NGHỆP VỤ DAO LỊCH LÀM VIỆC BÁC SĨ
    // =========================================================================

    /**
     * Lấy danh sách các Slot 60 phút còn trống của Bác sĩ theo Ngày khám cụ thể.
     * Gọi Stored Procedure sp_GetAvailableSlotsByDoctorAndDate trên SQL Server.
     *
     * @param doctorId Mã Bác sĩ (Doctor Profile ID)
     * @return Danh sách các Ca rảnh DoctorSchedule
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
     * Tìm thông tin Khung giờ làm việc theo ID Slot.
     *
     * @param id Mã Slot (Schedule ID)
     * @return Đối tượng DoctorSchedule hoặc null nếu không tìm thấy
     */
    public DoctorSchedule findById(int id) {
        String sql = "SELECT * FROM DoctorSchedules WHERE id = ?";
        return queryOne(sql, this::mapResultSetToSchedule, id);
    }

    /**
     * Lấy tất cả Slot 60 phút (cả rảnh và đã đặt) của Bác sĩ theo Ngày để hiển thị sơ đồ trực quan.
     * Tự động kiểm tra bảng Appointments: nếu đã có cuộc hẹn Hợp lệ (PENDING, CONFIRMED, COMPLETED) -> Khóa slot (is_available = 0).
     */
    public List<DoctorSchedule> findSchedulesByDoctorAndDate(int doctorId, Date workDate) {
        String sql = "SELECT ds.id, ds.doctor_id, ds.work_date, ds.start_time, ds.end_time, "
                + "CASE WHEN EXISTS ( "
                + "    SELECT 1 FROM Appointments a "
                + "    WHERE a.schedule_id = ds.id AND a.status IN ('PENDING', 'CONFIRMED', 'COMPLETED') "
                + ") THEN 0 ELSE ds.is_available END AS is_available "
                + "FROM DoctorSchedules ds "
                + "WHERE ds.doctor_id = ? AND ds.work_date = ? "
                + "ORDER BY ds.start_time ASC";
        return queryList(sql, this::mapResultSetToSchedule, doctorId, workDate);
    }

    /**
     * Cập nhật trạng thái Khóa / Mở Slot làm việc của Bác sĩ.
     *
     * @param id          Mã Slot (Schedule ID)
     * @param isAvailable true: Mở slot (Khả dụng), false: Khóa slot (Đã được đặt)
     * @return true nếu cập nhật thành công
     */
    public boolean updateSlotAvailability(int id, boolean isAvailable) {
        String sql = "UPDATE DoctorSchedules SET is_available = ? WHERE id = ?";
        return executeUpdate(sql, isAvailable, id);
    }
}
