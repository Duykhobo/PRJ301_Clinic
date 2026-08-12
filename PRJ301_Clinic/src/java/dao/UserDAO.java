package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import constant.RoleConstant;
import model.User;
import util.BCryptUtil;

/**
 * Lớp UserDAO quản lý các thao tác CSDL cho bảng Users. Bạn tự gõ code cho các
 * hàm TODO bên dưới để rèn luyện thói quen!
 */
public class UserDAO extends BaseDAO<User> {

    // =========================================================================
    // 🧱 1. HELPER MAPPER (CHUẨN DRY)
    // =========================================================================
    /**
     * Helper Mapper chuyển 1 dòng ResultSet thành đối tượng User.
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
    // 🔑 2. CÁC NGHỆP VỤ ĐĂNG NHẬP & ĐĂNG KÝ (TODO DÀNH CHO BẠN)
    // =========================================================================
    /**
     * TODO 1: Viết hàm Đăng nhập login(String username, String rawPassword) Gợi
     * ý Flow: - Query: "SELECT * FROM Users WHERE username = ?" - Dùng
     * queryOne(sql, this::mapResultSetToUser, username) - Check status == true
     * (Tài khoản đang Active) - Check BCryptUtil.checkPassword(rawPassword,
     * user.getPassword())
     */
    public User login(String username, String rawPassword) {
        String sql = "SELECT * FROM Users WHERE username = ?";
        User user = queryOne(sql, this::mapResultSetToUser, username);
        if (user != null && user.isStatus()) {
            if (BCryptUtil.checkPassword(rawPassword, user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    /**
     * TODO 2: Viết hàm Đăng ký register(User user) - Form 4 trường (username,
     * password, fullname, phone + email optional) Gợi ý Flow: - Hash password
     * trước: String hashed = BCryptUtil.hashPassword(user.getPassword()); -
     * Query: "INSERT INTO Users (username, password, email, fullname, phone,
     * role, status) VALUES (?, ?, ?, ?, ?, ?, ?)" - Dùng executeUpdate(sql,
     * user.getUsername(), hashed, user.getEmail(), user.getFullname(),
     * user.getPhone(), "PATIENT", true)
     */
    public boolean register(User user) {
        String sql = "INSERT INTO Users (username, password, email, fullname, phone, role, status)"
                + "VALUES(?, ?, ?, ?, ?, ?, ?)";
        String hashedPassword = BCryptUtil.hashPassword(user.getPassword());
        String role = (user.getRole() != null && !user.getRole().trim().isEmpty()) ? user.getRole()
                : RoleConstant.PATIENT;
        return executeUpdate(sql, user.getUsername(), hashedPassword, user.getEmail(), user.getFullname(),
                user.getPhone(), role, true);
    }

    /**
     * TODO 3: Kiểm tra trùng username existsByUsername(String username) Gợi ý:
     * SELECT 1 FROM Users WHERE username = ?
     */
    public boolean existsByUsername(String username) {
        String sql = "SELECT * FROM Users WHERE username = ?";
        return queryOne(sql, this::mapResultSetToUser, username) != null;
    }

    /**
     * TODO 4: Kiểm tra trùng email existsByEmail(String email)
     */
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String sql = "SELECT * FROM Users WHERE email = ?";
        return queryOne(sql, this::mapResultSetToUser, email) != null;
    }

    /**
     * TODO 5: Tìm User theo ID findById(int id)
     */
    public User findById(int id) {
        String sql = "SELECT * FROM Users WHERE id = ?";
        return queryOne(sql, this::mapResultSetToUser, id);
    }

    /**
     * TODO 6: Cập nhật trạng thái Active/Banned updateStatus(int id, boolean
     * status)
     */
    public boolean updateStatus(int id, boolean status) {
        String sql = "UPDATE Users SET status = ? WHERE id = ?";
        return executeUpdate(sql, status, id);
    }

    /**
     * TODO 7: Lấy danh sách toàn bộ Users (Admin) findAll()
     */
    public List<User> findAll() {
        String sql = "SELECT * FROM Users";
        return queryList(sql, this::mapResultSetToUser);
    }
}
