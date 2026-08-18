package controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
import model.User;
import service.UserService;
import util.ValidationUtil;

/**
 * LoginServlet - Điều hướng & Xử lý Đăng nhập Người dùng (/login).
 * Tách biệt lỗi chi tiết cho từng field, dùng toán tử 3 ngôi tinh gọn.
 */
@WebServlet(name = "LoginServlet", urlPatterns = { "/login" })
public class LoginServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        this.userService = new UserService();
    }

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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username") != null ? request.getParameter("username").trim() : "";
        String password = request.getParameter("password") != null ? request.getParameter("password") : "";
        String redirect = request.getParameter("redirect");

        request.setAttribute("username", username);
        Map<String, String> errors = new HashMap<>();

        ValidationUtil.validateField(errors, "username", !username.isEmpty(), "Vui lòng nhập tên đăng nhập!");
        ValidationUtil.validateField(errors, "password", !password.isEmpty(), "Vui lòng nhập mật khẩu!");

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, "Vui lòng điền đầy đủ tên đăng nhập và mật khẩu!");
            request.getRequestDispatcher(RouterConstant.LOGIN_JSP).forward(request, response);
            return;
        }

        User user = userService.login(username, password);
        if (user == null) {
            errors.put("general", MessageConstant.ERR_LOGIN_FAILED);
            request.setAttribute("errors", errors);
            request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, MessageConstant.ERR_LOGIN_FAILED);
            request.getRequestDispatcher(RouterConstant.LOGIN_JSP).forward(request, response);
            return;
        }

        HttpSession session = request.getSession();
        session.setAttribute(SystemConstant.SESSION_USER, user);

        boolean hasRedirect = redirect != null && !redirect.trim().isEmpty() && !redirect.contains("/login");
        if (hasRedirect) {
            response.sendRedirect(request.getContextPath() + redirect);
        } else {
            redirectByRole(response, request.getContextPath(), user.getRole());
        }
    }

    private void redirectByRole(HttpServletResponse response, String contextPath, String role) throws IOException {
        String target = RoleConstant.ADMIN.equalsIgnoreCase(role) ? RouterConstant.DASHBOARD_ADMIN :
                        RoleConstant.DOCTOR.equalsIgnoreCase(role) ? RouterConstant.DASHBOARD_DOCTOR :
                        RoleConstant.RECEPTIONIST.equalsIgnoreCase(role) ? RouterConstant.DASHBOARD_RECEPTIONIST :
                        RouterConstant.ROUTE_HOME;
        response.sendRedirect(contextPath + target);
    }
}
