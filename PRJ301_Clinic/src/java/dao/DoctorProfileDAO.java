package dao;

import java.util.List;
import model.DoctorProfile;

/**
 * Lớp DoctorProfileDAO quản lý thông tin Hồ sơ Bác sĩ (DoctorProfiles & Users).
 * Kế thừa BaseDAO<DoctorProfile> áp dụng chuẩn SOLID và DRY (AutoMapper Reflection).
 */
public class DoctorProfileDAO extends BaseDAO<DoctorProfile> {

    // =========================================================================
    // 🧱 1. ROWMAPPER (TỰ ĐỘNG BẰNG REFLECTION CHUẨN DRY & SOLID)
    // =========================================================================
    private final RowMapper<DoctorProfile> mapper = autoMapper(DoctorProfile.class);

    private static final String BASE_DOCTOR_SELECT = 
            "SELECT d.*, u.fullname AS doctor_name, u.phone AS doctor_phone, u.email AS doctor_email "
            + "FROM DoctorProfiles d JOIN Users u ON d.user_id = u.id ";

    // =========================================================================
    // 🔑 2. CÁC NGHỆP VỤ DAO BÁC SĨ
    // =========================================================================

    /**
     * Lấy danh sách tất cả Bác sĩ đang Hoạt động kèm Họ tên, SĐT từ bảng Users.
     * Dành cho Bệnh nhân tham khảo và chọn Bác sĩ khi Đặt lịch.
     *
     * @return Danh sách các Hồ sơ Bác sĩ Active
     */
    public List<DoctorProfile> findAllActiveDoctors() {
        String sql = BASE_DOCTOR_SELECT + "WHERE u.status = 1 ORDER BY u.fullname ASC";
        return queryList(sql, mapper);
    }

    /**
     * Tìm thông tin Hồ sơ Bác sĩ theo ID Bác sĩ (Doctor Profile ID).
     *
     * @param id Mã Bác sĩ (id trong bảng DoctorProfiles)
     * @return Đối tượng DoctorProfile hoặc null nếu không tìm thấy
     */
    public DoctorProfile findById(int id) {
        String sql = BASE_DOCTOR_SELECT + "WHERE d.id = ?";
        return queryOne(sql, mapper, id);
    }

    /**
     * Tìm thông tin Hồ sơ Bác sĩ theo Mã Tài khoản Đăng nhập (User ID).
     * Dành cho Bác sĩ khi đã đăng nhập hệ thống xem Hồ sơ cá nhân.
     *
     * @param userId Mã Tài khoản người dùng (user_id)
     * @return Đối tượng DoctorProfile tương ứng với tài khoản Bác sĩ
     */
    public DoctorProfile findByUserId(int userId) {
        String sql = BASE_DOCTOR_SELECT + "WHERE d.user_id = ?";
        return queryOne(sql, mapper, userId);
    }

    /**
     * Cập nhật thông tin Hồ sơ Chuyên môn Bác sĩ (Chuyên khoa, Kinh nghiệm, Phòng, Giới thiệu).
     */
    public boolean updateDoctorProfile(int userId, String specialty, int experienceYears, String roomNumber, String bio) {
        DoctorProfile existing = findByUserId(userId);
        if (existing != null) {
            String sql = "UPDATE DoctorProfiles SET specialty = ?, experience_years = ?, room_number = ?, bio = ? WHERE user_id = ?";
            return executeUpdate(sql, specialty, experienceYears, roomNumber, bio, userId);
        } else {
            String sql = "INSERT INTO DoctorProfiles (user_id, specialty, experience_years, room_number, bio) VALUES (?, ?, ?, ?, ?)";
            return executeUpdate(sql, userId, specialty, experienceYears, roomNumber, bio);
        }
    }
}
