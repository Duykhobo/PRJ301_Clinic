package controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.SystemConstant;
import dao.UserDAO;
import model.User;
import util.BCryptUtil;
import util.ValidationUtil;

/**
 * ProfileServlet - Quản lý Chỉnh sửa Hồ sơ Cá nhân và Đổi mật khẩu cho Người Dùng.
 * Tách biệt lỗi chi tiết cho từng field, dùng toán tử 3 ngôi tinh gọn.
 */
@WebServlet(name = "ProfileServlet", urlPatterns = {"/profile"})
public class ProfileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final dao.LoyaltyDAO loyaltyDAO = new dao.LoyaltyDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User loginUser = (User) request.getSession().getAttribute(SystemConstant.SESSION_USER);
        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/MainController?action=login-page");
            return;
        }

        User freshUser = userDAO.findById(loginUser.getId());
        request.setAttribute("user", freshUser != null ? freshUser : loginUser);
        if (freshUser != null) {
            request.getSession().setAttribute(SystemConstant.SESSION_USER, freshUser);
            if (constant.RoleConstant.PATIENT.equals(freshUser.getRole())) {
                request.setAttribute("loyaltyProfile", loyaltyDAO.getLoyaltyProfileByPatient(freshUser.getId()));
            }
        }

        request.getRequestDispatcher("/WEB-INF/views/user/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        User loginUser = (User) request.getSession().getAttribute(SystemConstant.SESSION_USER);
        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/MainController?action=login-page");
            return;
        }

        String action = request.getParameter("action") != null ? request.getParameter("action") : "update-profile";
        if ("change-password".equals(action)) {
            handleChangePassword(request, response, loginUser);
        } else {
            handleUpdateProfile(request, response, loginUser);
        }
    }

    private void handleUpdateProfile(HttpServletRequest request, HttpServletResponse response, User loginUser)
            throws ServletException, IOException {
        String fullname = request.getParameter("fullname") != null ? request.getParameter("fullname").trim() : "";
        String email = request.getParameter("email") != null ? request.getParameter("email").trim() : "";
        String phone = request.getParameter("phone") != null ? request.getParameter("phone").trim() : "";

        Map<String, String> errors = new HashMap<>();
        ValidationUtil.validateField(errors, "fullname", ValidationUtil.isValidFullname(fullname), "Họ và tên không hợp lệ (độ dài 2-100 ký tự)!");
        ValidationUtil.validateField(errors, "email", ValidationUtil.isValidEmail(email), "Địa chỉ Email không đúng định dạng!");
        ValidationUtil.validateField(errors, "phone", ValidationUtil.isValidPhone(phone), "Số điện thoại không hợp lệ (10 chữ số)!");

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, "Vui lòng kiểm tra lại thông tin hồ sơ!");
            doGet(request, response);
            return;
        }

        boolean updated = userDAO.updateProfile(loginUser.getId(), fullname, email, phone);
        request.setAttribute(updated ? SystemConstant.SUCCESS_MESSAGE_ATTR : SystemConstant.ERROR_MESSAGE_ATTR,
                             updated ? "Cập nhật hồ sơ cá nhân thành công!" : "Cập nhật hồ sơ thất bại, vui lòng thử lại!");
        doGet(request, response);
    }

    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response, User loginUser)
            throws ServletException, IOException {
        String oldPassword = request.getParameter("oldPassword") != null ? request.getParameter("oldPassword") : "";
        String newPassword = request.getParameter("newPassword") != null ? request.getParameter("newPassword") : "";
        String confirmPassword = request.getParameter("confirmPassword") != null ? request.getParameter("confirmPassword") : "";

        Map<String, String> errors = new HashMap<>();
        User freshUser = userDAO.findById(loginUser.getId());

        boolean isOldPassValid = freshUser != null && BCryptUtil.checkPassword(oldPassword, freshUser.getPassword());
        ValidationUtil.validateField(errors, "oldPassword", isOldPassValid, "Mật khẩu hiện tại không chính xác!");
        ValidationUtil.validateField(errors, "newPassword", ValidationUtil.isValidPassword(newPassword), "Mật khẩu mới phải có ít nhất 6 ký tự!");
        ValidationUtil.validateField(errors, "confirmPassword", newPassword.equals(confirmPassword) && !confirmPassword.isEmpty(), "Mật khẩu xác nhận không trùng khớp!");

        request.setAttribute("activeTab", "password");
        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, "Vui lòng kiểm tra lại thông tin đổi mật khẩu!");
            doGet(request, response);
            return;
        }

        boolean updated = userDAO.updatePassword(loginUser.getId(), newPassword.trim());
        request.setAttribute(updated ? SystemConstant.SUCCESS_MESSAGE_ATTR : SystemConstant.ERROR_MESSAGE_ATTR,
                             updated ? "Đổi mật khẩu thành công! Vui lòng sử dụng mật khẩu mới cho các lần đăng nhập tiếp theo." : "Đổi mật khẩu thất bại!");
        doGet(request, response);
    }
}
