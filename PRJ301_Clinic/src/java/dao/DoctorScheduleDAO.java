package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import config.DBContext;
import java.util.HashMap;
import model.DoctorSchedule;

/**
 * Lớp DoctorScheduleDAO quản lý Khung giờ làm việc 60 phút của Bác sĩ
 * (DoctorSchedules). Tích hợp gọi Stored Procedure
 * sp_GetAvailableSlotsByDoctorAndDate.
 */
public class DoctorScheduleDAO extends BaseDAO<DoctorSchedule> {

    private static final Logger LOGGER = Logger.getLogger(DoctorScheduleDAO.class.getName());

    // =========================================================================
    // 🧱 1. HELPER MAPPER (CHUẨN DRY)
    // =========================================================================
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
    public List<DoctorSchedule> findAvailableSlotsByDoctorAndDate(int doctorId, Date workDate) {
        ensureSchedulesExist(doctorId, workDate);
        List<DoctorSchedule> list = new ArrayList<>();
        String sql = "{call sp_GetAvailableSlotsByDoctorAndDate(?, ?)}";
        try ( Connection conn = DBContext.getConnection();  CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, doctorId);
            cs.setDate(2, workDate);

            try ( ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToSchedule(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi gọi Stored Proc sp_GetAvailableSlotsByDoctorAndDate", e);
        }
        return list;
    }

    public DoctorSchedule findById(int id) {
        String sql = "SELECT * FROM DoctorSchedules WHERE id = ?";
        return queryOne(sql, this::mapResultSetToSchedule, id);
    }

    /**
     * Kiểm tra xem ngày làm việc đã có ca nào trong CSDL chưa.
     */
    public boolean hasSchedules(int doctorId, Date workDate) {
        String sql = "SELECT TOP 1 * FROM DoctorSchedules WHERE doctor_id = ? AND work_date = ?";
        return queryOne(sql, this::mapResultSetToSchedule, doctorId, workDate) != null;
    }

    /**
     * Tự động đồng bộ Khung giờ làm việc (Slots) cho Bác sĩ theo Cấu hình CSDL
     * mới nhất (CLINIC_TIME_SLOTS). 1. Tự động chèn các mốc giờ mới được Admin
     * bật. 2. Tự động dọn dẹp (xóa) các mốc giờ Admin đã bỏ chọn (với điều kiện
     * ca đó chưa có bệnh nhân đặt lịch).
     */
    public void ensureSchedulesExist(int doctorId, Date workDate) {
        if (workDate == null || doctorId <= 0) {
            return;
        }

        // Lấy cấu hình khung giờ làm việc từ ClinicSettings
        ClinicSettingDAO settingDAO = new ClinicSettingDAO();
        Map<String, String> settings = settingDAO.getSettingsMap();

        String timeSlotsConfig = settings.get("CLINIC_TIME_SLOTS");
        if (timeSlotsConfig == null || timeSlotsConfig.trim().isEmpty()) {
            timeSlotsConfig = settings.get("time_slots");
        }

        List<String> configuredTimes = new ArrayList<>();
        if (timeSlotsConfig != null && !timeSlotsConfig.trim().isEmpty()) {
            for (String s : timeSlotsConfig.split(",")) {
                String trimmed = s.trim();
                if (trimmed.length() >= 5) {
                    configuredTimes.add(trimmed.substring(0, 5));
                }
            }
        } else {
            configuredTimes.add("08:00");
            configuredTimes.add("09:00");
            configuredTimes.add("10:00");
            configuredTimes.add("11:00");
            configuredTimes.add("14:00");
            configuredTimes.add("15:00");
            configuredTimes.add("16:00");
            configuredTimes.add("17:00");
        }

        // Lấy tất cả các ca hiện có trong CSDL của bác sĩ vào ngày này
        String queryExistingSql = "SELECT id, start_time FROM DoctorSchedules WHERE doctor_id = ? AND work_date = ?";
        Map<Integer, String> existingSlotsMap = new HashMap<>(); // slotId -> HH:mm
        try ( Connection conn = DBContext.getConnection();  java.sql.PreparedStatement ps = conn.prepareStatement(queryExistingSql)) {
            ps.setInt(1, doctorId);
            ps.setDate(2, workDate);
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    java.sql.Time t = rs.getTime("start_time");
                    if (t != null) {
                        existingSlotsMap.put(rs.getInt("id"), t.toString().substring(0, 5));
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách ca khám của bác sĩ", e);
        }

        // 1. CHÈN BỔ SUNG CÁC CA MỚI NẾU CHƯA TỒN TẠI
        String insertSql = "INSERT INTO DoctorSchedules (doctor_id, work_date, start_time, end_time, is_available) VALUES (?, ?, ?, ?, 1)";
        try ( Connection conn = DBContext.getConnection();  java.sql.PreparedStatement ps = conn.prepareStatement(insertSql)) {

            boolean hasNewSlots = false;
            for (String hhmm : configuredTimes) {
                if (!existingSlotsMap.containsValue(hhmm)) {
                    String startTimeStr = hhmm + ":00";
                    try {
                        java.sql.Time startTime = java.sql.Time.valueOf(startTimeStr);
                        long endMs = startTime.getTime() + 60L * 60L * 1000L;
                        java.sql.Time endTime = new java.sql.Time(endMs);

                        ps.setInt(1, doctorId);
                        ps.setDate(2, workDate);
                        ps.setTime(3, startTime);
                        ps.setTime(4, endTime);
                        ps.addBatch();
                        hasNewSlots = true;
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Bỏ qua slot không đúng định dạng: " + hhmm, e);
                    }
                }
            }
            if (hasNewSlots) {
                ps.executeBatch();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi chèn ca khám mới", e);
        }

        // 2. DỌN DẸP XÓA CÁC CA MÀ ADMIN ĐÃ BỎ CHỌN (CHỈ XÓA CA CHƯA CÓ BỆNH NHÂN ĐẶT LỊCH)
        String deleteUnusedSql = "DELETE FROM DoctorSchedules WHERE id = ? AND id NOT IN (SELECT schedule_id FROM Appointments WHERE schedule_id IS NOT NULL)";
        try ( Connection conn = DBContext.getConnection();  java.sql.PreparedStatement ps = conn.prepareStatement(deleteUnusedSql)) {

            boolean hasDeletedSlots = false;
            for (Map.Entry<Integer, String> entry : existingSlotsMap.entrySet()) {
                int slotId = entry.getKey();
                String hhmm = entry.getValue();
                if (!configuredTimes.contains(hhmm)) {
                    ps.setInt(1, slotId);
                    ps.addBatch();
                    hasDeletedSlots = true;
                }
            }
            if (hasDeletedSlots) {
                ps.executeBatch();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xóa các ca đã hủy từ cấu hình Admin", e);
        }
    }

    public List<DoctorSchedule> findSchedulesByDoctorAndDate(int doctorId, Date workDate) {
        ensureSchedulesExist(doctorId, workDate);

        String sql = "SELECT ds.id, ds.doctor_id, ds.work_date, ds.start_time, ds.end_time, "
                + "CASE "
                + "  WHEN ds.work_date < CAST(GETDATE() AS DATE) THEN 0 "
                + "  WHEN ds.work_date = CAST(GETDATE() AS DATE) AND ds.start_time <= CAST(GETDATE() AS TIME) THEN 0 "
                + "  WHEN EXISTS ( "
                + "      SELECT 1 FROM Appointments a "
                + "      WHERE a.schedule_id = ds.id AND a.status IN ('PENDING', 'CONFIRMED', 'COMPLETED') "
                + "  ) THEN 0 "
                + "  ELSE ds.is_available "
                + "END AS is_available "
                + "FROM DoctorSchedules ds "
                + "WHERE ds.doctor_id = ? AND ds.work_date = ? "
                + "ORDER BY ds.start_time ASC";
        return queryList(sql, this::mapResultSetToSchedule, doctorId, workDate);
    }

    public boolean updateSlotAvailability(int id, boolean isAvailable) {
        String sql = "UPDATE DoctorSchedules SET is_available = ? WHERE id = ?";
        return executeUpdate(sql, isAvailable, id);
    }

    public boolean insertSlot(int doctorId, Date workDate, java.sql.Time startTime, java.sql.Time endTime) {
        String checkSql = "SELECT TOP 1 * FROM DoctorSchedules WHERE doctor_id = ? AND work_date = ? AND start_time = ?";
        DoctorSchedule existing = queryOne(checkSql, this::mapResultSetToSchedule, doctorId, workDate, startTime);
        if (existing != null) {
            return false;
        }
        String sql = "INSERT INTO DoctorSchedules (doctor_id, work_date, start_time, end_time, is_available) VALUES (?, ?, ?, ?, 1)";
        return executeUpdate(sql, doctorId, workDate, startTime, endTime);
    }

    public boolean deleteSlot(int slotId, int doctorId) {
        String sql = "DELETE FROM DoctorSchedules WHERE id = ? AND doctor_id = ? AND NOT EXISTS (SELECT 1 FROM Appointments WHERE schedule_id = ?)";
        return executeUpdate(sql, slotId, doctorId, slotId);
    }
}
