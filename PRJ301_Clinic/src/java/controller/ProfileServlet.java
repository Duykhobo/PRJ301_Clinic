package controller;

import java.io.IOException;
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
 */
@WebServlet(name = "ProfileServlet", urlPatterns = {"/profile"})
public class ProfileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User loginUser = (User) request.getSession().getAttribute(SystemConstant.LOGIN_USER_SESSION);
        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/MainController?action=login-page");
            return;
        }

        // Tải thông tin mới nhất từ CSDL
        User freshUser = userDAO.findById(loginUser.getId());
        if (freshUser != null) {
            request.getSession().setAttribute(SystemConstant.LOGIN_USER_SESSION, freshUser);
            request.setAttribute("user", freshUser);
        } else {
            request.setAttribute("user", loginUser);
        }

        request.getRequestDispatcher("/WEB-INF/views/user/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        User loginUser = (User) request.getSession().getAttribute(SystemConstant.LOGIN_USER_SESSION);
        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/MainController?action=login-page");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) action = "update-profile";

        if ("change-password".equals(action)) {
            handleChangePassword(request, response, loginUser);
        } else {
            handleUpdateProfile(request, response, loginUser);
        }
    }

    private void handleUpdateProfile(HttpServletRequest request, HttpServletResponse response, User loginUser)
            throws ServletException, IOException {
        String fullname = request.getParameter("fullname");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");

        if (!ValidationUtil.isFullnameValid(fullname)) {
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, "Họ và tên không hợp lệ (độ dài 2-100 ký tự)!");
            doGet(request, response);
            return;
        }

        if (!ValidationUtil.isEmailValid(email)) {
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, "Địa chỉ Email không đúng định dạng!");
            doGet(request, response);
            return;
        }

        if (!ValidationUtil.isPhoneValid(phone)) {
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, "Số điện thoại không hợp lệ (độ dài 10-11 chữ số)!");
            doGet(request, response);
            return;
        }

        boolean updated = userDAO.updateProfile(loginUser.getId(), fullname.trim(), email.trim(), phone.trim());
        if (updated) {
            User freshUser = userDAO.findById(loginUser.getId());
            request.getSession().setAttribute(SystemConstant.LOGIN_USER_SESSION, freshUser);
            request.setAttribute(SystemConstant.SUCCESS_MESSAGE_ATTR, "Cập nhật hồ sơ cá nhân thành công!");
        } else {
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, "Cập nhật hồ sơ thất bại, vui lòng thử lại!");
        }

        doGet(request, response);
    }

    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response, User loginUser)
            throws ServletException, IOException {
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        User freshUser = userDAO.findById(loginUser.getId());
        if (freshUser == null || !BCryptUtil.checkPassword(oldPassword, freshUser.getPassword())) {
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, "Mật khẩu hiện tại không chính xác!");
            request.setAttribute("activeTab", "password");
            doGet(request, response);
            return;
        }

        if (newPassword == null || newPassword.trim().length() < 6) {
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, "Mật khẩu mới phải có ít nhất 6 ký tự!");
            request.setAttribute("activeTab", "password");
            doGet(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, "Mật khẩu xác nhận không trùng khớp!");
            request.setAttribute("activeTab", "password");
            doGet(request, response);
            return;
        }

        boolean updated = userDAO.updatePassword(loginUser.getId(), newPassword.trim());
        if (updated) {
            request.setAttribute(SystemConstant.SUCCESS_MESSAGE_ATTR, "Đổi mật khẩu thành công! Vui lòng sử dụng mật khẩu mới cho các lần đăng nhập tiếp theo.");
        } else {
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, "Đổi mật khẩu thất bại!");
        }

        request.setAttribute("activeTab", "password");
        doGet(request, response);
    }
}
