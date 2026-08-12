package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.MessageConstant;
import constant.RoleConstant;
import constant.RouterConstant;
import constant.SystemConstant;
import dao.UserDAO;
import model.User;
import util.ValidationUtil;

/**
 * RegisterServlet - Điều hướng & Xử lý Đăng ký Tài khoản Bệnh nhân (/register).
 */
@WebServlet(name = "RegisterServlet", urlPatterns = { "/register" })
public class RegisterServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        this.userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(RouterConstant.REGISTER_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String fullname = request.getParameter("fullname");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");

        // Ghi nhớ dữ liệu form cũ để điền lại khi có lỗi
        request.setAttribute("username", username);
        request.setAttribute("fullname", fullname);
        request.setAttribute("phone", phone);
        request.setAttribute("email", email);

        // 1. Validation kiểm tra định dạng dữ liệu (Fail-fast)
        if (!ValidationUtil.isValidUsername(username)) {
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, MessageConstant.ERR_INVALID_USERNAME);
            request.getRequestDispatcher(RouterConstant.REGISTER_JSP).forward(request, response);
            return;
        }

        if (!ValidationUtil.isValidPassword(password)) {
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, MessageConstant.ERR_INVALID_PASSWORD);
            request.getRequestDispatcher(RouterConstant.REGISTER_JSP).forward(request, response);
            return;
        }

        if (password == null || !password.equals(confirmPassword)) {
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, MessageConstant.ERR_PASSWORD_MISMATCH);
            request.getRequestDispatcher(RouterConstant.REGISTER_JSP).forward(request, response);
            return;
        }

        if (!ValidationUtil.isValidFullname(fullname)) {
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, MessageConstant.ERR_INVALID_FULLNAME);
            request.getRequestDispatcher(RouterConstant.REGISTER_JSP).forward(request, response);
            return;
        }

        if (!ValidationUtil.isValidPhone(phone)) {
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, MessageConstant.ERR_INVALID_PHONE);
            request.getRequestDispatcher(RouterConstant.REGISTER_JSP).forward(request, response);
            return;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, MessageConstant.ERR_INVALID_EMAIL);
            request.getRequestDispatcher(RouterConstant.REGISTER_JSP).forward(request, response);
            return;
        }

        // 2. Kiểm tra trùng lặp trong CSDL
        if (userDAO.existsByUsername(username.trim())) {
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, MessageConstant.ERR_USERNAME_EXISTS);
            request.getRequestDispatcher(RouterConstant.REGISTER_JSP).forward(request, response);
            return;
        }

        if (email != null && !email.trim().isEmpty() && userDAO.existsByEmail(email.trim())) {
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, MessageConstant.ERR_EMAIL_EXISTS);
            request.getRequestDispatcher(RouterConstant.REGISTER_JSP).forward(request, response);
            return;
        }

        // 3. Khởi tạo đối tượng User mới (Role PATIENT)
        User newUser = new User();
        newUser.setUsername(username.trim());
        newUser.setPassword(password);
        newUser.setFullname(fullname.trim());
        newUser.setPhone(phone.trim());
        newUser.setEmail(email != null && !email.trim().isEmpty() ? email.trim() : null);
        newUser.setRole(RoleConstant.PATIENT);
        newUser.setStatus(true);

        // 4. Lưu vào CSDL
        boolean created = userDAO.register(newUser);

        if (created) {
            response.sendRedirect(request.getContextPath() + RouterConstant.ROUTE_LOGIN + "?registered=success");
        } else {
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR,
                    "Đã xảy ra lỗi trong quá trình tạo tài khoản. Vui lòng thử lại!");
            request.getRequestDispatcher(RouterConstant.REGISTER_JSP).forward(request, response);
        }
    }
}