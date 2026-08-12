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
 * SƯỜN MÃ NGUỒN USERDAO (BẠN TỰ TAY THỰC HÀNH CÁC TODO BÊN DƯỚI)
 */
public class UserDAO {

    /**
     * TODO 1: Viết phương thức đăng nhập login(String username, String rawPassword)
     * Flow thực hiện:
     * 1. Viết SQL: "SELECT * FROM Users WHERE username = ?"
     * 2. Mở try-with-resources cho Connection và PreparedStatement.
     * 3. Thực thi query và đọc ResultSet.
     * 4. Kiểm tra user.isStatus() == true.
     * 5. Dùng BCryptUtil.checkPassword(rawPassword, user.getPassword()) để xác thực.
     */
    public User login(String username, String rawPassword) {
        // TODO: Bạn tự gõ code tại đây
        return null;
    }

    /**
     * TODO 2: Viết phương thức đăng ký register(User user)
     * Flow thực hiện:
     * 1. Mã hóa mật khẩu thô: String hashed = BCryptUtil.hashPassword(user.getPassword());
     * 2. Viết SQL: "INSERT INTO Users (username, password, email, fullname, phone, role, status) VALUES (?, ?, ?, ?, ?, ?, ?)"
     * 3. Mở try-with-resources, set các tham số 1..7.
     * 4. Chạy executeUpdate() > 0 trả về true.
     */
    public boolean register(User user) {
        // TODO: Bạn tự gõ code tại đây
        return false;
    }

    /**
     * TODO 3: Viết phương thức kiểm tra trùng username existsByUsername(String username)
     */
    public boolean existsByUsername(String username) {
        // TODO: Bạn tự gõ code tại đây
        return false;
    }

    /**
     * TODO 4: Viết phương thức kiểm tra trùng email existsByEmail(String email)
     */
    public boolean existsByEmail(String email) {
        // TODO: Bạn tự gõ code tại đây
        return false;
    }
}
