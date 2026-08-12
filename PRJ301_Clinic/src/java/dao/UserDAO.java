package dao;

import config.DBContext;
import model.User;
import util.BCryptUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp UserDAO triển khai các thao tác CSDL cho bảng Users.
 * Áp dụng nguyên tắc DRY (Don't Repeat Yourself) qua hàm helper mapResultSetToUser.
 * Bạn tự gõ code triển khai cho các hàm TODO bên dưới để rèn luyện thói quen!
 */
public class UserDAO {

    // =========================================================================
    // 🧱 1. ÁP DỤNG NGUYÊN TẮC DRY (DON'T REPEAT YOURSELF) - HELPER MAPPER
    // =========================================================================
    /**
     * Helper Mapper dùng chung cho tất cả các hàm SELECT (Tái sử dụng code 100%, không lặp code).
     */
    protected User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setFullname(rs.getString("fullname"));
        user.setPhone(rs.getString("phone"));
        user.setRole(rs.getString("role"));
        user.setStatus(rs.getBoolean("status"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }

    // =========================================================================
    // 🔑 2. CÁC NGHỆP VỤ ĐĂNG NHẬP, ĐĂNG KÝ & KIỂM TRA (TODO DÀNH CHO BẠN)
    // =========================================================================

    /**
     * TODO 1: Viết hàm Đăng nhập login(String username, String rawPassword)
     * Gợi ý Flow:
     * - Query: "SELECT * FROM Users WHERE username = ?"
     * - try-with-resources cho Connection & PreparedStatement
     * - Check status == true (tài khoản không bị khóa)
     * - Check BCryptUtil.checkPassword(rawPassword, dbHash)
     * - Trả về mapResultSetToUser(rs)
     */
    public User login(String username, String rawPassword) {
        // TODO: Bạn tự gõ code tại đây
        return null;
    }

    /**
     * TODO 2: Viết hàm Đăng ký / Thêm mới register(User user)
     * Gợi ý Flow:
     * - Hash password trước: String hashed = BCryptUtil.hashPassword(user.getPassword());
     * - Query: "INSERT INTO Users (username, password, email, fullname, phone, role, status) VALUES (?, ?, ?, ?, ?, ?, ?)"
     * - Set các tham số 1..7
     * - executeUpdate() > 0
     */
    public boolean register(User user) {
        // TODO: Bạn tự gõ code tại đây
        return false;
    }

    /**
     * TODO 3: Kiểm tra trùng username
     */
    public boolean existsByUsername(String username) {
        // TODO: Bạn tự gõ code tại đây
        return false;
    }

    /**
     * TODO 4: Kiểm tra trùng email
     */
    public boolean existsByEmail(String email) {
        // TODO: Bạn tự gõ code tại đây
        return false;
    }

    /**
     * TODO 5: Tìm User theo ID
     */
    public User findById(int id) {
        // TODO: Bạn tự gõ code tại đây
        return null;
    }

    /**
     * TODO 6: Cập nhật trạng thái Active/Banned
     */
    public boolean updateStatus(int id, boolean status) {
        // TODO: Bạn tự gõ code tại đây
        return false;
    }

    /**
     * TODO 7: Lấy toàn bộ danh sách Users (Admin)
     */
    public List<User> findAll() {
        // TODO: Bạn tự gõ code tại đây
        return new ArrayList<>();
    }
}
