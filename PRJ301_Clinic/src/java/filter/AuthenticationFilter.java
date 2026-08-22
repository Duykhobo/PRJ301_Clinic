package filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import constant.RoleConstant;
import constant.RouterConstant;
import constant.SystemConstant;
import dao.UserDAO;
import model.User;

/**
 * AuthenticationFilter - Bộ lọc Kiểm tra Đăng nhập & Phân quyền Truy cập (Session Guard).
 * Phân quyền 4 Roles: ADMIN, DOCTOR, PATIENT, RECEPTIONIST.
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = { "/*" })
public class AuthenticationFilter implements Filter {

    private final UserDAO userDAO = new UserDAO();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String uri = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String relativePath = uri.substring(contextPath.length());

        // 1. NẾU TRUY CẬP VÀO TRANG HOME/GỐC MÀ LÀ ADMIN/DOCTOR/RECEPTIONIST -> ĐẨY THẲNG VÀO DASHBOARD
        User user = (session != null) ? (User) session.getAttribute(SystemConstant.SESSION_USER) : null;
        if (user != null && isHomeOrRootPath(httpRequest, relativePath)) {
            String role = user.getRole();
            if (RoleConstant.ADMIN.equalsIgnoreCase(role)) {
                httpResponse.sendRedirect(contextPath + RouterConstant.DASHBOARD_ADMIN);
                return;
            } else if (RoleConstant.DOCTOR.equalsIgnoreCase(role)) {
                httpResponse.sendRedirect(contextPath + RouterConstant.DASHBOARD_DOCTOR);
                return;
            } else if (RoleConstant.RECEPTIONIST.equalsIgnoreCase(role)) {
                httpResponse.sendRedirect(contextPath + RouterConstant.DASHBOARD_RECEPTIONIST);
                return;
            }
        }

        // 2. NẾU KHÔNG PHẢI VÙNG NỘI BỘ BẢO VỆ -> MẶC ĐỊNH CHO QUA LUÔN (CHO GUEST VÀ PATIENT)
        if (!isProtectedUri(relativePath)) {
            chain.doFilter(request, response);
            return;
        }

        // 3. NẾU LÀ VÙNG NỘI BỘ -> KIỂM TRA ĐĂNG NHẬP (SESSION & CSDL)
        if (user == null || userDAO.findById(user.getId()) == null) {
            if (session != null) {
                session.invalidate();
            }
            httpResponse.sendRedirect(contextPath + RouterConstant.ROUTE_LOGIN + "?redirect=" + relativePath);
            return;
        }
        // 3. PHÂN QUYỀN TRUY CẬP THEO ROLE (RBAC)
        String role = user.getRole();

        if (relativePath.startsWith("/admin") && !RoleConstant.ADMIN.equalsIgnoreCase(role)) {
            httpRequest.getRequestDispatcher(RouterConstant.ERROR_403_JSP).forward(httpRequest, httpResponse);
            return;
        }

        if (relativePath.startsWith("/doctor/") && !RoleConstant.DOCTOR.equalsIgnoreCase(role)) {
            httpRequest.getRequestDispatcher(RouterConstant.ERROR_403_JSP).forward(httpRequest, httpResponse);
            return;
        }
        if (relativePath.startsWith("/patient/") && !RoleConstant.PATIENT.equalsIgnoreCase(role)
                && !RoleConstant.ADMIN.equalsIgnoreCase(role)) {
            httpRequest.getRequestDispatcher(RouterConstant.ERROR_403_JSP).forward(httpRequest, httpResponse);
            return;
        }
        if (relativePath.startsWith("/receptionist/") && !RoleConstant.RECEPTIONIST.equalsIgnoreCase(role)
                && !RoleConstant.ADMIN.equalsIgnoreCase(role)) {
            httpRequest.getRequestDispatcher(RouterConstant.ERROR_403_JSP).forward(httpRequest, httpResponse);
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * Hàm helper chỉ kiểm tra xem đường dẫn có thuộc 4 vùng bảo vệ nội bộ hay
     * không.
     */
    private boolean isProtectedUri(String path) {
        return path.startsWith("/admin")
                || path.startsWith("/doctor")
                || path.startsWith("/patient")
                || path.startsWith("/receptionist")
                || path.startsWith("/booking")
                || path.startsWith("/history");
    }

    /**
     * Hàm helper kiểm tra đường dẫn có phải trang chủ / gốc hay không.
     */
    private boolean isHomeOrRootPath(HttpServletRequest request, String path) {
        String action = request.getParameter("action");
        if (action != null && !action.trim().isEmpty() && !"home".equalsIgnoreCase(action)) {
            return false;
        }
        return path == null || path.isEmpty() || "/".equals(path) || "/index.jsp".equals(path)
                || "/home".equals(path) || "/main".equals(path) || "/MainController".equals(path);
    }

    @Override
    public void destroy() {
    }
}
