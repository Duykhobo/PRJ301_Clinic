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
    // 🔑 2. CÁC NGHỆP VỤ ĐĂNG NHẬP & ĐĂNG KÝ
    // =========================================================================

    /**
     * Xác thực Đăng nhập tài khoản bằng Username và Mật khẩu thô (BCrypt Verified).
     *
     * @param username    Tên đăng nhập
     * @param rawPassword Mật khẩu người dùng nhập vào
     * @return Đối tượng User nếu thành công và tài khoản Active, ngược lại trả về null
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
     * Đăng ký tài khoản Bệnh nhân mới (Form 4 trường tối giản + email optional).
     * Tự động băm mật khẩu bằng BCryptUtil trước khi lưu vào CSDL.
     *
     * @param user Đối tượng User chứa thông tin đăng ký
     * @return true nếu đăng ký thành công
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
     * Kiểm tra xem Username đã tồn tại trong CSDL hay chưa.
     *
     * @param username Tên đăng nhập cần kiểm tra
     * @return true nếu username đã tồn tại
     */
    public boolean existsByUsername(String username) {
        String sql = "SELECT * FROM Users WHERE username = ?";
        return queryOne(sql, this::mapResultSetToUser, username) != null;
    }

    /**
     * Kiểm tra xem Email đã tồn tại trong CSDL hay chưa.
     *
     * @param email Email cần kiểm tra
     * @return true nếu email đã tồn tại và không rỗng
     */
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String sql = "SELECT * FROM Users WHERE email = ?";
        return queryOne(sql, this::mapResultSetToUser, email) != null;
    }

    /**
     * Tìm thông tin Người dùng theo ID.
     *
     * @param id Mã User ID
     * @return Đối tượng User hoặc null nếu không tìm thấy
     */
    public User findById(int id) {
        String sql = "SELECT * FROM Users WHERE id = ?";
        return queryOne(sql, this::mapResultSetToUser, id);
    }

    /**
     * Cập nhật trạng thái Tài khoản (Active / Banned).
     *
     * @param id     Mã User ID
     * @param status true: Active, false: Banned
     * @return true nếu cập nhật thành công
     */
    public boolean updateStatus(int id, boolean status) {
        String sql = "UPDATE Users SET status = ? WHERE id = ?";
        return executeUpdate(sql, status, id);
    }

    /**
     * Lấy danh sách toàn bộ Người dùng trong hệ thống (Dành cho Admin).
     *
     * @return Danh sách tất cả Users
     */
    public List<User> findAll() {
        String sql = "SELECT * FROM Users";
        return queryList(sql, this::mapResultSetToUser);
    }
}
