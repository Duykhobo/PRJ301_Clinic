package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import model.DoctorProfile;

/**
 * Lớp DoctorProfileDAO quản lý thông tin Hồ sơ Bác sĩ (DoctorProfiles & Users).
 * Kế thừa BaseDAO<DoctorProfile> áp dụng chuẩn DRY.
 */
public class DoctorProfileDAO extends BaseDAO<DoctorProfile> {

    // =========================================================================
    // 🧱 1. HELPER MAPPER (CHUẨN DRY)
    // =========================================================================
    /**
     * Mapper chuyển ResultSet từ câu SQL JOIN DoctorProfiles + Users.
     */
    protected DoctorProfile mapResultSetToDoctorProfile(ResultSet rs) throws SQLException {
        DoctorProfile doc = new DoctorProfile();
        doc.setId(rs.getInt("id"));
        doc.setUserId(rs.getInt("user_id"));
        doc.setSpecialty(rs.getString("specialty"));
        doc.setExperienceYears(rs.getInt("experience_years"));
        doc.setRoomNumber(rs.getString("room_number"));
        doc.setBio(rs.getString("bio"));

        // Map thông tin JOIN từ bảng Users (nếu có trong câu query)
        try {
            doc.setDoctorName(rs.getString("fullname"));
            doc.setDoctorPhone(rs.getString("phone"));
            doc.setDoctorEmail(rs.getString("email"));
        } catch (SQLException ignored) {
            // Cho phép bỏ qua nếu câu SQL đơn giản không JOIN với Users
        }
        return doc;
    }

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
        String sql = "SELECT d.*, u.fullname, u.phone, u.email FROM DoctorProfiles d JOIN Users u ON d.user_id = u.id WHERE u.status = 1";
        return queryList(sql, this::mapResultSetToDoctorProfile);
    }

    /**
     * Tìm thông tin Hồ sơ Bác sĩ theo ID Bác sĩ (Doctor Profile ID).
     *
     * @param id Mã Bác sĩ (id trong bảng DoctorProfiles)
     * @return Đối tượng DoctorProfile hoặc null nếu không tìm thấy
     */
    public DoctorProfile findById(int id) {
        String sql = "SELECT d.*, u.fullname, u.phone, u.email FROM DoctorProfiles d JOIN Users u ON d.user_id = u.id WHERE d.id = ?";
        return queryOne(sql, this::mapResultSetToDoctorProfile, id);
    }

    /**
     * Tìm thông tin Hồ sơ Bác sĩ theo Mã Tài khoản Đăng nhập (User ID).
     * Dành cho Bác sĩ khi đã đăng nhập hệ thống xem Hồ sơ cá nhân.
     *
     * @param userId Mã Tài khoản người dùng (user_id)
     * @return Đối tượng DoctorProfile tương ứng với tài khoản Bác sĩ
     */
    public DoctorProfile findByUserId(int userId) {
        String sql = "SELECT d.*, u.fullname, u.phone, u.email FROM DoctorProfiles d JOIN Users u ON d.user_id = u.id WHERE d.user_id = ?";
        return queryOne(sql, this::mapResultSetToDoctorProfile, userId);
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
