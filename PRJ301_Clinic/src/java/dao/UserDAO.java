package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import constant.RoleConstant;
import model.User;
import util.BCryptUtil;

/**
 * Lớp UserDAO quản lý các thao tác CSDL cho bảng Users.
 */
public class UserDAO extends BaseDAO<User> {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

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
     * @return Đối tượng User nếu thành công và tài khoản Active, ngược lại trả về
     *         null
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
     * Tìm đối tượng Người dùng theo Email.
     *
     * @param email Email người dùng
     * @return Đối tượng User hoặc null nếu không tìm thấy
     */
    public User findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT * FROM Users WHERE email = ?";
        return queryOne(sql, this::mapResultSetToUser, email.trim());
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
        String sql = "SELECT * FROM Users ORDER BY id DESC";
        return queryList(sql, this::mapResultSetToUser);
    }

    /**
     * Đảo trạng thái tài khoản (Active <-> Banned).
     */
    public boolean toggleStatus(int userId) {
        User user = findById(userId);
        if (user == null)
            return false;
        String sql = "UPDATE Users SET status = ? WHERE id = ?";
        return executeUpdate(sql, !user.isStatus(), userId);
    }

    /**
     * Cập nhật Vai trò người dùng (PATIENT, DOCTOR, RECEPTIONIST, ADMIN).
     */
    public boolean updateRole(int userId, String newRole) {
        String sql = "UPDATE Users SET role = ? WHERE id = ?";
        return executeUpdate(sql, newRole, userId);
    }

    /**
     * Lấy danh sách Người dùng có Phân Trang (SQL Server OFFSET...FETCH NEXT).
     */
    public List<User> findPaginated(int offset, int limit) {
        String sql = "SELECT * FROM Users ORDER BY id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        return queryList(sql, this::mapResultSetToUser, offset, limit);
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM Users";
        return queryCount(sql);
    }

    /**
     * Cập nhật thông tin Hồ sơ cá nhân (Họ tên, Email, Số điện thoại).
     */
    public boolean updateProfile(int userId, String fullname, String email, String phone) {
        String sql = "UPDATE Users SET fullname = ?, email = ?, phone = ? WHERE id = ?";
        return executeUpdate(sql, fullname, email, phone, userId);
    }

    /**
     * Đổi mật khẩu người dùng (Băm BCrypt mật khẩu mới).
     */
    public boolean updatePassword(int userId, String newRawPassword) {
        String hashedPassword = BCryptUtil.hashPassword(newRawPassword);
        String sql = "UPDATE Users SET password = ? WHERE id = ?";
        return executeUpdate(sql, hashedPassword, userId);
    }

    /**
     * Tìm người dùng theo Số điện thoại.
     * Dùng cho Booking tại quầy (Walk-in): kiểm tra bệnh nhân đã có tài khoản chưa.
     *
     * @param phone Số điện thoại cần tìm
     * @return Đối tượng User hoặc null nếu chưa có tài khoản
     */
    public User findByPhone(String phone) {
        if (phone == null || phone.trim().isEmpty())
            return null;
        String sql = "SELECT * FROM Users WHERE phone = ?";
        return queryOne(sql, this::mapResultSetToUser, phone.trim());
    }

    /**
     * Tạo mới tài khoản Bệnh nhân walk-in và trả về ID tự sinh.
     * Bảo đảm unique username bằng cách dùng số điện thoại làm username.
     *
     * @param user Đối tượng User cần tạo mới
     * @return ID người dùng mới tạo, hoặc -1 nếu thất bại
     */
    public int insertAndGetId(User user) {
        String sql = "INSERT INTO Users (username, password, email, fullname, phone, role, status)"
                + " VALUES(?, ?, ?, ?, ?, ?, 1)";

        String username = (user.getUsername() != null && !user.getUsername().isEmpty())
                ? user.getUsername()
                : "walkin_" + user.getPhone();
        String rawPass = (user.getPassword() != null && !user.getPassword().isEmpty())
                ? user.getPassword()
                : "WALKIN_" + System.currentTimeMillis();
        String hashedPassword = BCryptUtil.hashPassword(rawPass);

        try {
            return executeInsertAndGetGeneratedKey(sql, username, hashedPassword, user.getEmail(), user.getFullname(),
                    user.getPhone(), user.getRole() != null ? user.getRole() : "PATIENT");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "insertAndGetId error: Không thể tạo user mới", e);
            return -1;
        }
    }
}
