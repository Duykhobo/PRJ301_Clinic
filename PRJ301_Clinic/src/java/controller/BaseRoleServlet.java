package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import constant.RouterConstant;
import constant.SystemConstant;
import model.User;
import util.AppUtils;

/**
 * BaseRoleServlet — Lớp trừu tượng cung cấp các Helper tái sử dụng cho mọi Servlet trong hệ thống.
 * Tích hợp cơ chế Flash Session Messages (chống kẹt thông báo khi F5) và DRY Request Utilities.
 */
public abstract class BaseRoleServlet extends HttpServlet {

    /**
     * Xác thực phiên đăng nhập và phân quyền đa vai trò.
     * Tự động tiêu thụ Flash Messages từ Session sang Request khi xác thực thành công.
     *
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

        if (user == null) {
            response.sendRedirect(request.getContextPath() + RouterConstant.ROUTE_LOGIN);
            return null;
        }

        for (String role : roles) {
            if (role != null && role.equalsIgnoreCase(user.getRole())) {
                consumeFlashMessages(request);
                return user;
            }
        }

        response.sendRedirect(request.getContextPath() + RouterConstant.ROUTE_LOGIN);
        return null;
    }

    /**
     * Tự động chuyển Flash Messages (thông báo 1 lần) từ Session sang Request Attribute và xóa khỏi Session.
     * Ngăn chặn triệt để tình trạng thông báo bị "kẹt" hiển thị mãi khi người dùng nhấn F5/Reload trang.
     *
     * @param request HTTP request
     */
    protected void consumeFlashMessages(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object successMsg = session.getAttribute(SystemConstant.SUCCESS_MESSAGE_ATTR);
            if (successMsg != null) {
                request.setAttribute(SystemConstant.SUCCESS_MESSAGE_ATTR, successMsg);
                session.removeAttribute(SystemConstant.SUCCESS_MESSAGE_ATTR);
            }
            Object errorMsg = session.getAttribute(SystemConstant.ERROR_MESSAGE_ATTR);
            if (errorMsg != null) {
                request.setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, errorMsg);
                session.removeAttribute(SystemConstant.ERROR_MESSAGE_ATTR);
            }
        }
    }

    /**
     * Helper chuyển tiếp an toàn (Forward) sang View JSP.
     * Tự động tiêu thụ Flash Messages trước khi forward.
     */
    protected void forward(HttpServletRequest request, HttpServletResponse response, String jspPath)
            throws ServletException, IOException {
        consumeFlashMessages(request);
        request.getRequestDispatcher(jspPath).forward(request, response);
    }

    /**
     * Helper chuyển hướng (Redirect) an toàn.
     */
    protected void redirect(HttpServletRequest request, HttpServletResponse response, String url)
            throws IOException {
        response.sendRedirect(request.getContextPath() + url);
    }

    /**
     * Kiểm tra xem request hiện tại có phải là AJAX hay không.
     */
    protected boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))
                || "true".equalsIgnoreCase(request.getParameter("ajax"));
    }

    /**
     * Ghi dữ liệu chuỗi JSON về Client.
     */
    protected void writeJson(HttpServletResponse response, String json) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(json != null ? json : "{}");
    }

    /**
     * Lưu thông báo thành công dạng Flash vào Session (hiển thị 1 lần sau redirect).
     */
    protected void setSuccess(HttpServletRequest request, String message) {
        request.getSession().setAttribute(SystemConstant.SUCCESS_MESSAGE_ATTR, message);
    }

    /**
     * Lưu thông báo lỗi dạng Flash vào Session (hiển thị 1 lần sau redirect).
     */
    protected void setError(HttpServletRequest request, String message) {
        request.getSession().setAttribute(SystemConstant.ERROR_MESSAGE_ATTR, message);
    }

    // =========================================================================
    // 🧱 DRY PARAMETER PARSING HELPERS (Null-Safe & Type-Safe)
    // =========================================================================

    protected String getParam(HttpServletRequest request, String paramName, String defaultVal) {
        return AppUtils.getParam(request, paramName, defaultVal);
    }

    protected String getParam(HttpServletRequest request, String paramName) {
        return AppUtils.getParam(request, paramName, "");
    }

    protected int getInt(HttpServletRequest request, String paramName, int defaultVal) {
        return AppUtils.getInt(request, paramName, defaultVal);
    }

    protected double getDouble(HttpServletRequest request, String paramName, double defaultVal) {
        return AppUtils.getDouble(request, paramName, defaultVal);
    }

    protected boolean getBoolean(HttpServletRequest request, String paramName, boolean defaultVal) {
        return AppUtils.getBoolean(request, paramName, defaultVal);
    }

    /**
     * Parse số trang phân trang an toàn (mặc định tối thiểu là trang 1).
     */
    protected int parsePage(HttpServletRequest request, String paramName) {
        return getInt(request, paramName, 1);
    }
}
