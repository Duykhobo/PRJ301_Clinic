package service;

import dao.UserDAO;
import model.User;

/**
 * UserService - Tầng Service Quản lý Người dùng & Xác thực (Business Logic).
 * Mô hình Enterprise 3-Tier (Servlet -> Service -> DAO).
 */
public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Xác thực Đăng nhập & Check Mật khẩu băm BCrypt qua UserDAO.
     *
     * @param username Tên đăng nhập
     * @param password Mật khẩu thô
     * @return Đối tượng User nếu hợp lệ, ngược lại trả về null
     */
    public User login(String username, String password) {
        return userDAO.login(username, password);
    }

    /**
     * Đăng ký tài khoản Bệnh nhân mới vào CSDL.
     *
     * @param user Đối tượng User chứa thông tin đăng ký
     * @return true nếu đăng ký thành công
     */
    public boolean registerPatient(User user) {
        return userDAO.register(user);
    }

    /**
     * Kiểm tra xem Username đã tồn tại hay chưa.
     */
    public boolean existsByUsername(String username) {
        return userDAO.existsByUsername(username);
    }

    /**
     * Kiểm tra xem Email đã tồn tại hay chưa.
     */
    public boolean existsByEmail(String email) {
        return userDAO.existsByEmail(email);
    }

    /**
     * Tìm thông tin Người dùng theo địa chỉ Email.
     */
    public User findByEmail(String email) {
        return userDAO.findByEmail(email);
    }

    /**
     * Đặt lại mật khẩu mới cho người dùng qua Email.
     */
    public boolean resetPasswordByEmail(int userId, String newRawPassword) {
        return userDAO.updatePassword(userId, newRawPassword);
    }
}
