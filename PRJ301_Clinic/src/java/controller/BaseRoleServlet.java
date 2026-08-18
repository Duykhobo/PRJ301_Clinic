package controller;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import constant.RouterConstant;
import constant.SystemConstant;
import model.User;

/**
 * BaseRoleServlet — Lớp trừu tượng cung cấp các Helper tái sử dụng cho mọi
 * Servlet.
 */
public abstract class BaseRoleServlet extends HttpServlet {

    /**
     * @param request  HTTP request
     * @param response HTTP response
     * @param roles    Danh sách role được phép truy cập (ít nhất 1 role)
     * @return User đã đăng nhập nếu hợp lệ, {@code null} nếu đã redirect
     * @throws IOException nếu redirect thất bại
     */
    protected User requireRole(HttpServletRequest request, HttpServletResponse response,
            String... roles) throws IOException {
        HttpSession session = request.getSession(false);

        User user = (session != null) ? (User) session.getAttribute(SystemConstant.SESSION_USER) : null;
        ;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + RouterConstant.ROUTE_LOGIN);
        }

        for (String role : roles) {
            if (role.equalsIgnoreCase(user.getRole()))
                return user;
        }

        response.sendRedirect(request.getContextPath() + RouterConstant.ROUTE_LOGIN);
        return null;
    }

    /**
     * @param request HTTP request
     * @return true nếu là AJAX request
     */
    protected boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))
                || "true".equalsIgnoreCase(request.getParameter("ajax"));
    }

    /**
     *
     * @param response HTTP response
     * @param json     Chuỗi JSON cần ghi
     * @throws IOException nếu ghi thất bại
     */
    protected void writeJson(HttpServletResponse response, String json) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(json);
    }

    /**
     *
     * @param request HTTP request
     * @param message Nội dung thông báo thành công
     */
    protected void setSuccess(HttpServletRequest request, String message) {
        request.getSession().setAttribute(SystemConstant.SUCCESS_MESSAGE_ATTR, message);
    }

    /**
     *
     * @param request HTTP request
     * @param message Nội dung thông báo lỗi
     */
    protected void setError(HttpServletRequest request, String message) {
        request.getSession().setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, message);
    }

    /**
     *
     * @param request   HTTP request
     * @param paramName Tên query parameter (ví dụ: "page", "pageUser")
     * @return Số trang hợp lệ (tối thiểu là 1)
     */
    protected int parsePage(HttpServletRequest request, String paramName) {
        try {
            String val = request.getParameter(paramName);
            return (val != null) ? Math.max(1, Integer.parseInt(val)) : 1;
        } catch (NumberFormatException e) {
            return 1;
        }
    }
}
