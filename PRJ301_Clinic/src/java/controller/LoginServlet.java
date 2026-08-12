package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import constant.MessageConstant;
import constant.RoleConstant;
import constant.RouterConstant;
import constant.SystemConstant;
import dao.UserDAO;
import model.User;
import util.ValidationUtil;

/**
 * TODO: LoginServlet - Điều hướng & Xử lý Đăng nhập Người dùng (/login).
 */
@WebServlet(name = "LoginServlet", urlPatterns = { "/login" })
public class LoginServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        this.userDAO = new UserDAO();
    }

    /**
     * TODO 1: Hiển thị trang Đăng nhập (GET)
     * Gợi ý:
     * 1. Check xem đã đăng nhập chưa (session != null && SESSION_USER != null)
     * 2. Nếu đã đăng nhập -> chuyển hướng về trang tương ứng theo Role
     * 3. Nếu chưa -> forward tới RouterConstant.LOGIN_JSP
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(SystemConstant.SESSION_USER) != null) {
            User user = (User) session.getAttribute(SystemConstant.SESSION_USER);
            redirectByRole(response, request.getContextPath(), user.getRole());
            return;
        }

        request.getRequestDispatcher(RouterConstant.LOGIN_JSP).forward(request, response);
    }

    /**
     * TODO 2: Xử lý Đăng nhập khi bấm Submit Form (POST)
     * Quy trình 4 bước:
     * Bước 1: Lấy thông tin username, password từ request.getParameter()
     * Bước 2: Fail-fast Validation dùng ValidationUtil.isValidUsername(), check
     * null password
     * Bước 3: Gọi userDAO.login(username, password)
     * Bước 4: Nếu user != null -> lưu session.setAttribute(SESSION_USER, user),
     * chuyển hướng theo Role
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String redirect = request.getParameter("redirect");

        // 1. Fail-fast Validation
        if (!ValidationUtil.isValidUsername(username)) {
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, MessageConstant.ERR_INVALID_USERNAME);
            request.setAttribute("username", username);
            request.getRequestDispatcher(RouterConstant.LOGIN_JSP).forward(request, response);
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, "Vui lòng nhập mật khẩu!");
            request.setAttribute("username", username);
            request.getRequestDispatcher(RouterConstant.LOGIN_JSP).forward(request, response);
            return;
        }

        // 2. Xác thực tài khoản CSDL
        User user = userDAO.login(username.trim(), password);
        if (user == null) {
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, MessageConstant.ERR_LOGIN_FAILED);
            request.setAttribute("username", username);
            request.getRequestDispatcher(RouterConstant.LOGIN_JSP).forward(request, response);
            return;
        }

        // 3. Đăng nhập thành công -> Lưu Session
        HttpSession session = request.getSession();
        session.setAttribute(SystemConstant.SESSION_USER, user);

        // 4. Chuyển hướng theo Role hoặc theo param redirect
        if (redirect != null && !redirect.trim().isEmpty() && !redirect.contains("/login")) {
            response.sendRedirect(request.getContextPath() + redirect);
        } else {
            redirectByRole(response, request.getContextPath(), user.getRole());
        }
    }

    private void redirectByRole(HttpServletResponse response, String contextPath, String role) throws IOException {
        if (RoleConstant.ADMIN.equalsIgnoreCase(role)) {
            response.sendRedirect(contextPath + RouterConstant.DASHBOARD_ADMIN);
        } else if (RoleConstant.DOCTOR.equalsIgnoreCase(role)) {
            response.sendRedirect(contextPath + RouterConstant.DASHBOARD_DOCTOR);
        } else if (RoleConstant.RECEPTIONIST.equalsIgnoreCase(role)) {
            response.sendRedirect(contextPath + RouterConstant.DASHBOARD_RECEPTIONIST);
        } else {
            response.sendRedirect(contextPath + RouterConstant.ROUTE_HOME);
        }
    }
}
