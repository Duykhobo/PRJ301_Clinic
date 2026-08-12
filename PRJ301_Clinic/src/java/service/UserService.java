package service;

import dao.UserDAO;
import model.User;

/**
 * TODO: UserService - Tầng Service Quản lý Người dùng & Xác thực (Business Logic).
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
     * TODO: Xác thực Đăng nhập & Check Mật khẩu băm BCrypt.
     */
    public User login(String username, String password) {
        // =====================================================================
        // TODO: BẠN TỰ THỰC HÀNH GÕ LOGIC SERVICE TẠI ĐÂY!
        // Gợi ý: return userDAO.login(username, password);
        // =====================================================================
        return userDAO.login(username, password);
    }

    /**
     * TODO: Đăng ký Bệnh nhân mới.
     */
    public boolean registerPatient(User user) {
        // =====================================================================
        // TODO: BẠN TỰ THỰC HÀNH GÕ LOGIC SERVICE TẠI ĐÂY!
        // Gợi ý: return userDAO.register(user);
        // =====================================================================
        return userDAO.register(user);
    }

    public boolean existsByUsername(String username) {
        return userDAO.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userDAO.existsByEmail(email);
    }
}
