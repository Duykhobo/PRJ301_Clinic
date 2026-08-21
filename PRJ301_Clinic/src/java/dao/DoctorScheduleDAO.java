package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import config.DBContext;
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
    // 🔍 2. STORED PROCEDURE & QUERY METHODS
    // =========================================================================

    public DoctorSchedule findById(int id) {
        String sql = "SELECT * FROM DoctorSchedules WHERE id = ?";
        return queryOne(sql, this::mapResultSetToSchedule, id);
    }

    public List<DoctorSchedule> findAvailableSlotsByDoctorAndDate(int doctorId, Date workDate) {
        return getAvailableSlots(doctorId, workDate);
    }

    /**
     * Lấy danh sách Khung giờ KHẢ DỤNG (Available) của Bác sĩ theo Ngày thông
     * qua Stored Procedure sp_GetAvailableSlotsByDoctorAndDate.
     */
    public List<DoctorSchedule> getAvailableSlots(int doctorId, Date workDate) {
        List<DoctorSchedule> list = new ArrayList<>();
        String sql = "{CALL sp_GetAvailableSlotsByDoctorAndDate(?, ?)}";

        try {
            Connection conn = DBContext.getConnection();
            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setInt(1, doctorId);
                cs.setDate(2, workDate);

                try (ResultSet rs = cs.executeQuery()) {
                    while (rs.next()) {
                        list.add(mapResultSetToSchedule(rs));
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi gọi sp_GetAvailableSlotsByDoctorAndDate cho doctorId="
                    + doctorId + ", workDate=" + workDate, e);
        }
        return list;
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
        try {
            Connection conn = DBContext.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(queryExistingSql)) {
                ps.setInt(1, doctorId);
                ps.setDate(2, workDate);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Time t = rs.getTime("start_time");
                        if (t != null) {
                            existingSlotsMap.put(rs.getInt("id"), t.toString().substring(0, 5));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách ca khám của bác sĩ", e);
        }

        // 1. CHÈN BỔ SUNG CÁC CA MỚI NẾU CHƯA TỒN TẠI
        String insertSql = "INSERT INTO DoctorSchedules (doctor_id, work_date, start_time, end_time, is_available) VALUES (?, ?, ?, ?, 1)";
        try {
            Connection conn = DBContext.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                boolean hasNewSlots = false;
                for (String hhmm : configuredTimes) {
                    if (!existingSlotsMap.containsValue(hhmm)) {
                        String startTimeStr = hhmm + ":00";
                        try {
                            Time startTime = Time.valueOf(startTimeStr);
                            long endMs = startTime.getTime() + 60L * 60L * 1000L;
                            Time endTime = new Time(endMs);

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
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi chèn ca khám mới", e);
        }

        // 2. DỌN DẸP XÓA CÁC CA MÀ ADMIN ĐÃ BỎ CHỌN (CHỈ XÓA CA CHƯA CÓ BỆNH NHÂN ĐẶT LỊCH)
        String deleteUnusedSql = "DELETE FROM DoctorSchedules WHERE id = ? AND id NOT IN (SELECT schedule_id FROM Appointments WHERE schedule_id IS NOT NULL)";
        try {
            Connection conn = DBContext.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(deleteUnusedSql)) {
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
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xóa các ca đã hủy từ cấu hình Admin", e);
        }
    }

    public List<DoctorSchedule> findSchedulesByDoctorAndDate(int doctorId, Date workDate) {
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

    public boolean insertSlot(int doctorId, Date workDate, Time startTime, Time endTime) {
        String checkSql = "SELECT TOP 1 * FROM DoctorSchedules WHERE doctor_id = ? AND work_date = ? AND start_time = CAST(? AS TIME)";
        DoctorSchedule existing = queryOne(checkSql, this::mapResultSetToSchedule, doctorId, workDate, startTime.toString());
        if (existing != null) {
            return false;
        }
        String sql = "INSERT INTO DoctorSchedules (doctor_id, work_date, start_time, end_time, is_available) VALUES (?, ?, CAST(? AS TIME), CAST(? AS TIME), 1)";
        return executeUpdate(sql, doctorId, workDate, startTime.toString(), endTime.toString());
    }

    public boolean deleteSlot(int slotId, int doctorId) {
        String sql = "DELETE FROM DoctorSchedules WHERE id = ? AND doctor_id = ? AND NOT EXISTS (SELECT 1 FROM Appointments WHERE schedule_id = ?)";
        return executeUpdate(sql, slotId, doctorId, slotId);
    }

    // =========================================================================
    // 📅 3. ĐĂNG KÝ VÀ DỌN DẸP LỊCH LÀM VIỆC THEO TUẦN (BATCH SCHEDULE)
    // =========================================================================

    /**
     * Đăng ký Lịch Làm Việc Hàng Loạt Theo Tuần (Batch Insert with Idempotency).
     * Bác sĩ có thể thiết lập lịch cho các ngày trong tuần (T2 - CN) trong khoảng thời gian xác định.
     *
     * @param doctorId           Mã hồ sơ bác sĩ
     * @param startDate          Ngày bắt đầu (phải >= Hôm nay)
     * @param endDate            Ngày kết thúc (tối đa 90 ngày kể từ startDate)
     * @param selectedDaysOfWeek Danh sách các thứ trong tuần (1: Thứ 2, 2: Thứ 3, ..., 7: Chủ Nhật)
     * @param selectedTimeSlots  Danh sách khung giờ (ví dụ: ["08:00", "09:00", ...])
     * @return Số lượng ca khám mới được tạo thành công
     * @throws IllegalArgumentException khi tham số không hợp lệ
     */
    public int registerWeeklyScheduleBatch(int doctorId, LocalDate startDate, LocalDate endDate,
                                           List<Integer> selectedDaysOfWeek, List<String> selectedTimeSlots) {
        if (doctorId <= 0) {
            throw new IllegalArgumentException("Mã bác sĩ không hợp lệ!");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Ngày bắt đầu và ngày kết thúc không được để trống!");
        }
        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Không thể đăng ký lịch khám cho các ngày trong quá khứ!");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Ngày bắt đầu không được lớn hơn ngày kết thúc!");
        }
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        if (daysBetween > 90) {
            throw new IllegalArgumentException("Khoảng thời gian đăng ký không được vượt quá 90 ngày (3 tháng)!");
        }
        if (selectedDaysOfWeek == null || selectedDaysOfWeek.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn ít nhất một ngày làm việc trong tuần!");
        }
        if (selectedTimeSlots == null || selectedTimeSlots.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn ít nhất một khung giờ khám!");
        }

        // 1. Lấy tất cả các ca hiện có trong khoảng ngày này của bác sĩ để chống trùng lặp (Idempotent)
        String queryExistingSql = "SELECT work_date, start_time FROM DoctorSchedules WHERE doctor_id = ? AND work_date BETWEEN ? AND ?";
        Set<String> existingKeySet = new HashSet<>(); // "yyyy-MM-dd_HH:mm"

        Date sqlStartDate = Date.valueOf(startDate);
        Date sqlEndDate = Date.valueOf(endDate);

        try {
            Connection conn = DBContext.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(queryExistingSql)) {
                ps.setInt(1, doctorId);
                ps.setDate(2, sqlStartDate);
                ps.setDate(3, sqlEndDate);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Date d = rs.getDate("work_date");
                        Time t = rs.getTime("start_time");
                        if (d != null && t != null) {
                            String timeStr = t.toString().substring(0, 5);
                            existingKeySet.add(d.toString() + "_" + timeStr);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi kiểm tra ca khám hiện có của bác sĩ", e);
        }

        // 2. Thực hiện Batch Insert các ca chưa tồn tại
        String insertSql = "INSERT INTO DoctorSchedules (doctor_id, work_date, start_time, end_time, is_available) VALUES (?, ?, ?, ?, 1)";
        int totalInserted = 0;

        try {
            Connection conn = DBContext.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                LocalDate current = startDate;
                boolean hasBatch = false;

                while (!current.isAfter(endDate)) {
                    int dayOfWeek = current.getDayOfWeek().getValue(); // 1 (Mon) -> 7 (Sun)
                    if (selectedDaysOfWeek.contains(dayOfWeek)) {
                        Date currentSqlDate = Date.valueOf(current);

                        for (String hhmm : selectedTimeSlots) {
                            String trimmed = hhmm.trim();
                            if (trimmed.length() >= 5) {
                                String slotKey = current.toString() + "_" + trimmed.substring(0, 5);
                                if (!existingKeySet.contains(slotKey)) {
                                    String startTimeStr = trimmed.substring(0, 5) + ":00";
                                    try {
                                        Time startTime = Time.valueOf(startTimeStr);
                                        long endMs = startTime.getTime() + 60L * 60L * 1000L;
                                        Time endTime = new Time(endMs);

                                        ps.setInt(1, doctorId);
                                        ps.setDate(2, currentSqlDate);
                                        ps.setTime(3, startTime);
                                        ps.setTime(4, endTime);
                                        ps.addBatch();
                                        hasBatch = true;
                                        totalInserted++;
                                    } catch (Exception e) {
                                        LOGGER.log(Level.WARNING, "Bỏ qua khung giờ không hợp lệ: " + trimmed, e);
                                    }
                                }
                            }
                        }
                    }
                    current = current.plusDays(1);
                }

                if (hasBatch) {
                    ps.executeBatch();
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thực thi Batch Insert lịch làm việc theo tuần", e);
            throw new RuntimeException("Lỗi hệ thống khi lưu lịch làm việc: " + e.getMessage(), e);
        }

        return totalInserted;
    }

    /**
     * Dọn dẹp / Xóa hàng loạt các ca khám trống chưa có bệnh nhân đặt trong khoảng ngày đã chọn.
     *
     * @param doctorId           Mã bác sĩ
     * @param startDate          Ngày bắt đầu
     * @param endDate            Ngày kết thúc
     * @param selectedDaysOfWeek Danh sách các thứ (nếu null hoặc rỗng thì áp dụng tất cả)
     * @return Số lượng ca khám đã được xóa
     */
    public int clearAvailableWeeklySchedules(int doctorId, LocalDate startDate, LocalDate endDate,
                                            List<Integer> selectedDaysOfWeek) {
        if (doctorId <= 0 || startDate == null || endDate == null) {
            return 0;
        }

        String deleteSql = "DELETE FROM DoctorSchedules WHERE id = ? AND doctor_id = ? "
                + "AND NOT EXISTS (SELECT 1 FROM Appointments a WHERE a.schedule_id = DoctorSchedules.id)";

        // Lấy danh sách ID các ca thỏa mãn
        String findSql = "SELECT id, work_date FROM DoctorSchedules WHERE doctor_id = ? AND work_date BETWEEN ? AND ? "
                + "AND is_available = 1 AND NOT EXISTS (SELECT 1 FROM Appointments a WHERE a.schedule_id = DoctorSchedules.id)";

        List<Integer> slotIdsToDelete = new ArrayList<>();
        try {
            Connection conn = DBContext.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(findSql)) {
                ps.setInt(1, doctorId);
                ps.setDate(2, Date.valueOf(startDate));
                ps.setDate(3, Date.valueOf(endDate));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int slotId = rs.getInt("id");
                        Date workDate = rs.getDate("work_date");
                        if (workDate != null) {
                            LocalDate ld = workDate.toLocalDate();
                            int dayOfWeek = ld.getDayOfWeek().getValue();
                            if (selectedDaysOfWeek == null || selectedDaysOfWeek.isEmpty() || selectedDaysOfWeek.contains(dayOfWeek)) {
                                slotIdsToDelete.add(slotId);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm ca khám cần xóa", e);
        }

        if (slotIdsToDelete.isEmpty()) {
            return 0;
        }

        int deletedCount = 0;
        try {
            Connection conn = DBContext.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                for (Integer id : slotIdsToDelete) {
                    ps.setInt(1, id);
                    ps.setInt(2, doctorId);
                    ps.addBatch();
                }
                int[] results = ps.executeBatch();
                for (int r : results) {
                    if (r > 0 || r == Statement.SUCCESS_NO_INFO) {
                        deletedCount++;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xóa hàng loạt ca khám trống", e);
        }
        return deletedCount;
    }
}
